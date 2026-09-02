package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.serviceorder.api.resources.LaborsRequest;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LaborSelectionService {

    private final LaborService laborService;

    public LaborSelectionService(LaborService laborService) {
        this.laborService = laborService;
    }

    LaborsDTO addLabors(
            List<LaborsRequest> laborsIdList,
            List<LaborDetailDTO> laborsDetails) {

        log.info(
                "Adding labors to service order. Requested labors: {}",
                laborsIdList != null ? laborsIdList.size() : 0
        );

        List<LaborDetailDTO> allLabors =
                new ArrayList<>(laborsDetails != null ? laborsDetails : List.of());

        if (laborsIdList != null && !laborsIdList.isEmpty()) {

            List<LaborDetailDTO> newLabors = laborsIdList.stream()
                    .map(id -> {

                        log.debug(
                                "Finding labor by id: {}",
                                id.getId()
                        );

                        Labor labor = laborService.findById(id.getId());

                        log.debug(
                                "Labor found: id={}, name={}",
                                labor.getId(),
                                labor.getName()
                        );

                        return new LaborDetailDTO(
                                labor.getId(),
                                labor.getName(),
                                labor.getDescription(),
                                labor.getPrice(),
                                null,
                                null,
                                LaborSituation.PENDENTE,
                                LocalDateTime.now()
                        );
                    })
                    .toList();

            allLabors.addAll(newLabors);
        }

        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(allLabors);

        log.info(
                "Labors added successfully. Total labors: {}",
                allLabors.size()
        );

        return labors;
    }
}