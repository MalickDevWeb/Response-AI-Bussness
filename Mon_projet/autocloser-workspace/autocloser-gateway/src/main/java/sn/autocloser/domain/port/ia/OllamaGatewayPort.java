package sn.autocloser.domain.port.ia;

/**
 * PORT SORTANT (Out) - Passerelle vers l'IA Locale Ollama.
 * Le domaine n'a aucune dépendance sur LangChain4j.
 * C'est l'adaptateur d'infrastructure qui implémente ce contrat.
 */
public interface OllamaGatewayPort {

    /**
     * Génère une réponse textuelle à partir d'un System Prompt et du message utilisateur.
     */
    String genererReponse(String systemPrompt, String userMessage);

    /**
     * Génère un vecteur d'embedding pour l'indexation ou la recherche RAG.
     */
    float[] genererEmbedding(String texte);
}
