package sn.autocloser.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import sn.autocloser.domain.model.Commercant;
import sn.autocloser.infrastructure.adapter.out.persistence.entity.CommercantJpaEntity;

@Component
public class CommercantPersistenceMapper {

    public CommercantJpaEntity toEntity(Commercant domain) {
        return CommercantJpaEntity.builder()
                .id(domain.getId())
                .nomBoutique(domain.getNomBoutique())
                .telephoneCommercant(domain.getTelephoneCommercant())
                .emailNotification(domain.getEmailNotification())
                .whatsappActif(domain.isWhatsappActif())
                .instagramActif(domain.isInstagramActif())
                .tiktokActif(domain.isTiktokActif())
                .packMediaActif(domain.isPackMediaActif())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Commercant toDomain(CommercantJpaEntity entity) {
        Commercant c = Commercant.reconstituer(
                entity.getId(),
                entity.getNomBoutique(),
                entity.getTelephoneCommercant(),
                entity.getEmailNotification(),
                entity.getCreatedAt()
        );
        if (entity.isWhatsappActif()) c.activerWhatsapp();
        if (entity.isInstagramActif()) c.activerInstagram();
        if (entity.isTiktokActif()) c.activerTiktok();
        if (entity.isPackMediaActif()) c.activerPackMedia();
        return c;
    }
}
