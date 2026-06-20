package sn.autocloser.infrastructure.adapter.out.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sn.autocloser.domain.port.ia.VisionGatewayPort;

import java.util.List;
import java.util.Map;

/**
 * INFRASTRUCTURE - Adaptateur IA Vision (Ollama)
 * Appelle directement l'API REST d'Ollama pour l'analyse multimodale.
 */
@Component
public class OllamaVisionAdapter implements VisionGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(OllamaVisionAdapter.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${autocloser.ollama.base-url}")
    private String baseUrl;

    @Value("${autocloser.ollama.vision-model:llava}")
    private String visionModelName;

    public OllamaVisionAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String extraireDonneesRecu(String base64Image) {
        log.info("🔍 [IA VISION] Analyse du reçu avec le modèle {}", visionModelName);
        
        String url = baseUrl + "/api/generate";
        
        String prompt = "Tu es un expert comptable. " +
                "Analyse cette image de reçu de paiement (Wave, Orange Money, etc.). " +
                "Extrais uniquement le 'montant' et le 'numero_destinataire'. " +
                "Réponds UNIQUEMENT au format JSON strict, sans aucun autre texte. " +
                "Exemple : {\"montant\": 25000, \"numero_destinataire\": \"+221770000000\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", visionModelName,
                "prompt", prompt,
                "stream", false,
                "format", "json",
                "images", List.of(base64Image),
                "options", Map.of("temperature", 0.0)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            String responseStr = restTemplate.postForObject(url, request, String.class);
            JsonNode root = objectMapper.readTree(responseStr);
            String responseText = root.path("response").asText();
            log.info("✅ [IA VISION] Résultat brut : {}", responseText);
            return responseText;
        } catch (Exception e) {
            log.error("❌ [IA VISION] Erreur lors de l'analyse : {}", e.getMessage());
            return "{\"erreur\": \"Analyse impossible\"}";
        }
    }
}
