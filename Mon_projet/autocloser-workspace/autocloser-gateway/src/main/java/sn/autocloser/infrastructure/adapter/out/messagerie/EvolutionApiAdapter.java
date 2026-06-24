package sn.autocloser.infrastructure.adapter.out.messagerie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sn.autocloser.domain.port.messagerie.EnvoiMessagePort;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * INFRASTRUCTURE - Adaptateur Evolution API v2
 * Implémente l'envoi de messages et la gestion d'instances WhatsApp.
 *
 * Flux QR (Evolution API v2) :
 *  1. POST /instance/create  → crée l'instance en mode "connecting"
 *  2. GET  /instance/connect/{name} → retourne le QR code base64 dès qu'il est prêt
 */
@Component
public class EvolutionApiAdapter implements EnvoiMessagePort,
        sn.autocloser.domain.port.messagerie.GestionInstancePort {

    private static final Logger log = LoggerFactory.getLogger(EvolutionApiAdapter.class);
    private static final int QR_MAX_RETRIES    = 15;
    private static final int QR_RETRY_DELAY_MS = 2000;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${autocloser.evolution-api.base-url:http://localhost:8081}")
    private String baseUrl;

    @Value("${autocloser.evolution-api.global-apikey:}")
    private String globalApiKey;

    public EvolutionApiAdapter(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ─── Headers communs ────────────────────────────────────────────────────────

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (globalApiKey != null && !globalApiKey.isEmpty()) {
            headers.set("apikey", globalApiKey);
        }
        return headers;
    }

    // ─── Envoi de message ────────────────────────────────────────────────────────

    @Override
    public void envoyerMessage(String numeroDestinataire, String contenu, String instanceNom) {
        String url = baseUrl + "/message/sendText/" + instanceNom;

        Map<String, Object> body = Map.of(
            "number", numeroDestinataire,
            "text",   contenu
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, buildHeaders());

        try {
            log.info("📤 [EVOLUTION] Envoi message vers {} via {}", numeroDestinataire, instanceNom);
            restTemplate.postForEntity(url, request, String.class);
            log.info("✅ [EVOLUTION] Message envoyé avec succès");
        } catch (Exception e) {
            log.error("❌ [EVOLUTION] Échec envoi vers {} : {}", numeroDestinataire, e.getMessage());
        }
    }

    // ─── Gestion d'instance + QR Code ───────────────────────────────────────────

    @Override
    public String creerInstanceEtGenererQr(String instanceNom) {
        // Étape 1 : Créer (ou ignorer si l'instance existe déjà)
        creerInstanceSiAbsente(instanceNom);

        // Étape 2 : Connecter et récupérer le QR avec polling
        return connecterEtObtenirQr(instanceNom);
    }

    /**
     * Crée l'instance sur Evolution API.
     * Si elle existe déjà (409 Conflict), on ignore silencieusement.
     */
    private void creerInstanceSiAbsente(String instanceNom) {
        String url = baseUrl + "/instance/create";

        // NOTE : On n'envoie PAS de webhook ici pour éviter l'erreur
        // "Invalid url property" lorsqu'on est en local.
        Map<String, Object> body = Map.of(
            "instanceName", instanceNom,
            "integration",  "WHATSAPP-BAILEYS",
            "qrcode",       true
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, buildHeaders());

        try {
            log.info("🛠️ [EVOLUTION] Création de l'instance « {} »", instanceNom);
            restTemplate.postForObject(url, request, String.class);
            log.info("✅ [EVOLUTION] Instance « {} » créée", instanceNom);
        } catch (HttpClientErrorException.Conflict e) {
            log.info("ℹ️ [EVOLUTION] Instance « {} » déjà existante, on continue", instanceNom);
        } catch (HttpClientErrorException e) {
            // Instance peut-être déjà créée avec un message d'erreur différent
            log.warn("⚠️ [EVOLUTION] Création instance « {} » → {} : {}",
                    instanceNom, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.warn("⚠️ [EVOLUTION] Impossible de créer l'instance « {} » : {}", instanceNom, e.getMessage());
        }
    }

    /**
     * Appelle GET /instance/connect/{name} en boucle jusqu'à obtenir le QR base64.
     * L'API Evolution génère le QR de manière asynchrone (Baileys/WebSocket).
     *
     * @return QR Code en base64 (avec ou sans préfixe data:image), ou null si timeout.
     */
    private String connecterEtObtenirQr(String instanceNom) {
        String url = baseUrl + "/instance/connect/" + instanceNom;
        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());

        for (int i = 1; i <= QR_MAX_RETRIES; i++) {
            try {
                log.info("🔄 [EVOLUTION] Tentative QR {}/{} pour « {} »", i, QR_MAX_RETRIES, instanceNom);

                ResponseEntity<String> response =
                        restTemplate.exchange(url, HttpMethod.GET, request, String.class);

                if (response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());

                    // Evolution API v2 retourne : { "base64": "data:image/png;base64,...", "code": "1@xxx", "count": N }
                    String base64 = extractQrBase64(root);
                    if (base64 != null && !base64.isBlank()) {
                        log.info("✅ [EVOLUTION] QR Code obtenu pour « {} » (tentative {})", instanceNom, i);
                        return base64;
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ [EVOLUTION] Erreur connect tentative {} : {}", i, e.getMessage());
            }

            if (i < QR_MAX_RETRIES) {
                try { Thread.sleep(QR_RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
            }
        }

        log.warn("⏱️ [EVOLUTION] QR Code non disponible après {} tentatives pour « {} »", QR_MAX_RETRIES, instanceNom);
        return null;
    }

    /**
     * Extrait le QR base64 depuis la réponse JSON de /instance/connect.
     * Evolution API v2 peut retourner le champ à différents niveaux.
     */
    private String extractQrBase64(JsonNode root) {
        // Format standard : { "base64": "...", "code": "...", "count": N }
        if (root.has("base64") && !root.get("base64").isNull()) {
            return root.get("base64").asText();
        }
        // Format alternatif : { "qrcode": { "base64": "..." } }
        if (root.has("qrcode") && root.get("qrcode").has("base64")) {
            return root.get("qrcode").get("base64").asText();
        }
        return null;
    }
}
