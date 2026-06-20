package sn.autocloser.infrastructure.adapter.in.web.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.autocloser.application.port.command.RouterMessageCommand;
import sn.autocloser.application.port.usecase.RouterMessageUseCase;
import sn.autocloser.domain.valueobject.Plateforme;
import sn.autocloser.infrastructure.adapter.in.web.dto.request.WebhookMessageRequest;
import sn.autocloser.infrastructure.adapter.in.web.dto.response.ApiResponse;

/**
 * INFRASTRUCTURE - Adaptateur Entrant (In)
 * Reçoit les webhooks des réseaux sociaux via Evolution API.
 * Point d'entrée principal du Mois 1.
 */
@RestController
@RequestMapping("/api/v1/webhook")
public class WebhookController {

    private final RouterMessageUseCase routerMessageUseCase;

    public WebhookController(RouterMessageUseCase routerMessageUseCase) {
        this.routerMessageUseCase = routerMessageUseCase;
    }

    /**
     * POST /api/v1/webhook/message
     * Evolution API envoie ici chaque message reçu sur WhatsApp/Instagram.
     */
    @PostMapping("/message")
    public ResponseEntity<ApiResponse> recevoirMessage(@Valid @RequestBody WebhookMessageRequest request) {
        RouterMessageCommand command = new RouterMessageCommand(
                request.expediteurId(),
                request.commercantTelephone(),
                request.contenu(),
                request.typeMedia() != null ? request.typeMedia() : "text",
                request.mediaUrl(),
                Plateforme.valueOf(request.plateforme().toUpperCase())
        );

        String resultat = routerMessageUseCase.router(command);
        return ResponseEntity.ok(ApiResponse.ok("Message traité avec succès", resultat));
    }

    /**
     * GET /api/v1/webhook/health
     * Vérifie que le service est bien en ligne (utile pour les tests).
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(ApiResponse.ok("AutoCloser AI Gateway opérationnel ✅"));
    }
}
