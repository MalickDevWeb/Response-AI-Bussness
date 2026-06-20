package sn.autocloser.infrastructure.adapter.out.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import sn.autocloser.domain.event.ConversationCreeeEvent;
import sn.autocloser.domain.event.PaiementValideEvent;
import sn.autocloser.domain.port.event.ConversationEventPublisherPort;

/**
 * INFRASTRUCTURE - Adaptateur Sortant (Out)
 * Implémente le port de publication d'événements.
 * Utilise le bus d'événements interne de Spring (ApplicationEventPublisher) pour le Mois 1.
 * Pourra être remplacé par Kafka/RabbitMQ plus tard si besoin de microservices distribués.
 */
@Component
public class ConversationEventPublisherAdapter implements ConversationEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(ConversationEventPublisherAdapter.class);
    private final ApplicationEventPublisher springPublisher;

    public ConversationEventPublisherAdapter(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publierConversationCreee(ConversationCreeeEvent event) {
        log.info("📢 [EVENT PUBLISHED] Conversation créée : {} pour le client {}", event.conversationId(), event.clientId());
        springPublisher.publishEvent(event);
    }

    @Override
    public void publierPaiementValide(PaiementValideEvent event) {
        log.info("📢 [EVENT PUBLISHED] Paiement validé pour la conversation : {}", event.conversationId());
        springPublisher.publishEvent(event);
    }
}
