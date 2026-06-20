package sn.autocloser.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event : Déclenché dès qu'une nouvelle conversation est créée.
 * Utilisé pour logguer ou notifier d'autres services si besoin.
 */
public record ConversationCreeeEvent(
        UUID conversationId,
        UUID commercantId,
        String clientId,
        Instant occurredAt
) {
    public ConversationCreeeEvent(UUID conversationId, UUID commercantId, String clientId) {
        this(conversationId, commercantId, clientId, Instant.now());
    }
}
