package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;
import java.util.UUID;

public interface CommercantJpaRepository extends JpaRepository<CommercantJpaEntity, UUID> {
    Optional<CommercantJpaEntity> findByTelephoneCommercant(String telephone);
}
