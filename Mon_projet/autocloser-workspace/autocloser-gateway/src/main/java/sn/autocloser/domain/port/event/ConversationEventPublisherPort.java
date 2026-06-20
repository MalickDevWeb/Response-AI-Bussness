package sn.autocloser.domain.port.event;

import sn.autocloser.domain.event.ConversationCreeeEvent;
import sn.autocloser.domain.event.PaiementValideEvent;

/**
 * PORT SORTANT (Out) - Publication des événements du domaine liés à la conversation.
 * Inspiré de votre fichier exemple.md.
 */
public interface ConversationEventPublisherPort {
    void publierConversationCreee(ConversationCreeeEvent event);
    void publierPaiementValide(PaiementValideEvent event);
}
