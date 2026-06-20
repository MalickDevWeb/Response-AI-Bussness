package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Message;
import sn.autocloser.domain.port.repository.MessageRepositoryPort;
import sn.autocloser.infrastructure.adapter.out.persistence.mapper.MessagePersistenceMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final MessageJpaRepository repository;
    private final MessagePersistenceMapper mapper;

    public MessageRepositoryAdapter(MessageJpaRepository repository, MessagePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Message sauvegarder(Message message) {
        MessageJpaEntity entity = mapper.toEntity(message);
        MessageJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Message> recupererHistorique(UUID conversationId, int limit) {
        List<MessageJpaEntity> entities = repository.findDerniersMessages(conversationId, PageRequest.of(0, limit));
        
        List<Message> messages = entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        
        // Les remettre dans l'ordre chronologique (du plus ancien au plus récent)
        Collections.reverse(messages);
        return messages;
    }
}
