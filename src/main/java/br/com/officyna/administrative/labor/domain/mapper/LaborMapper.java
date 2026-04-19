package br.com.officyna.administrative.labor.domain.mapper;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.LaborEntity;
import org.springframework.stereotype.Component;

@Component
public class LaborMapper {

    public LaborEntity toEntity(LaborRequest request) {
        return LaborEntity.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .executionTimeInDays(request.executionTimeInDays())
                .build();
    }

    public LaborResponse toResponse(LaborEntity entity) {
        return new LaborResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getExecutionTimeInDays(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntity(LaborEntity entity, LaborRequest request) {
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPrice(request.price());
        entity.setExecutionTimeInDays(request.executionTimeInDays());
    }
}
