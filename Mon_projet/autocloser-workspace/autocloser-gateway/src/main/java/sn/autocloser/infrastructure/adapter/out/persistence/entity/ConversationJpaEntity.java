package sn.autocloser.infrastructure.adapter.out.persistence.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Entité JPA Conversation
 */
@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "commercant_id", nullable = false)
    private UUID commercantId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "plateforme", nullable = false)
    private String plateforme;

    @Column(name = "statut")
    private String statut;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
