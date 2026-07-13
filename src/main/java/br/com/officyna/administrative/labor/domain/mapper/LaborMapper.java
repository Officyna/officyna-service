package br.com.officyna.administrative.labor.domain.mapper;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
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
}