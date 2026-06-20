package sn.autocloser.domain.port.repository;

import sn.autocloser.domain.model.Message;

import java.util.List;
import java.util.UUID;

/**
 * PORT SORTANT - Sauvegarde et récupération de l'historique des messages.
 */
public interface MessageRepositoryPort {

    Message sauvegarder(Message message);

    /**
     * Récupère les derniers messages d'une conversation.
     * Utile pour la mémoire de l'IA (contexte).
     *
     * @param conversationId ID de la conversation
     * @param limit Nombre de messages à récupérer (ex: les 10 derniers)
     * @return Liste ordonnée des plus anciens aux plus récents
     */
    List<Message> recupererHistorique(UUID conversationId, int limit);
}
