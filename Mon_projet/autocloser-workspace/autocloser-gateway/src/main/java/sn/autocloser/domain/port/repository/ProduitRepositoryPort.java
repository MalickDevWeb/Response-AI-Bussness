package sn.autocloser.domain.port.repository;

import sn.autocloser.domain.model.Produit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PORT SORTANT (Out) - Contrat de persistance du catalogue produits.
 */
public interface ProduitRepositoryPort {

    Produit sauvegarder(Produit produit);

    Optional<Produit> trouverParId(UUID id);

    List<Produit> trouverParCommercant(UUID commercantId);

    // Recherche sémantique RAG (pgvector) - retourne les N produits les plus proches
    List<Produit> rechercherParSimilarite(UUID commercantId, float[] embedding, int limite);

    void supprimer(UUID id);
}
