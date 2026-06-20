package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Commande;
import sn.autocloser.domain.port.repository.CommandeRepositoryPort;
import sn.autocloser.domain.valueobject.StatutCommande;
import sn.autocloser.infrastructure.adapter.out.persistence.mapper.CommandePersistenceMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommandeRepositoryAdapter implements CommandeRepositoryPort {

    private final CommandeJpaRepository repository;
    private final CommandePersistenceMapper mapper;

    public CommandeRepositoryAdapter(CommandeJpaRepository repository, CommandePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Commande sauvegarder(Commande commande) {
        CommandeJpaEntity entity = mapper.toEntity(commande);
        CommandeJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Commande> trouverParId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Commande> trouverParCommercantId(UUID commercantId) {
        return repository.findByCommercantId(commercantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Commande> trouverEnAttenteParConversation(UUID conversationId) {
        return repository.findByConversationIdAndStatut(conversationId, StatutCommande.EN_ATTENTE_PAIEMENT.name())
                .map(mapper::toDomain);
    }
}
