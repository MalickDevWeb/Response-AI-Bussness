package sn.autocloser.infrastructure.config.ollama;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * INFRASTRUCTURE - Configuration des modèles IA locaux via Ollama.
 * Les modèles sont créés ici (couche infrastructure) et injectés dans l'adaptateur.
 * Le domaine reste totalement ignorant de LangChain4j.
 */
@Configuration
public class OllamaConfig {

    @Value("${autocloser.ollama.base-url}")
    private String baseUrl;

    @Value("${autocloser.ollama.chat-model}")
    private String chatModelName;

    @Value("${autocloser.ollama.embed-model}")
    private String embedModelName;

    @Value("${autocloser.ollama.temperature:0.3}")
    private double temperature;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(chatModelName)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(120))
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embedModelName)
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
