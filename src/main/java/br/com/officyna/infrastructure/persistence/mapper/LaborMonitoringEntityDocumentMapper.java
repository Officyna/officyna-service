package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.monitoring.domain.entity.LaborMonitoringEntity;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborMonitoringDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre LaborMonitoringEntity (domínio) e LaborMonitoringDocument (infraestrutura).
 */
@Component
public class LaborMonitoringEntityDocumentMapper {

    public LaborMonitoringDocument toDocument(LaborMonitoringEntity entity) {
        if (entity == null) {
            return null;
        }
        return LaborMonitoringDocument.builder()
                .id(entity.getId())
                .laborId(entity.getLaborId())
                .laborName(entity.getLaborName())
                .laborDescription(entity.getLaborDescription())
                .averageExecutionTimeInDays(entity.getAverageExecutionTimeInDays())
                .totalExecutions(entity.getTotalExecutions())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public LaborMonitoringEntity toEntity(LaborMonitoringDocument document) {
        if (document == null) {
            return null;
        }
        return LaborMonitoringEntity.builder()
                .id(document.getId())
                .laborId(document.getLaborId())
                .laborName(document.getLaborName())
                .laborDescription(document.getLaborDescription())
                .averageExecutionTimeInDays(document.getAverageExecutionTimeInDays())
                .totalExecutions(document.getTotalExecutions())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

