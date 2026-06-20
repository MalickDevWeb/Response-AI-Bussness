package sn.autocloser.application.service;

import sn.autocloser.application.port.command.RouterMessageCommand;
import sn.autocloser.application.port.usecase.RouterMessageUseCase;
import sn.autocloser.domain.exception.CommercantIntrouvableException;
import sn.autocloser.domain.model.Commercant;
import sn.autocloser.domain.model.Conversation;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;
import sn.autocloser.domain.port.repository.ConversationRepositoryPort;
import sn.autocloser.domain.port.event.ConversationEventPublisherPort;
import sn.autocloser.domain.event.ConversationCreeeEvent;

import java.util.Optional;

/**
 * APPLICATION - Service de routage (Le Cœur du Mois 1)
 *
 * LOGIQUE PRINCIPALE :
 * 1. On identifie le commerçant à partir du canal entrant.
 * 2. Si l'expéditeur EST le commerçant → Mode Admin (gestion du catalogue).
 * 3. Si l'expéditeur EST un client → Mode Vente (IA commerciale).
 */
public class RouterMessageService implements RouterMessageUseCase {

    private final CommercantRepositoryPort commercantRepo;
    private final ConversationRepositoryPort conversationRepo;
    private final AdminCatalogueService adminCatalogueService;
    private final ConversationEventPublisherPort eventPublisher;
    private final sn.autocloser.domain.port.ia.RechercheRagPort rechercheRagPort;
    private final sn.autocloser.domain.port.ia.OllamaGatewayPort ollamaGatewayPort;
    private final sn.autocloser.domain.port.messagerie.EnvoiMessagePort envoiMessagePort;
    private final sn.autocloser.domain.port.repository.MessageRepositoryPort messageRepo;
    private final sn.autocloser.domain.port.ia.VisionGatewayPort visionGatewayPort;
    private final sn.autocloser.domain.port.repository.CommandeRepositoryPort commandeRepo;

    public RouterMessageService(
            CommercantRepositoryPort commercantRepo,
            ConversationRepositoryPort conversationRepo,
            AdminCatalogueService adminCatalogueService,
            ConversationEventPublisherPort eventPublisher,
            sn.autocloser.domain.port.ia.RechercheRagPort rechercheRagPort,
            sn.autocloser.domain.port.ia.OllamaGatewayPort ollamaGatewayPort,
            sn.autocloser.domain.port.messagerie.EnvoiMessagePort envoiMessagePort,
            sn.autocloser.domain.port.repository.MessageRepositoryPort messageRepo,
            sn.autocloser.domain.port.ia.VisionGatewayPort visionGatewayPort,
            sn.autocloser.domain.port.repository.CommandeRepositoryPort commandeRepo) {
        this.commercantRepo = commercantRepo;
        this.conversationRepo = conversationRepo;
        this.adminCatalogueService = adminCatalogueService;
        this.eventPublisher = eventPublisher;
        this.rechercheRagPort = rechercheRagPort;
        this.ollamaGatewayPort = ollamaGatewayPort;
        this.envoiMessagePort = envoiMessagePort;
        this.messageRepo = messageRepo;
        this.visionGatewayPort = visionGatewayPort;
        this.commandeRepo = commandeRepo;
    }

    @Override
    public String router(RouterMessageCommand cmd) {

        // 1. Charger le commerçant propriétaire du canal
        Commercant commercant = commercantRepo
                .trouverParTelephone(cmd.commercantTelephone())
                .orElseThrow(() -> new CommercantIntrouvableException(
                        "Aucun commerçant trouvé pour le numéro : " + cmd.commercantTelephone()
                ));

        // ============================================================
        // 2. ROUTAGE : Admin OU Client ?
        // ============================================================
        boolean estAdmin = cmd.expediteurId().equals(commercant.getTelephoneCommercant());

        if (estAdmin) {
            // → MODE ADMIN : Le commerçant modifie son catalogue
            return "[ADMIN] Commande détectée depuis " + cmd.expediteurId()
                    + " → Interprétation en cours : \"" + cmd.contenu() + "\"";
        }

        // → MODE VENTE : Un client envoie un message
        return traiterMessageClient(cmd, commercant);
    }

    private String traiterMessageClient(RouterMessageCommand cmd, Commercant commercant) {
        // Chercher ou créer la session de conversation
        Optional<Conversation> conversationExistante = conversationRepo
                .trouverParClientEtCommercant(cmd.expediteurId(), commercant.getId(), cmd.plateforme());

        Conversation conversation = conversationExistante.orElseGet(() -> {
            Conversation nouvelle = Conversation.creer(
                    commercant.getId(),
                    cmd.expediteurId(),
                    cmd.plateforme()
            );
            Conversation saved = conversationRepo.sauvegarder(nouvelle);
            
            // Dispatcher les événements de domaine
            nouvelle.getDomainEvents().forEach(event -> {
                if (event instanceof ConversationCreeeEvent e) {
                    eventPublisher.publierConversationCreee(e);
                }
            });
            nouvelle.clearDomainEvents();
            
            return saved;
        });

        // TÂCHE 3.4 : Traitement des images (Reçu de paiement)
        if ("image".equalsIgnoreCase(cmd.typeMedia()) && cmd.mediaUrl() != null) {
            return traiterImagePaiement(conversation, commercant, cmd);
        }

        // Si le message contient des indices d'achat (ex: "je veux acheter", "je paie"), 
        // on passe la conversation en attente de paiement pour le MVP.
        if (cmd.contenu() != null && cmd.contenu().toLowerCase().contains("acheter")) {
            if (conversation.getStatut() == sn.autocloser.domain.valueobject.StatutConversation.EN_COURS) {
                conversation.passerEnAttenteDePaiement();
                conversationRepo.sauvegarder(conversation);
            }
        }

        // Sauvegarder le message du client
        sn.autocloser.domain.model.Message msgClient = sn.autocloser.domain.model.Message.creer(conversation.getId(), "USER", cmd.contenu());
        messageRepo.sauvegarder(msgClient);

        // Recherche RAG des produits
        var resultatsRag = rechercheRagPort.rechercherProduitsProches(commercant.getId(), cmd.contenu(), 3);
        
        StringBuilder contexteProduits = new StringBuilder();
        if (resultatsRag.isEmpty()) {
            contexteProduits.append("Aucun produit ne correspond exactement à la demande.");
        } else {
            contexteProduits.append("Produits disponibles dans le catalogue :\n");
            for (var p : resultatsRag) {
                contexteProduits.append("- ").append(p.nom())
                        .append(" (").append(p.prix()).append(" FCFA) : ")
                        .append(p.description()).append("\n");
            }
        }

        // Récupérer l'historique de la conversation (les 5 derniers messages)
        var historique = messageRepo.recupererHistorique(conversation.getId(), 5);
        StringBuilder historiqueStr = new StringBuilder();
        for (var m : historique) {
            historiqueStr.append(m.getRole()).append(": ").append(m.getContenu()).append("\n");
        }

        String systemPrompt = "Tu es un assistant de vente nommé AutoCloser AI pour la boutique " + commercant.getNomBoutique() + ".\n"
                + "Ton but est d'accueillir le client, de l'informer sur les produits et de l'encourager à acheter.\n"
                + "Voici les informations du catalogue correspondant à la demande du client :\n"
                + contexteProduits.toString() + "\n"
                + "Voici l'historique récent de la conversation :\n"
                + historiqueStr.toString() + "\n"
                + "Réponds au dernier message du client de manière concise, naturelle et chaleureuse.";

        // Appel Ollama
        String reponseIa = ollamaGatewayPort.genererReponse(systemPrompt, cmd.contenu());

        // Sauvegarder la réponse de l'IA
        sn.autocloser.domain.model.Message msgAssistant = sn.autocloser.domain.model.Message.creer(conversation.getId(), "ASSISTANT", reponseIa);
        messageRepo.sauvegarder(msgAssistant);

        // TÂCHE 2.4 : Envoi de la réponse via Evolution API
        String instanceNom = commercant.getNomBoutique().replaceAll("\\s+", "").toLowerCase();
        envoiMessagePort.envoyerMessage(cmd.expediteurId(), reponseIa, instanceNom);

        return reponseIa;
    }

    private String traiterImagePaiement(Conversation conversation, Commercant commercant, sn.autocloser.application.port.command.RouterMessageCommand cmd) {
        if (conversation.getStatut() != sn.autocloser.domain.valueobject.StatutConversation.ATTENTE_PAIEMENT) {
            String msg = "J'ai bien reçu votre image, mais nous n'étions pas en attente de paiement. Si vous souhaitez acheter un produit, dites-le moi !";
            String instanceNom = commercant.getNomBoutique().replaceAll("\\s+", "").toLowerCase();
            envoiMessagePort.envoyerMessage(cmd.expediteurId(), msg, instanceNom);
            return msg;
        }

        // 1. Analyse du reçu via l'IA Vision
        String jsonResult = visionGatewayPort.extraireDonneesRecu(cmd.mediaUrl());
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonResult);
            
            Double montant = root.path("montant").asDouble();
            String numero = root.path("numero_destinataire").asText();
            
            // Pour le MVP, si le montant est > 0, on valide (en prod, on comparerait avec le panier)
            if (montant != null && montant > 0) {
                // Création de la commande
                sn.autocloser.domain.model.Commande commande = sn.autocloser.domain.model.Commande.creer(
                        commercant.getId(), cmd.expediteurId(), conversation.getId(), montant);
                commande.validerPaiement();
                commandeRepo.sauvegarder(commande);
                
                // Mise à jour de la conversation
                conversation.validerPaiement();
                conversationRepo.sauvegarder(conversation);
                
                String succesMsg = "✅ Paiement de " + montant + " FCFA validé ! Votre commande est confirmée. Merci pour votre achat !";
                String instanceNom = commercant.getNomBoutique().replaceAll("\\s+", "").toLowerCase();
                envoiMessagePort.envoyerMessage(cmd.expediteurId(), succesMsg, instanceNom);
                return succesMsg;
            }
        } catch (Exception e) {
            // Ignorer et passer à l'échec
        }
        
        String echecMsg = "❌ Je n'ai pas pu valider le reçu. Veuillez vérifier que l'image est nette et correspond bien au transfert.";
        String instanceNom = commercant.getNomBoutique().replaceAll("\\s+", "").toLowerCase();
        envoiMessagePort.envoyerMessage(cmd.expediteurId(), echecMsg, instanceNom);
        return echecMsg;
    }
}
