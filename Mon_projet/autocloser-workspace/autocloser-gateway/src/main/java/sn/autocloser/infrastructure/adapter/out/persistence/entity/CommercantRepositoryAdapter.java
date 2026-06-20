package sn.autocloser.infrastructure.adapter.out.persistence.entity;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Commercant;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;

import sn.autocloser.infrastructure.adapter.out.persistence.mapper.CommercantPersistenceMapper;

import java.util.Optional;
import java.util.UUID;

@Component
public class CommercantRepositoryAdapter implements CommercantRepositoryPort {

    private final CommercantJpaRepository jpaRepository;
    private final CommercantPersistenceMapper
     mapper;

    public CommercantRepositoryAdapter(CommercantJpaRepository jpaRepository,
                                       CommercantPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Commercant> trouverParTelephone(String telephone) {
        return jpaRepository.findByTelephoneCommercant(telephone)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Commercant> trouverParId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Commercant sauvegarder(Commercant commercant) {
        CommercantJpaEntity saved = jpaRepository.save(mapper.toEntity(commercant));
        return mapper.toDomain(saved);
    }
}
