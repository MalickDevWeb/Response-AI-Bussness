package sn.autocloser.application.service;

import sn.autocloser.domain.model.Commercant;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;
import sn.autocloser.domain.port.messagerie.GestionInstancePort;

import java.util.Optional;

/**
 * CAS D'UTILISATION - Inscription et Onboarding d'un Commerçant.
 * Orchestre la création en base et la configuration WhatsApp (Evolution API).
 */
public class OnboardingCommercantService {

    private final CommercantRepositoryPort commercantRepo;
    private final GestionInstancePort gestionInstancePort;

    public OnboardingCommercantService(CommercantRepositoryPort commercantRepo, GestionInstancePort gestionInstancePort) {
        this.commercantRepo = commercantRepo;
        this.gestionInstancePort = gestionInstancePort;
    }

    public record DemandeInscription(String nomBoutique, String telephone, String email, String domaine) {}

    public record ReponseInscription(String idCommercant, String qrCodeBase64, String message) {}

    /**
     * Inscrit le commerçant et lui génère un QR code WhatsApp.
     */
    public ReponseInscription inscrireCommercant(DemandeInscription demande) {
        // 1. Normalisation du numéro de téléphone — accepte avec ou sans "+"
        String telephone = demande.telephone().trim();
        if (!telephone.startsWith("+")) {
            telephone = "+" + telephone;
        }
        final String telFinal = telephone;

        // 2. Vérifier si le commerçant existe déjà
        Optional<Commercant> existant = commercantRepo.trouverParTelephone(telFinal);
        if (existant.isPresent()) {
            throw new IllegalArgumentException("Un commerçant avec ce numéro existe déjà.");
        }

        // 3. Créer l'entité Commercant pure (domaine validera le format "+")
        Commercant nouveauCommercant = Commercant.creer(
                demande.nomBoutique(),
                telFinal,
                demande.email()
        );
        nouveauCommercant.activerWhatsapp();
        Commercant saved = commercantRepo.sauvegarder(nouveauCommercant);

        // 4. Créer l'instance sur Evolution API et récupérer le QR
        String instanceNom = demande.nomBoutique().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String qrCodeBase64 = null;
        try {
            qrCodeBase64 = gestionInstancePort.creerInstanceEtGenererQr(instanceNom);
        } catch (Exception e) {
            // Si Evolution API échoue, on continue mais on notifie le frontend
        }

        return new ReponseInscription(
                saved.getId().toString(),
                qrCodeBase64,
                qrCodeBase64 != null
                        ? "Inscription réussie. Scannez le QR Code pour lier WhatsApp."
                        : "Inscription réussie ! Configurez Evolution API pour le QR Code."
        );
    }
}
