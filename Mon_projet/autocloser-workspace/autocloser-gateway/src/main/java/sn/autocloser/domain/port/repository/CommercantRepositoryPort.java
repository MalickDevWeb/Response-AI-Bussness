package sn.autocloser.domain.port.repository;

import sn.autocloser.domain.model.Commercant;

import java.util.Optional;
import java.util.UUID;

/**
 * PORT SORTANT (Out) - Contrat de persistance des commerçants.
 */
public interface CommercantRepositoryPort {

    Optional<Commercant> trouverParTelephone(String telephone);

    Optional<Commercant> trouverParId(UUID id);

    Commercant sauvegarder(Commercant commercant);
}
