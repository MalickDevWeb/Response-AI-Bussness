package sn.autocloser.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * DOMAINE - Entité Commercant (Le client du SaaS)
 */
public class Commercant {

    private final UUID id;
    private final String nomBoutique;
    private final String telephoneCommercant;
    private final String emailNotification;
    private boolean whatsappActif;
    private boolean instagramActif;
    private boolean tiktokActif;
    private boolean packMediaActif;
    private final Instant createdAt;

    private Commercant(UUID id, String nomBoutique, String telephoneCommercant,
                       String emailNotification, Instant createdAt) {
        this.id = id;
        this.nomBoutique = nomBoutique;
        this.telephoneCommercant = telephoneCommercant;
        this.emailNotification = emailNotification;
        this.whatsappActif = false;
        this.instagramActif = false;
        this.tiktokActif = false;
        this.packMediaActif = false;
        this.createdAt = createdAt;
    }

    public static Commercant creer(String nomBoutique, String telephone, String email) {
        if (telephone == null || !telephone.startsWith("+"))
            throw new IllegalArgumentException("Le téléphone doit être au format international (+221...)");
        return new Commercant(UUID.randomUUID(), nomBoutique, telephone, email, Instant.now());
    }

    public static Commercant reconstituer(UUID id, String nomBoutique, String telephone, String email, Instant createdAt) {
        return new Commercant(id, nomBoutique, telephone, email, createdAt);
    }

    public void activerWhatsapp() { this.whatsappActif = true; }
    public void activerInstagram() { this.instagramActif = true; }
    public void activerTiktok() { this.tiktokActif = true; }
    public void activerPackMedia() { this.packMediaActif = true; }

    public UUID getId() { return id; }
    public String getNomBoutique() { return nomBoutique; }
    public String getTelephoneCommercant() { return telephoneCommercant; }
    public String getEmailNotification() { return emailNotification; }
    public boolean isWhatsappActif() { return whatsappActif; }
    public boolean isInstagramActif() { return instagramActif; }
    public boolean isTiktokActif() { return tiktokActif; }
    public boolean isPackMediaActif() { return packMediaActif; }
    public Instant getCreatedAt() { return createdAt; }
}
