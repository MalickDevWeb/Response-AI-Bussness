package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ConversationJpaRepository extends JpaRepository<ConversationJpaEntity, UUID> {
    Optional<ConversationJpaEntity> findByClientIdAndCommercantIdAndPlateforme(
            String clientId, UUID commercantId, String plateforme);
}
