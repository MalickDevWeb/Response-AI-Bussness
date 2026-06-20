package sn.autocloser.domain.port.ia;

import java.util.Optional;

/**
 * PORT SORTANT (IA Vision) - Analyse de reçus de paiement
 */
public interface VisionGatewayPort {

    /**
     * Analyse une image de reçu de paiement et en extrait les informations clés.
     * 
     * @param imageUrl L'URL de l'image du reçu (ou l'image en base64)
     * @return Les données extraites (montant, numero, etc.) sous format JSON
     */
    String extraireDonneesRecu(String imageUrl);

    /**
     * Classe utilitaire pour représenter les données extraites par l'IA Vision.
     */
    class ResultatAnalyseRecu {
        private final Double montant;
        private final String numeroDestinataire;
        private final boolean estValide;
        private final String motif;

        public ResultatAnalyseRecu(Double montant, String numeroDestinataire, boolean estValide, String motif) {
            this.montant = montant;
            this.numeroDestinataire = numeroDestinataire;
            this.estValide = estValide;
            this.motif = motif;
        }

        public Double getMontant() { return montant; }
        public String getNumeroDestinataire() { return numeroDestinataire; }
        public boolean isEstValide() { return estValide; }
        public String getMotif() { return motif; }
    }
}
