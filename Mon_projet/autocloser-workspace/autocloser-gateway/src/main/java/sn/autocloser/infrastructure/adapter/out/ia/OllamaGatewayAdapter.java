package sn.autocloser.infrastructure.adapter.out.ia;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sn.autocloser.domain.port.ia.OllamaGatewayPort;

/**
 * INFRASTRUCTURE - Adaptateur Sortant (Out)
 * Implémente le port IA en utilisant LangChain4j + Ollama.
 * Le domaine ne voit jamais cette classe.
 */
@Component
public class OllamaGatewayAdapter implements OllamaGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(OllamaGatewayAdapter.class);

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;

    public OllamaGatewayAdapter(ChatLanguageModel chatModel, EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public String genererReponse(String systemPrompt, String userMessage) {
        log.debug("🤖 [OLLAMA CHAT] Envoi du message à Llama...");
        var response = chatModel.generate(
            SystemMessage.from(systemPrompt),
            UserMessage.from(userMessage)
        );
        String reponse = response.content().text();
        log.debug("🤖 [OLLAMA CHAT] Réponse reçue ({} chars)", reponse.length());
        return reponse;
    }

    @Override
    public float[] genererEmbedding(String texte) {
        log.debug("🔢 [OLLAMA EMBED] Génération du vecteur pour : '{}'", texte.substring(0, Math.min(50, texte.length())));
        Response<Embedding> response = embeddingModel.embed(texte);
        return response.content().vector();
    }
}
