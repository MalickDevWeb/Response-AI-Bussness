package sn.autocloser.domain.port.messagerie;

/**
 * PORT SORTANT (Out) - Envoi de messages vers les clients.
 * Permet au domaine d'envoyer la réponse générée par l'IA vers WhatsApp, Instagram, etc.
 */
public interface EnvoiMessagePort {
    
    /**
     * Envoie un message texte à un client.
     *
     * @param numeroDestinataire Le numéro WhatsApp ou ID Instagram du client
     * @param contenu Le message à envoyer
     * @param instanceNom Le nom de l'instance Evolution API (ex: nom_boutique)
     */
    void envoyerMessage(String numeroDestinataire, String contenu, String instanceNom);
}
