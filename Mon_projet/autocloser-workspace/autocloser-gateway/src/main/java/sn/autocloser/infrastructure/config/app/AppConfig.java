package sn.autocloser.infrastructure.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sn.autocloser.application.service.AdminCatalogueService;
import sn.autocloser.application.service.OnboardingCommercantService;
import sn.autocloser.application.service.RouterMessageService;
import sn.autocloser.domain.port.event.ConversationEventPublisherPort;
import sn.autocloser.domain.port.ia.OllamaGatewayPort;
import sn.autocloser.domain.port.ia.RechercheRagPort;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;
import sn.autocloser.domain.port.repository.ConversationRepositoryPort;

import sn.autocloser.domain.port.repository.ProduitRepositoryPort;

@Configuration
public class AppConfig {

    @Bean
    public AdminCatalogueService adminCatalogueService(CommercantRepositoryPort commercantRepo, ProduitRepositoryPort produitRepo) {
        return new AdminCatalogueService(commercantRepo, produitRepo);
    }

    @Bean
    public OnboardingCommercantService onboardingCommercantService(
            CommercantRepositoryPort commercantRepo,
            sn.autocloser.domain.port.messagerie.GestionInstancePort gestionInstancePort) {
        return new OnboardingCommercantService(commercantRepo, gestionInstancePort);
    }

    @Bean
    public RouterMessageService routerMessageService(
            CommercantRepositoryPort commercantRepo,
            ConversationRepositoryPort conversationRepo,
            AdminCatalogueService adminCatalogueService,
            ConversationEventPublisherPort eventPublisher,
            RechercheRagPort rechercheRagPort,
            OllamaGatewayPort ollamaGatewayPort,
            sn.autocloser.domain.port.messagerie.EnvoiMessagePort envoiMessagePort,
            sn.autocloser.domain.port.repository.MessageRepositoryPort messageRepo,
            sn.autocloser.domain.port.ia.VisionGatewayPort visionGatewayPort,
            sn.autocloser.domain.port.repository.CommandeRepositoryPort commandeRepo) {
        
        return new RouterMessageService(
                commercantRepo,
                conversationRepo,
                adminCatalogueService,
                eventPublisher,
                rechercheRagPort,
                ollamaGatewayPort,
                envoiMessagePort,
                messageRepo,
                visionGatewayPort,
                commandeRepo
        );
    }
}
