package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.serviceorder.api.resources.IdListRequest;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaborSelectionService {

    private final LaborService laborService;

    LaborsDTO addLabors(List<IdListRequest> laborsIdList, List<LaborDetailDTO> laborsDetails) {
        List<LaborDetailDTO> allLabors = new ArrayList<>(laborsDetails != null ? laborsDetails : List.of());

        if (laborsIdList != null && !laborsIdList.isEmpty()) {
            List<LaborDetailDTO> newLabors = laborsIdList.stream()
                    .map(id -> {
                        LaborResponse response = laborService.findById(id.getId());
                        return new LaborDetailDTO(response.id(), response.name(), response.description(), response.price(), null, null);
                    })
                    .toList();
            allLabors.addAll(newLabors);
        }
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(allLabors);
        this.calculateTotalLaborsAmount(labors);
        return labors;
    }

    public void calculateTotalLaborsAmount(LaborsDTO labors){
        if(labors.getLaborsDetails() == null || labors.getLaborsDetails().isEmpty()){
            labors.setTotalLaborsAmount(BigDecimal.ZERO);
            return;
        }
        BigDecimal totalLaborsAmount = labors.getLaborsDetails().stream()
                .map(LaborDetailDTO::getLaborPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labors.setTotalLaborsAmount(totalLaborsAmount);
    }
}
