package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Entité JPA Commerçant (adaptateur de persistance)
 */
@Entity
@Table(name = "commercants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommercantJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom_boutique", nullable = false)
    private String nomBoutique;

    @Column(name = "telephone_commercant", unique = true, nullable = false)
    private String telephoneCommercant;

    @Column(name = "email_notification", nullable = false)
    private String emailNotification;

    @Column(name = "whatsapp_actif")
    private boolean whatsappActif;

    @Column(name = "instagram_actif")
    private boolean instagramActif;

    @Column(name = "tiktok_actif")
    private boolean tiktokActif;

    @Column(name = "pack_media_actif")
    private boolean packMediaActif;

    @Column(name = "created_at")
    private Instant createdAt;
}
