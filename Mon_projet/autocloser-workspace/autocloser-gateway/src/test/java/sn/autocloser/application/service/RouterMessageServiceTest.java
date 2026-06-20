package sn.autocloser.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.autocloser.application.port.command.RouterMessageCommand;
import sn.autocloser.domain.exception.CommercantIntrouvableException;
import sn.autocloser.domain.model.Commercant;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;
import sn.autocloser.domain.port.repository.ConversationRepositoryPort;
import sn.autocloser.domain.port.event.ConversationEventPublisherPort;
import sn.autocloser.domain.valueobject.Plateforme;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * TEST UNITAIRE - RouterMessageService
 * Vérifie la logique de routage Admin vs Client (Le cœur du Mois 1).
 * Aucune base de données requise → tests purs avec Mockito.
 */
@ExtendWith(MockitoExtension.class)
class RouterMessageServiceTest {

    @Mock
    private CommercantRepositoryPort commercantRepo;

    @Mock
    private ConversationRepositoryPort conversationRepo;

    @Mock
    private AdminCatalogueService adminCatalogueService;

    @Mock
    private ConversationEventPublisherPort eventPublisher;

    @InjectMocks
    private RouterMessageService routerMessageService;

    private Commercant commercantTest;
    private static final String TELEPHONE_COMMERCANT = "+221770000001";
    private static final String TELEPHONE_CLIENT = "+221771234567";

    @BeforeEach
    void setUp() {
        commercantTest = Commercant.creer("Boutique Test", TELEPHONE_COMMERCANT, "test@test.com");
        commercantTest.activerWhatsapp();
    }

    // =====================================================
    // TEST 1 : LE ROUTAGE ADMIN
    // =====================================================
    @Test
    @DisplayName("Un message du commerçant lui-même doit être routé en MODE ADMIN")
    void quandCommercantEnvoieMessage_doitActiverModeAdmin() {
        // GIVEN
        when(commercantRepo.trouverParTelephone(TELEPHONE_COMMERCANT))
                .thenReturn(Optional.of(commercantTest));

        RouterMessageCommand command = new RouterMessageCommand(
                TELEPHONE_COMMERCANT,  // l'expéditeur EST le commerçant
                TELEPHONE_COMMERCANT,
                "Prix de la robe passe à 15000",
                "text", null,
                Plateforme.WHATSAPP
        );

        // WHEN
        String resultat = routerMessageService.router(command);

        // THEN
        assertThat(resultat).contains("[ADMIN]");
        assertThat(resultat).contains(TELEPHONE_COMMERCANT);
    }

    // =====================================================
    // TEST 2 : LE ROUTAGE CLIENT
    // =====================================================
    @Test
    @DisplayName("Un message d'un numéro inconnu doit être routé en MODE VENTE (Client)")
    void quandClientEnvoieMessage_doitActiverModeVente() {
        // GIVEN
        when(commercantRepo.trouverParTelephone(TELEPHONE_COMMERCANT))
                .thenReturn(Optional.of(commercantTest));
        when(conversationRepo.trouverParClientEtCommercant(
                TELEPHONE_CLIENT, commercantTest.getId(), Plateforme.WHATSAPP))
                .thenReturn(Optional.empty());
        when(conversationRepo.sauvegarder(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(inv -> inv.getArgument(0));

        RouterMessageCommand command = new RouterMessageCommand(
                TELEPHONE_CLIENT,      // l'expéditeur est un client
                TELEPHONE_COMMERCANT,
                "Bonjour, vous avez des robes ?",
                "text", null,
                Plateforme.WHATSAPP
        );

        // WHEN
        String resultat = routerMessageService.router(command);

        // THEN
        assertThat(resultat).contains("[CLIENT]");
        assertThat(resultat).contains("Bonjour, vous avez des robes ?");
    }

    // =====================================================
    // TEST 3 : COMMERÇANT INTROUVABLE
    // =====================================================
    @Test
    @DisplayName("Si le commerçant est introuvable, une exception doit être levée")
    void quandCommercantInexistant_doitLeverException() {
        // GIVEN
        when(commercantRepo.trouverParTelephone("+221999999999"))
                .thenReturn(Optional.empty());

        RouterMessageCommand command = new RouterMessageCommand(
                TELEPHONE_CLIENT,
                "+221999999999",
                "Test message",
                "text", null,
                Plateforme.WHATSAPP
        );

        // WHEN + THEN
        assertThatThrownBy(() -> routerMessageService.router(command))
                .isInstanceOf(CommercantIntrouvableException.class)
                .hasMessageContaining("+221999999999");
    }
}
