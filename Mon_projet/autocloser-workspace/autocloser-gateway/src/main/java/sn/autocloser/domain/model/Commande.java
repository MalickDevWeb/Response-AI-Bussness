package sn.autocloser.domain.model;

import sn.autocloser.domain.valueobject.StatutCommande;

import java.time.Instant;
import java.util.UUID;

/**
 * DOMAINE - Agrégat Commande
 * Représente un achat validé (ou en cours de validation) par le client.
 */
public class Commande {

    private final UUID id;
    private final UUID commercantId;
    private final String clientId;
    private final UUID conversationId;
    private final Double montantTotal;
    private StatutCommande statut;
    private final Instant createdAt;

    private Commande(UUID id, UUID commercantId, String clientId, UUID conversationId, Double montantTotal, StatutCommande statut, Instant createdAt) {
        this.id = id;
        this.commercantId = commercantId;
        this.clientId = clientId;
        this.conversationId = conversationId;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.createdAt = createdAt;
    }

    public static Commande creer(UUID commercantId, String clientId, UUID conversationId, Double montantTotal) {
        return new Commande(
                UUID.randomUUID(),
                commercantId,
                clientId,
                conversationId,
                montantTotal,
                StatutCommande.EN_ATTENTE_PAIEMENT,
                Instant.now()
        );
    }

    public static Commande reconstituer(UUID id, UUID commercantId, String clientId, UUID conversationId, Double montantTotal, StatutCommande statut, Instant createdAt) {
        return new Commande(id, commercantId, clientId, conversationId, montantTotal, statut, createdAt);
    }

    public void validerPaiement() {
        this.statut = StatutCommande.PAYEE;
    }

    public void annuler() {
        this.statut = StatutCommande.ANNULEE;
    }

    public UUID getId() { return id; }
    public UUID getCommercantId() { return commercantId; }
    public String getClientId() { return clientId; }
    public UUID getConversationId() { return conversationId; }
    public Double getMontantTotal() { return montantTotal; }
    public StatutCommande getStatut() { return statut; }
    public Instant getCreatedAt() { return createdAt; }
}
