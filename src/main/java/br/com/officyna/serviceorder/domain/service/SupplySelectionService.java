package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.api.resources.LaborsRequest;
import br.com.officyna.serviceorder.api.resources.SupplysRequest;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplySelectionService {

    private final SupplyService service;

    SupplyDTO addSupplys(List<SupplysRequest> supplysIdList, List<SupplyDetailDTO> supplysDetails){
        List<SupplyDetailDTO> allSupplys = new ArrayList<>(supplysDetails != null ? supplysDetails : List.of());

        if(supplysIdList != null && !supplysIdList.isEmpty()){
            List<SupplyDetailDTO> newSupplys = supplysIdList.stream()
                    .map(id -> {
                SupplyResponse response = service.findById(id.getId());
                return new SupplyDetailDTO(response.id(),
                        response.name(),
                        response.description(),
                        id.getQuantity(),
                        response.salePrice(),
                        this.calculateTotalPriceForUnitSupply(id.getQuantity(), response.salePrice())
                );
            }).toList();
            allSupplys.addAll(newSupplys);
        }
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(allSupplys);
        this.calculateTotalSupplyAmount(supplys);
        return supplys;
    }

    public void removeSupply(SupplyDTO supplys, String supplyId){
        if(supplys.getSupplysDetails().isEmpty() || supplyId == null)
            throw new DomainException("A Ordem de Serviço não possui suprimentos cadastrados.");
        supplys.getSupplysDetails().removeIf(supply -> supply.getId().equals(supplyId));
        this.calculateTotalSupplyAmount(supplys);
    }

    public void calculateTotalSupplyAmount(SupplyDTO supplys) {
        if (supplys.getSupplysDetails() == null || supplys.getSupplysDetails().isEmpty()) {
            supplys.setTotalSupplyAmount(BigDecimal.ZERO);
        } else {
            supplys.setTotalSupplyAmount(BigDecimal.ZERO);
            for (SupplyDetailDTO item : supplys.getSupplysDetails()){
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                supplys.setTotalSupplyAmount(supplys.getTotalSupplyAmount().add(item.getTotalPrice()));
            }
        }
    }

    public BigDecimal calculateTotalPriceForUnitSupply(Integer quantity, BigDecimal unitPrice){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
