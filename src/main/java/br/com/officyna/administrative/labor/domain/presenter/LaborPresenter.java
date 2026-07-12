package br.com.officyna.administrative.labor.domain.presenter;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import org.springframework.stereotype.Component;

@Component
public class LaborPresenter {

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
}