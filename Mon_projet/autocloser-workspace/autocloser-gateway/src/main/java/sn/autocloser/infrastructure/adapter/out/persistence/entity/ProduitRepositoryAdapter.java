package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Produit;
import sn.autocloser.domain.port.repository.ProduitRepositoryPort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProduitRepositoryAdapter implements ProduitRepositoryPort {

    private final ProduitJpaRepository jpaRepository;

    public ProduitRepositoryAdapter(ProduitJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Produit sauvegarder(Produit produit) {
        ProduitJpaEntity entity = ProduitJpaEntity.builder()
                .id(produit.getId())
                .commercantId(produit.getCommercantId())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .prix(produit.getPrix())
                .stock(produit.getStock())
                .actif(produit.isActif())
                .imageUrl(produit.getImageUrl())
                .createdAt(Instant.now())
                .build();
        jpaRepository.save(entity);
        return produit;
    }

    @Override
    public Optional<Produit> trouverParId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Produit> trouverParCommercant(UUID commercantId) {
        return jpaRepository.findByCommercantId(commercantId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Produit> rechercherParSimilarite(UUID commercantId, float[] embedding, int limite) {
        // Mois 2 : intégration pgvector avec requête native SQL
        return trouverParCommercant(commercantId).stream().limit(limite).collect(Collectors.toList());
    }

    @Override
    public void supprimer(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Produit toDomain(ProduitJpaEntity e) {
        return Produit.creer(e.getCommercantId(), e.getNom(), e.getDescription(),
                e.getPrix(), e.getStock(), e.getImageUrl());
    }
}
