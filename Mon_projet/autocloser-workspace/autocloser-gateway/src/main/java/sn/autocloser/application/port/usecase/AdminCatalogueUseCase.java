package sn.autocloser.application.port.usecase;

import sn.autocloser.application.port.command.AdminProduitCommand;

/**
 * PORT ENTRANT (In) - Interprète et exécute les commandes Admin en langage naturel.
 */
public interface AdminCatalogueUseCase {
    String executerCommande(AdminProduitCommand command);
}
