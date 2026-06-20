package sn.autocloser.application.service;

import sn.autocloser.application.port.command.AdminProduitCommand;
import sn.autocloser.application.port.usecase.AdminCatalogueUseCase;
import sn.autocloser.domain.exception.CommercantIntrouvableException;
import sn.autocloser.domain.model.Commercant;
import sn.autocloser.domain.model.Produit;
import sn.autocloser.domain.port.repository.CommercantRepositoryPort;
import sn.autocloser.domain.port.repository.ProduitRepositoryPort;

import java.util.List;

/**
 * APPLICATION - Service Admin (Gestion du catalogue par WhatsApp)
 * Exécute les commandes du commerçant en langage naturel.
 */
public class AdminCatalogueService implements AdminCatalogueUseCase {

    private final CommercantRepositoryPort commercantRepo;
    private final ProduitRepositoryPort produitRepo;

    public AdminCatalogueService(CommercantRepositoryPort commercantRepo,
                                 ProduitRepositoryPort produitRepo) {
        this.commercantRepo = commercantRepo;
        this.produitRepo = produitRepo;
    }

    @Override
    public String executerCommande(AdminProduitCommand cmd) {
        Commercant commercant = commercantRepo
                .trouverParTelephone(cmd.commercantTelephone())
                .orElseThrow(() -> new CommercantIntrouvableException(
                        "Commerçant introuvable : " + cmd.commercantTelephone()
                ));

        return switch (cmd.action().toUpperCase()) {
            case "AJOUTER" -> ajouterProduit(commercant, cmd);
            case "MODIFIER_PRIX" -> modifierPrix(commercant, cmd);
            case "RUPTURE_STOCK" -> mettreEnRupture(commercant, cmd);
            case "LISTER" -> listerProduits(commercant);
            default -> "❌ Commande non reconnue. Essayez : AJOUTER, MODIFIER_PRIX, RUPTURE_STOCK, LISTER";
        };
    }

    private String ajouterProduit(Commercant commercant, AdminProduitCommand cmd) {
        Produit produit = Produit.creer(
                commercant.getId(),
                cmd.nomProduit(),
                "Ajouté via WhatsApp Admin",
                cmd.nouveauPrix(),
                cmd.stock() != null ? cmd.stock() : 1,
                cmd.imageUrl()
        );
        produitRepo.sauvegarder(produit);
        return "✅ Produit ajouté : " + produit.getNom()
                + " | Prix : " + produit.getPrix() + " FCFA"
                + " | Stock : " + produit.getStock();
    }

    private String modifierPrix(Commercant commercant, AdminProduitCommand cmd) {
        List<Produit> produits = produitRepo.trouverParCommercant(commercant.getId());
        return produits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(cmd.nomProduit().toLowerCase()))
                .findFirst()
                .map(p -> {
                    p.mettreAJourPrix(cmd.nouveauPrix());
                    produitRepo.sauvegarder(p);
                    return "✅ Prix mis à jour : " + p.getNom() + " → " + p.getPrix() + " FCFA";
                })
                .orElse("❌ Produit introuvable : " + cmd.nomProduit());
    }

    private String mettreEnRupture(Commercant commercant, AdminProduitCommand cmd) {
        List<Produit> produits = produitRepo.trouverParCommercant(commercant.getId());
        return produits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(cmd.nomProduit().toLowerCase()))
                .findFirst()
                .map(p -> {
                    p.mettreEnRuptureDeStock();
                    produitRepo.sauvegarder(p);
                    return "✅ Rupture de stock enregistrée pour : " + p.getNom();
                })
                .orElse("❌ Produit introuvable : " + cmd.nomProduit());
    }

    private String listerProduits(Commercant commercant) {
        List<Produit> produits = produitRepo.trouverParCommercant(commercant.getId());
        if (produits.isEmpty()) return "📦 Aucun produit dans votre catalogue.";
        StringBuilder sb = new StringBuilder("📦 Votre catalogue :\n");
        produits.forEach(p -> sb.append("• ").append(p.getNom())
                .append(" - ").append(p.getPrix()).append(" FCFA")
                .append(" (Stock: ").append(p.getStock()).append(")\n"));
        return sb.toString();
    }
}
