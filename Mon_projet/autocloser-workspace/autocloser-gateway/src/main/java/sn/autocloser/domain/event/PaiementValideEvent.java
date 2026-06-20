package sn.autocloser.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event : Déclenché dès que l'IA Vision valide un reçu de paiement.
 * Déclenche l'alerte "Vente Confirmée" au commerçant.
 */
public record PaiementValideEvent(
        UUID conversationId,
        UUID commercantId,
        String clientId,
        Instant occurredAt
) {
    public PaiementValideEvent(UUID conversationId, UUID commercantId, String clientId) {
        this(conversationId, commercantId, clientId, Instant.now());
    }
}
