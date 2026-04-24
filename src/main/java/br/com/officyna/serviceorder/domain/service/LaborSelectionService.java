package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.serviceorder.api.resources.LaborsRequest;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaborSelectionService {

    private final LaborService laborService;

    LaborsDTO addLabors(List<LaborsRequest> laborsIdList, List<LaborDetailDTO> laborsDetails) {
        List<LaborDetailDTO> allLabors = new ArrayList<>(laborsDetails != null ? laborsDetails : List.of());

        if (laborsIdList != null && !laborsIdList.isEmpty()) {
            List<LaborDetailDTO> newLabors = laborsIdList.stream()
                    .map(id -> {
                        LaborResponse response = laborService.findById(id.getId());
                        return new LaborDetailDTO(
                                response.id(),
                                response.name(),
                                response.description(),
                                response.price(),
                                null,
                                null,
                                LaborSituation.PENDING,
                                LocalDateTime.now());
                    })
                    .toList();
            allLabors.addAll(newLabors);
        }
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(allLabors);
        return labors;
    }
}
