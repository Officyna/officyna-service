package br.com.officyna.administrative.labor.domain.mapper;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import org.springframework.stereotype.Component;

@Component
public class LaborMapper {

    public Labor toEntity(LaborRequest request) {
        return Labor.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .executionTimeInDays(request.executionTimeInDays())
                .active(request.active())
                .build();
    }

    public LaborResponse toResponse(Labor entity) {
        return new LaborResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getExecutionTimeInDays(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getActive()
        );
    }

    public void updateEntity(Labor entity, LaborRequest request) {
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setPrice(request.price());
        entity.setActive(request.active());
        entity.setExecutionTimeInDays(request.executionTimeInDays());
    }
}
