package sn.autocloser.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Conversation;
import sn.autocloser.domain.valueobject.Plateforme;
import sn.autocloser.domain.valueobject.StatutConversation;
import sn.autocloser.infrastructure.adapter.out.persistence.entity.ConversationJpaEntity;

@Component
public class ConversationPersistenceMapper {

    public ConversationJpaEntity toEntity(Conversation domain) {
        return ConversationJpaEntity.builder()
                .id(domain.getId())
                .commercantId(domain.getCommercantId())
                .clientId(domain.getClientId())
                .plateforme(domain.getPlateforme().name())
                .statut(domain.getStatut().name())
                .updatedAt(java.time.Instant.now())
                .build();
    }

    public Conversation toDomain(sn.autocloser.infrastructure.adapter.out.persistence.entity.ConversationJpaEntity entity) {
        return Conversation.reconstituer(
                entity.getId(),
                entity.getCommercantId(),
                entity.getClientId(),
                Plateforme.valueOf(entity.getPlateforme()),
                StatutConversation.valueOf(entity.getStatut()),
                entity.getUpdatedAt()
        );
    }
}
