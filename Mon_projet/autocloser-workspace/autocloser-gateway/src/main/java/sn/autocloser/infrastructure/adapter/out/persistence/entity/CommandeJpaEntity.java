package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "commandes")
public class CommandeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "commercant_id", nullable = false)
    private UUID commercantId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @Column(nullable = false)
    private String statut;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CommandeJpaEntity() {}

    public CommandeJpaEntity(UUID id, UUID commercantId, String clientId, UUID conversationId, Double montantTotal, String statut, Instant createdAt) {
        this.id = id;
        this.commercantId = commercantId;
        this.clientId = clientId;
        this.conversationId = conversationId;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getCommercantId() { return commercantId; }
    public String getClientId() { return clientId; }
    public UUID getConversationId() { return conversationId; }
    public Double getMontantTotal() { return montantTotal; }
    public String getStatut() { return statut; }
    public Instant getCreatedAt() { return createdAt; }
}
