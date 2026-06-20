package sn.autocloser.domain.port.ia;

import java.util.List;
import java.util.UUID;

/**
 * PORT SORTANT (Out) - Recherche sémantique de produits via pgvector.
 * Le domaine définit le contrat, l'infrastructure l'implémente avec JPA + SQL natif.
 */
public interface RechercheRagPort {

    /**
     * Cherche les produits les plus proches sémantiquement d'une requête texte.
     *
     * @param commercantId UUID du commerçant (pour filtrer son catalogue)
     * @param requete      Texte de la requête client (ex: "Vous avez des robes noires ?")
     * @param topK         Nombre de résultats à retourner (généralement 3 à 5)
     * @return Liste de résultats RAG (nom, prix, score de similarité)
     */
    List<ResultatRag> rechercherProduitsProches(UUID commercantId, String requete, int topK);

    /**
     * Indexe un produit en générant et stockant son embedding dans la BDD.
     *
     * @param produitId UUID du produit à indexer
     * @param texteAPourIndexer Texte qui représente le produit (nom + description)
     */
    void indexerProduit(UUID produitId, String texteAPourIndexer);

    /**
     * Résultat d'une recherche RAG.
     */
    record ResultatRag(
            UUID produitId,
            String nom,
            String description,
            double prix,
            int stock,
            double scoreSimilarite  // 0.0 = pas similaire, 1.0 = identique
    ) {}
}
