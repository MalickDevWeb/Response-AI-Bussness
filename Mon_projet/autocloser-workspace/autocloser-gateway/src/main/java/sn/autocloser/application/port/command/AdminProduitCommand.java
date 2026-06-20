package sn.autocloser.application.port.command;

import java.math.BigDecimal;

/**
 * COMMANDE ADMIN : Ajout ou mise à jour d'un produit via WhatsApp.
 * Exemple: "Ajoute produit : Veste Cuir, Prix: 25000, Stock: 4"
 */
public record AdminProduitCommand(
        String commercantTelephone,
        String action,        // "AJOUTER", "MODIFIER_PRIX", "RUPTURE_STOCK"
        String nomProduit,
        BigDecimal nouveauPrix,
        Integer stock,
        String imageUrl       // null si texte simple
) {}
