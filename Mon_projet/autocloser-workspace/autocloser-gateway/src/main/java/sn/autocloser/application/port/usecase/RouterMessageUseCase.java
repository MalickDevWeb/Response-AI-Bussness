package sn.autocloser.application.port.usecase;

import sn.autocloser.application.port.command.RouterMessageCommand;

/**
 * PORT ENTRANT (In) - Le Use Case principal du Mois 1 :
 * Reçoit un message brut et décide si c'est un message Admin ou un message Client.
 */
public interface RouterMessageUseCase {
    String router(RouterMessageCommand command);
}
