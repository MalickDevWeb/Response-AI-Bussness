package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class MessageJpaEntity {

    @Id
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "type_media")
    private String typeMedia = "text";

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MessageJpaEntity() {}

    public MessageJpaEntity(UUID id, UUID conversationId, String role, String contenu, String typeMedia, String mediaUrl, Instant createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.role = role;
        this.contenu = contenu;
        this.typeMedia = typeMedia;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getContenu() { return contenu; }
    public String getTypeMedia() { return typeMedia; }
    public String getMediaUrl() { return mediaUrl; }
    public Instant getCreatedAt() { return createdAt; }
}
