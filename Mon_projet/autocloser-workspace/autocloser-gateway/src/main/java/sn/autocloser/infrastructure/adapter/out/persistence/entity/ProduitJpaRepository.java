package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProduitJpaRepository extends JpaRepository<ProduitJpaEntity, UUID> {
    List<ProduitJpaEntity> findByCommercantId(UUID commercantId);
}
