package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.serviceorder.api.resources.IdListRequest;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplySelectionService {

    SupplyDTO addSupplys(List<IdListRequest> supplysIdList, List<SupplyDetailDTO> supplysDetails){

        return null;
    }
}
