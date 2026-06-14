package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre LaborEntity (domínio) e LaborDocument (infraestrutura).
 */
@Component
public class LaborEntityDocumentMapper {

    public LaborDocument toDocument(Labor entity) {
        if (entity == null) {
            return null;
        }
        return LaborDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .executionTimeInDays(entity.getExecutionTimeInDays())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Labor toEntity(LaborDocument document) {
        if (document == null) {
            return null;
        }
        return Labor.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .price(document.getPrice())
                .executionTimeInDays(document.getExecutionTimeInDays())
                .active(document.getActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

