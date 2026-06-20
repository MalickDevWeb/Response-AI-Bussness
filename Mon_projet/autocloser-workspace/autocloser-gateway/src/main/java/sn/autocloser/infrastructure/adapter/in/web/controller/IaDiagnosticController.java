package sn.autocloser.infrastructure.adapter.in.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.autocloser.domain.port.ia.OllamaGatewayPort;
import sn.autocloser.domain.port.ia.RechercheRagPort;
import sn.autocloser.infrastructure.adapter.in.web.dto.response.ApiResponse;

import java.util.List;
import java.util.UUID;

/**
 * INFRASTRUCTURE - Controller de diagnostic IA (Tâche 2.1 + 2.2)
 * Permet de valider Ollama Chat, Embeddings et RAG pgvector.
 * À SUPPRIMER ou sécuriser (rôle ADMIN) en production.
 */
@RestController
@RequestMapping("/api/v1/ia")
public class IaDiagnosticController {

    private final OllamaGatewayPort ollamaGateway;
    private final RechercheRagPort rechercheRag;

    public IaDiagnosticController(OllamaGatewayPort ollamaGateway, RechercheRagPort rechercheRag) {
        this.ollamaGateway = ollamaGateway;
        this.rechercheRag = rechercheRag;
    }

    // -------------------------------------------------------
    // TÂCHE 2.1 : Tests Chat + Embedding
    // -------------------------------------------------------

    /**
     * POST /api/v1/ia/ping
     * Test de connexion Ollama Chat.
     */
    @PostMapping("/ping")
    public ResponseEntity<ApiResponse> ping(@RequestBody PingRequest request) {
        String reponse = ollamaGateway.genererReponse(
            "Tu es un assistant de test. Réponds en une phrase courte.",
            request.message()
        );
        return ResponseEntity.ok(ApiResponse.ok("Ollama répond ✅", reponse));
    }

    /**
     * POST /api/v1/ia/embed
     * Test de génération d'embedding.
     */
    @PostMapping("/embed")
    public ResponseEntity<ApiResponse> embed(@RequestBody PingRequest request) {
        float[] vector = ollamaGateway.genererEmbedding(request.message());
        return ResponseEntity.ok(ApiResponse.ok(
            "Embedding généré ✅",
            "Dimension du vecteur : " + vector.length
        ));
    }

    // -------------------------------------------------------
    // TÂCHE 2.2 : Tests RAG (Indexation + Recherche)
    // -------------------------------------------------------

    /**
     * POST /api/v1/ia/rag/indexer
     * Indexe un produit existant en base (génère et sauvegarde son embedding).
     * Body: { "produitId": "uuid", "texte": "Robe en soie noire taille M" }
     */
    @PostMapping("/rag/indexer")
    public ResponseEntity<ApiResponse> indexerProduit(@RequestBody IndexerRequest request) {
        rechercheRag.indexerProduit(request.produitId(), request.texte());
        return ResponseEntity.ok(ApiResponse.ok(
            "Produit indexé ✅",
            "ID: " + request.produitId() + " | Texte: " + request.texte()
        ));
    }

    /**
     * POST /api/v1/ia/rag/rechercher
     * Recherche les produits les plus proches sémantiquement.
     * Body: { "commercantId": "uuid", "requete": "Je cherche une robe noire", "topK": 3 }
     */
    @PostMapping("/rag/rechercher")
    public ResponseEntity<ApiResponse> rechercherProduits(@RequestBody RechercherRequest request) {
        List<RechercheRagPort.ResultatRag> resultats = rechercheRag.rechercherProduitsProches(
            request.commercantId(),
            request.requete(),
            request.topK() > 0 ? request.topK() : 3
        );

        if (resultats.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(
                "Aucun produit trouvé (aucun produit indexé ?)",
                resultats
            ));
        }

        return ResponseEntity.ok(ApiResponse.ok(
            resultats.size() + " produit(s) trouvé(s) ✅",
            resultats
        ));
    }

    // ---- Records (DTOs internes) ----
    public record PingRequest(String message) {}
    public record IndexerRequest(UUID produitId, String texte) {}
    public record RechercherRequest(UUID commercantId, String requete, int topK) {}
}
