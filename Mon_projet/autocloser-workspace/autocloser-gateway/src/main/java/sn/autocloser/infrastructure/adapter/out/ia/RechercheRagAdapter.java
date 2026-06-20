package sn.autocloser.infrastructure.adapter.out.ia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import sn.autocloser.domain.port.ia.OllamaGatewayPort;
import sn.autocloser.domain.port.ia.RechercheRagPort;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Adaptateur RAG
 * Utilise JdbcTemplate + SQL natif pgvector pour la recherche par similarité cosinus.
 * Utilise OllamaGatewayPort pour générer les embeddings.
 */
@Component
public class RechercheRagAdapter implements RechercheRagPort {

    private static final Logger log = LoggerFactory.getLogger(RechercheRagAdapter.class);

    private final JdbcTemplate jdbcTemplate;
    private final OllamaGatewayPort ollamaGateway;

    public RechercheRagAdapter(JdbcTemplate jdbcTemplate, OllamaGatewayPort ollamaGateway) {
        this.jdbcTemplate = jdbcTemplate;
        this.ollamaGateway = ollamaGateway;
    }

    @Override
    public List<ResultatRag> rechercherProduitsProches(UUID commercantId, String requete, int topK) {
        log.debug("🔍 [RAG] Recherche pour : '{}' (commerçant: {})", requete, commercantId);

        // 1. Générer l'embedding de la requête client
        float[] queryVector = ollamaGateway.genererEmbedding(requete);
        String vectorStr = floatArrayToPostgresVector(queryVector);

        log.debug("🔢 [RAG] Vecteur de requête généré ({} dims)", queryVector.length);

        // 2. Requête SQL native pgvector avec opérateur cosinus <=>
        // On filtre les produits qui ont déjà un embedding (indexés)
        String sql = """
                SELECT
                    p.id::text,
                    p.nom,
                    p.description,
                    p.prix,
                    p.stock,
                    1 - (p.embedding <=> ?::vector) AS score
                FROM produits p
                WHERE p.commercant_id = ?::uuid
                  AND p.actif = TRUE
                  AND p.embedding IS NOT NULL
                ORDER BY p.embedding <=> ?::vector
                LIMIT ?
                """;

        List<ResultatRag> resultats = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ResultatRag(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getDouble("score")
                ),
                vectorStr, commercantId.toString(), vectorStr, topK
        );

        log.info("✅ [RAG] {} produit(s) trouvé(s) pour '{}'", resultats.size(), requete);
        return resultats;
    }

    @Override
    public void indexerProduit(UUID produitId, String texteAPourIndexer) {
        log.info("📥 [RAG INDEXATION] Indexation du produit : {}", produitId);

        // 1. Générer l'embedding du texte produit
        float[] embedding = ollamaGateway.genererEmbedding(texteAPourIndexer);
        String vectorStr = floatArrayToPostgresVector(embedding);

        // 2. Sauvegarder dans la colonne embedding de PostgreSQL
        String sql = "UPDATE produits SET embedding = ?::vector WHERE id = ?::uuid";
        int rows = jdbcTemplate.update(sql, vectorStr, produitId.toString());

        if (rows > 0) {
            log.info("✅ [RAG INDEXATION] Produit {} indexé ({} dims)", produitId, embedding.length);
        } else {
            log.warn("⚠️ [RAG INDEXATION] Produit {} introuvable !", produitId);
        }
    }

    /**
     * Convertit un tableau float[] en format string PostgreSQL : '[0.1, 0.2, ...]'
     */
    private String floatArrayToPostgresVector(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            sb.append(vector[i]);
            if (i < vector.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
