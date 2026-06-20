package sn.autocloser.domain.valueobject;

/**
 * Value Object - Statut d'une conversation de vente
 * Représente le cycle de vie d'une session client.
 */
public enum StatutConversation {
    EN_COURS,           // L'IA discute avec le client
    ATTENTE_PAIEMENT,   // L'IA a envoyé les coordonnées de paiement, attend le reçu
    PAYE                // Le reçu a été validé par l'IA Vision → vente confirmée
}
