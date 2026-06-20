package sn.autocloser.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * DOMAINE - Entité Message
 * Représente un message individuel dans une conversation.
 */
public class Message {

    private final UUID id;
    private final UUID conversationId;
    private final String role; // "USER" ou "ASSISTANT"
    private final String contenu;
    private final Instant createdAt;

    private Message(UUID id, UUID conversationId, String role, String contenu, Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.role = role;
        this.contenu = contenu;
        this.createdAt = createdAt;
    }

    public static Message creer(UUID conversationId, String role, String contenu) {
        return new Message(UUID.randomUUID(), conversationId, role, contenu, Instant.now());
    }

    public static Message reconstituer(UUID id, UUID conversationId, String role, String contenu, Instant createdAt) {
        return new Message(id, conversationId, role, contenu, createdAt);
    }

    public UUID getId() { return id; }
    public UUID getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getContenu() { return contenu; }
    public Instant getCreatedAt() { return createdAt; }
}
