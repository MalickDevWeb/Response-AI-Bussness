package sn.autocloser.domain.model;

import sn.autocloser.domain.event.ConversationCreeeEvent;
import sn.autocloser.domain.event.PaiementValideEvent;
import sn.autocloser.domain.exception.TransitionStatutInvalideException;
import sn.autocloser.domain.valueobject.StatutConversation;
import sn.autocloser.domain.valueobject.Plateforme;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * DOMAINE - Agrégat Conversation
 * Représente une session de vente active entre un client et l'IA.
 * Équivalent de ProduitStock.java dans votre projet stock.
 * RÈGLE : Aucune annotation Spring/JPA ici. Le domaine est pur.
 */
public class Conversation {

    private final UUID id;
    private final UUID commercantId;
    private final String clientId; // Numéro WhatsApp ou ID Instagram du client
    private final Plateforme plateforme;
    private StatutConversation statut;
    private final Instant createdAt;
    private final List<Object> domainEvents = new ArrayList<>();

    private Conversation(UUID id, UUID commercantId, String clientId,
                         Plateforme plateforme, StatutConversation statut, Instant createdAt) {
        this.id = id;
        this.commercantId = commercantId;
        this.clientId = clientId;
        this.plateforme = plateforme;
        this.statut = statut;
        this.createdAt = createdAt;
    }

    // ---- FACTORY METHODS ----
    public static Conversation creer(UUID commercantId, String clientId, Plateforme plateforme) {
        var conversation = new Conversation(
                UUID.randomUUID(),
                commercantId,
                clientId,
                plateforme,
                StatutConversation.EN_COURS,
                Instant.now()
        );
        conversation.domainEvents.add(new ConversationCreeeEvent(conversation.id, commercantId, clientId));
        return conversation;
    }

    /** Reconstitution depuis la persistance (pas de nouvel event) */
    public static Conversation reconstituer(UUID id, UUID commercantId, String clientId,
                                            Plateforme plateforme, StatutConversation statut,
                                            java.time.Instant updatedAt) {
        return new Conversation(id, commercantId, clientId, plateforme, statut, updatedAt);
    }

    // ---- RÈGLES MÉTIER ----

    /**
     * L'IA a envoyé les infos de paiement → on attend la capture d'écran du client.
     */
    public void passerEnAttenteDePaiement() {
        if (this.statut != StatutConversation.EN_COURS) {
            throw new TransitionStatutInvalideException(
                    "Impossible de passer en attente de paiement depuis le statut : " + this.statut
            );
        }
        this.statut = StatutConversation.ATTENTE_PAIEMENT;
    }

    /**
     * L'IA Vision a validé le reçu de paiement → vente confirmée.
     */
    public void validerPaiement() {
        if (this.statut != StatutConversation.ATTENTE_PAIEMENT) {
            throw new TransitionStatutInvalideException(
                    "Impossible de valider le paiement depuis le statut : " + this.statut
            );
        }
        this.statut = StatutConversation.PAYE;
        this.domainEvents.add(new PaiementValideEvent(this.id, this.commercantId, this.clientId));
    }

    /**
     * Vérifie si le message vient du commerçant (Mode Admin) ou d'un client (Mode Vente).
     */
    public boolean estModeAdmin(String telephoneCommerçant) {
        return this.clientId.equals(telephoneCommerçant);
    }

    // ---- GETTERS ----
    public UUID getId() { return id; }
    public UUID getCommercantId() { return commercantId; }
    public String getClientId() { return clientId; }
    public Plateforme getPlateforme() { return plateforme; }
    public StatutConversation getStatut() { return statut; }
    public Instant getCreatedAt() { return createdAt; }
    public List<Object> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }
    public void clearDomainEvents() { domainEvents.clear(); }
}
