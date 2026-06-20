package sn.autocloser.domain.port.messagerie;

/**
 * PORT SORTANT - Gestion des instances WhatsApp (Onboarding)
 */
public interface GestionInstancePort {

    /**
     * Crée une instance sur le serveur de messagerie (ex: Evolution API)
     * et retourne le QR Code en format Base64 pour que le commerçant le scanne.
     *
     * @param instanceNom Le nom de l'instance (ex: boutiquepmt)
     * @return Le QR Code en Base64
     */
    String creerInstanceEtGenererQr(String instanceNom);
}
