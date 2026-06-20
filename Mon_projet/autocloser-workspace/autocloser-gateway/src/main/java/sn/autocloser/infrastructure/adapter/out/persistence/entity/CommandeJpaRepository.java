package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeJpaRepository extends JpaRepository<CommandeJpaEntity, UUID> {
    
    List<CommandeJpaEntity> findByCommercantId(UUID commercantId);
    
    Optional<CommandeJpaEntity> findByConversationIdAndStatut(UUID conversationId, String statut);
}
