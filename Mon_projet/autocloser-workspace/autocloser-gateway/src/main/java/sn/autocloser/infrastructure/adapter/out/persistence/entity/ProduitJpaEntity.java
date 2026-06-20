package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Entité JPA Produit
 * Note: le champ embedding (pgvector) est géré en String car
 * Spring Data JPA ne supporte pas encore nativement le type vector.
 * On l'hydrate manuellement via une requête native SQL.
 */
@Entity
@Table(name = "produits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "commercant_id", nullable = false)
    private UUID commercantId;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "prix", nullable = false)
    private BigDecimal prix;

    @Column(name = "stock")
    private int stock;

    @Column(name = "actif")
    private boolean actif;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private Instant createdAt;
}
