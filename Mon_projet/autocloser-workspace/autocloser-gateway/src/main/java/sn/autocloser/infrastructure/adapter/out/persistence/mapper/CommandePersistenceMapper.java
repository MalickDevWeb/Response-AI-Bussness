package sn.autocloser.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Commande;
import sn.autocloser.domain.valueobject.StatutCommande;
import sn.autocloser.infrastructure.adapter.out.persistence.entity.CommandeJpaEntity;

@Component
public class CommandePersistenceMapper {

    public CommandeJpaEntity toEntity(Commande commande) {
        return new CommandeJpaEntity(
                commande.getId(),
                commande.getCommercantId(),
                commande.getClientId(),
                commande.getConversationId(),
                commande.getMontantTotal(),
                commande.getStatut().name(),
                commande.getCreatedAt()
        );
    }

    public Commande toDomain(CommandeJpaEntity entity) {
        return Commande.reconstituer(
                entity.getId(),
                entity.getCommercantId(),
                entity.getClientId(),
                entity.getConversationId(),
                entity.getMontantTotal(),
                StatutCommande.valueOf(entity.getStatut()),
                entity.getCreatedAt()
        );
    }
}
