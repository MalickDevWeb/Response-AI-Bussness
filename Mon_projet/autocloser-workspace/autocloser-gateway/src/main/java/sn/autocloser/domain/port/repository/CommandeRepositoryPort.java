package sn.autocloser.domain.port.repository;

import sn.autocloser.domain.model.Commande;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeRepositoryPort {

    Commande sauvegarder(Commande commande);

    Optional<Commande> trouverParId(UUID id);

    List<Commande> trouverParCommercantId(UUID commercantId);

    Optional<Commande> trouverEnAttenteParConversation(UUID conversationId);
}
