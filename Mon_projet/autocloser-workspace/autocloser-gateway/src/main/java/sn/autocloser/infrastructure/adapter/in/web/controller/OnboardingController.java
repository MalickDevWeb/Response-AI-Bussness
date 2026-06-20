package sn.autocloser.infrastructure.adapter.in.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.autocloser.application.service.OnboardingCommercantService;
import sn.autocloser.domain.port.messagerie.GestionInstancePort;

import java.util.Map;

/**
 * INFRASTRUCTURE - API REST d'onboarding des commerçants.
 *
 * Endpoints :
 *  POST /api/v1/onboarding/inscription   → Crée le commerçant ET l'instance WhatsApp
 *  GET  /api/v1/onboarding/qr/{instance} → Récupère (ou recrée) le QR Code pour une instance
 *  GET  /api/v1/onboarding/status/{instance} → Vérifie si l'instance est connectée (polling frontend)
 */
@RestController
@RequestMapping("/api/v1/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    private static final Logger log = LoggerFactory.getLogger(OnboardingController.class);

    private final OnboardingCommercantService onboardingService;
    private final GestionInstancePort gestionInstancePort;

    public OnboardingController(OnboardingCommercantService onboardingService,
                                 GestionInstancePort gestionInstancePort) {
        this.onboardingService = onboardingService;
        this.gestionInstancePort = gestionInstancePort;
    }

    /**
     * Inscrit le commerçant et démarre la session WhatsApp.
     * Le QR Code peut être null si Evolution API met du temps à le générer —
     * dans ce cas le frontend doit appeler GET /qr/{instance} séparément.
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@RequestBody Map<String, String> payload) {
        try {
            var demande = new OnboardingCommercantService.DemandeInscription(
                    payload.get("nomBoutique"),
                    payload.get("telephone"),
                    payload.get("email"),
                    payload.getOrDefault("domaine", "Général")
            );
            var reponse = onboardingService.inscrireCommercant(demande);
            return ResponseEntity.ok(reponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur inscription", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("erreur", "Erreur interne: " + e.getMessage()));
        }
    }

    /**
     * Récupère le QR Code d'une instance WhatsApp existante.
     * Appelé par le frontend en mode polling jusqu'à obtenir le QR.
     *
     * Réponse success : { "qrCodeBase64": "data:image/png;base64,...", "ready": true }
     * Réponse pending : { "qrCodeBase64": null, "ready": false, "message": "..." }
     */
    @GetMapping("/qr/{instance}")
    public ResponseEntity<?> getQrCode(@PathVariable String instance) {
        try {
            log.info("📱 [ONBOARDING] Demande QR pour l'instance : {}", instance);
            String qrBase64 = gestionInstancePort.creerInstanceEtGenererQr(instance);

            if (qrBase64 != null && !qrBase64.isBlank()) {
                return ResponseEntity.ok(Map.of(
                        "qrCodeBase64", qrBase64,
                        "ready", true
                ));
            } else {
                // QR pas encore prêt (Evolution API génère en async)
                return ResponseEntity.ok(Map.of(
                        "qrCodeBase64", "",
                        "ready", false,
                        "message", "QR Code en cours de génération, réessayez dans 3 secondes."
                ));
            }
        } catch (Exception e) {
            log.error("Erreur QR pour {}", instance, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "erreur", "Erreur lors de la récupération du QR Code: " + e.getMessage(),
                            "ready", false
                    ));
        }
    }
}
