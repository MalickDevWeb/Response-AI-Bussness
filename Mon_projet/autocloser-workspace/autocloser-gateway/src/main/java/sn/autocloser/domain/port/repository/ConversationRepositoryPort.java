package sn.autocloser.domain.port.repository;

import sn.autocloser.domain.model.Conversation;
import sn.autocloser.domain.valueobject.Plateforme;

import java.util.Optional;
import java.util.UUID;

/**
 * PORT SORTANT (Out) - Contrat de persistance des conversations.
 * L'infrastructure implémentera ce port. Le domaine ne connaît pas JPA.
 */
public interface ConversationRepositoryPort {

    Conversation sauvegarder(Conversation conversation);

    Optional<Conversation> trouverParClientEtCommercant(
            String clientId, UUID commercantId, Plateforme plateforme);

    Optional<Conversation> trouverParId(UUID id);
}
