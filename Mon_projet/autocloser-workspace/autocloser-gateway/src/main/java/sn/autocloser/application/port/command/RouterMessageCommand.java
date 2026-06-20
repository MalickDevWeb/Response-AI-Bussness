package sn.autocloser.application.port.command;

import sn.autocloser.domain.valueobject.Plateforme;

/**
 * COMMANDE : Message entrant d'un utilisateur (client ou commerçant admin).
 * C'est l'objet qui déclenche le routage IA.
 */
public record RouterMessageCommand(
        String expediteurId,     // Numéro WhatsApp ou ID Instagram de l'expéditeur
        String commercantTelephone, // Téléphone du commerçant propriétaire du canal
        String contenu,          // Texte du message (déjà transcrit si c'était un vocal)
        String typeMedia,        // "text", "audio", "image"
        String mediaUrl,         // URL du fichier média (reçu de paiement, vocal, etc.)
        Plateforme plateforme
) {}
