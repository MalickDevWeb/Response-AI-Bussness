package sn.autocloser.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Message;
import sn.autocloser.infrastructure.adapter.out.persistence.entity.MessageJpaEntity;

@Component
public class MessagePersistenceMapper {

    public MessageJpaEntity toEntity(Message message) {
        return new MessageJpaEntity(
                message.getId(),
                message.getConversationId(),
                message.getRole(),
                message.getContenu(),
                "text",
                null,
                message.getCreatedAt()
        );
    }

    public Message toDomain(MessageJpaEntity entity) {
        return Message.reconstituer(
                entity.getId(),
                entity.getConversationId(),
                entity.getRole(),
                entity.getContenu(),
                entity.getCreatedAt()
        );
    }
}
