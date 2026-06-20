package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, UUID> {

    @Query("SELECT m FROM MessageJpaEntity m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC")
    List<MessageJpaEntity> findDerniersMessages(UUID conversationId, Pageable pageable);
}
