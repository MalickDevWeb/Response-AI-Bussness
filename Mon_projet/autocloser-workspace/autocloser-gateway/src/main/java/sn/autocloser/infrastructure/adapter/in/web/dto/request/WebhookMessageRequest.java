package sn.autocloser.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO entrant : Webhook WhatsApp/Instagram → AutoCloser
 * C'est le JSON que Evolution API envoie à notre API Gateway.
 */
public record WebhookMessageRequest(
        @NotBlank String expediteurId,
        @NotBlank String commercantTelephone,
        @NotBlank String contenu,
        String typeMedia,    // "text" | "audio" | "image" (défaut: "text")
        String mediaUrl,
        @NotBlank String plateforme  // "WHATSAPP" | "INSTAGRAM" | "TIKTOK"
) {}
