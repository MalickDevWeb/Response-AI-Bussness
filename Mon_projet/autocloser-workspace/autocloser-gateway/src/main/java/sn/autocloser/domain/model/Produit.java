package sn.autocloser.domain.model;

import sn.autocloser.domain.exception.PrixInvalideException;
import sn.autocloser.domain.exception.StockInsuffisantException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DOMAINE - Entité Produit (Catalogue + RAG)
 * Contient toutes les règles métier liées aux produits du commerçant.
 * RÈGLE : Aucune annotation Spring/JPA ici.
 */
public class Produit {

    private final UUID id;
    private final UUID commercantId;
    private final String nom;
    private String description;
    private BigDecimal prix;
    private int stock;
    private boolean actif;
    private String imageUrl;

    private Produit(UUID id, UUID commercantId, String nom, String description,
                    BigDecimal prix, int stock, String imageUrl) {
        this.id = id;
        this.commercantId = commercantId;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.actif = true;
        this.imageUrl = imageUrl;
    }

    // ---- FACTORY METHOD ----
    public static Produit creer(UUID commercantId, String nom, String description,
                                BigDecimal prix, int stock, String imageUrl) {
        if (prix == null || prix.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrixInvalideException("Le prix doit être supérieur à 0.");
        }
        if (stock < 0) {
            throw new StockInsuffisantException("Le stock ne peut pas être négatif.");
        }
        return new Produit(UUID.randomUUID(), commercantId, nom, description, prix, stock, imageUrl);
    }

    // ---- RÈGLES MÉTIER ----

    public void mettreAJourPrix(BigDecimal nouveauPrix) {
        if (nouveauPrix == null || nouveauPrix.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PrixInvalideException("Le nouveau prix doit être supérieur à 0.");
        }
        this.prix = nouveauPrix;
    }

    public void mettreEnRuptureDeStock() {
        this.stock = 0;
        this.actif = false;
    }

    public void reapprovisionner(int quantite) {
        if (quantite <= 0) throw new IllegalArgumentException("La quantité doit être positive.");
        this.stock += quantite;
        this.actif = true;
    }

    public boolean estDisponible() {
        return this.actif && this.stock > 0;
    }

    // ---- GETTERS ----
    public UUID getId() { return id; }
    public UUID getCommercantId() { return commercantId; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public BigDecimal getPrix() { return prix; }
    public int getStock() { return stock; }
    public boolean isActif() { return actif; }
    public String getImageUrl() { return imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
