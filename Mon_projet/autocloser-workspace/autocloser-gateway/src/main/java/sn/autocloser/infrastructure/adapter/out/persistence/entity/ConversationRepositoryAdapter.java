package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Conversation;
import sn.autocloser.domain.port.repository.ConversationRepositoryPort;
import sn.autocloser.domain.valueobject.Plateforme;
import sn.autocloser.infrastructure.adapter.out.persistence.mapper.ConversationPersistenceMapper;

import java.util.Optional;
import java.util.UUID;

@Component
public class ConversationRepositoryAdapter implements ConversationRepositoryPort {

    private final ConversationJpaRepository jpaRepository;
    private final ConversationPersistenceMapper mapper;

    public ConversationRepositoryAdapter(ConversationJpaRepository jpaRepository,
                                         ConversationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Conversation sauvegarder(Conversation conversation) {
        ConversationJpaEntity saved = jpaRepository.save(mapper.toEntity(conversation));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Conversation> trouverParClientEtCommercant(
            String clientId, UUID commercantId, Plateforme plateforme) {
        return jpaRepository.findByClientIdAndCommercantIdAndPlateforme(
                clientId, commercantId, plateforme.name()
        ).map(mapper::toDomain);
    }

    @Override
    public Optional<Conversation> trouverParId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
