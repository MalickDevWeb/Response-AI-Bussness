package sn.autocloser.infrastructure.adapter.in.web.mapper;

import org.springframework.stereotype.Component;
import sn.autocloser.application.port.command.RouterMessageCommand;
import sn.autocloser.domain.port.ia.RechercheRagPort;
import sn.autocloser.domain.valueobject.Plateforme;
import sn.autocloser.infrastructure.adapter.in.web.dto.request.WebhookMessageRequest;
import sn.autocloser.infrastructure.adapter.in.web.dto.response.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de la couche Web (Input Adapter).
 * Responsabilité unique : convertir entre le monde HTTP (DTOs) et le monde du Domaine.
 * Il ne connaît ni JPA, ni Kafka — uniquement les objets du domaine et les DTOs REST.
 *
 * Pattern identique au StockRestMapper du projet stock.
 */
@Component
public class WebhookRestMapper {

    /**
     * DTO entrant (WebhookMessageRequest) → Commande du domaine (RouterMessageCommand).
     * C'est l'entrée du système : ce que l'API reçoit d'Evolution API (WhatsApp/Instagram).
     */
    public RouterMessageCommand toCommand(WebhookMessageRequest request) {
        return new RouterMessageCommand(
                request.expediteurId(),
                request.commercantTelephone(),
                request.contenu(),
                request.typeMedia(),
                request.mediaUrl(),
                Plateforme.valueOf(request.plateforme())
        );
    }

    /**
     * Résultat domaine (String) → DTO de réponse HTTP standard.
     */
    public ApiResponse toApiResponse(String resultatMetier) {
        return ApiResponse.ok("Message traité avec succès", resultatMetier);
    }

    /**
     * Liste de résultats RAG → format texte lisible pour la réponse HTTP.
     * Transforme les produits trouvés en une réponse JSON structurée.
     */
    public ApiResponse toRagResponse(List<RechercheRagPort.ResultatRag> resultats, String requete) {
        if (resultats.isEmpty()) {
            return ApiResponse.ok(
                "Aucun produit correspondant trouvé pour : \"" + requete + "\"",
                List.of()
            );
        }

        List<ProduitDto> produitDtos = resultats.stream()
                .map(r -> new ProduitDto(
                        r.nom(),
                        r.description(),
                        r.prix(),
                        r.stock(),
                        Math.round(r.scoreSimilarite() * 100) + "%"
                ))
                .collect(Collectors.toList());

        return ApiResponse.ok(
            resultats.size() + " produit(s) trouvé(s) pour : \"" + requete + "\"",
            produitDtos
        );
    }

    /**
     * DTO de présentation d'un produit dans la réponse REST.
     */
    public record ProduitDto(
            String nom,
            String description,
            double prix,
            int stock,
            String pertinence
    ) {}
}
