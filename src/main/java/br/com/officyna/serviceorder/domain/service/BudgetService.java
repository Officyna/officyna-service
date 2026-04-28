package br.com.officyna.serviceorder.domain.service;


import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class BudgetService {

    public void calculateBudget(ServiceOrderEntity entity) {
        log.info("Iniciando cálculo de orçamento para a O.S ID: {}", entity.getId());
        if(entity.getLabors()!=null) this.calculateTotalLaborsAmount(entity.getLabors());
        if(entity.getSupplys()!=null) this.calculateTotalSupplyAmount(entity.getSupplys());
        BigDecimal laborTotal = (entity.getLabors() != null && entity.getLabors().getLaborsDetails() != null)
                ? entity.getLabors().getTotalLaborsAmount()
                : BigDecimal.ZERO;

        BigDecimal supplyTotal = (entity.getSupplys() != null && entity.getSupplys().getTotalSupplyAmount() != null)
                ? entity.getSupplys().getTotalSupplyAmount()
                : BigDecimal.ZERO;

        BigDecimal finalTotal = laborTotal.add(supplyTotal);
        
        entity.setTotalBudgetAmount(finalTotal);
        log.info("Cálculo finalizado. Total: {}", finalTotal);
    }

    public void calculateTotalLaborsAmount(LaborsDTO labors){
        if(labors.getLaborsDetails() == null || labors.getLaborsDetails().isEmpty()){
            labors.setTotalLaborsAmount(BigDecimal.ZERO);
            return;
        }
        BigDecimal totalLaborsAmount = labors.getLaborsDetails().stream()
                .filter(item -> item.getSituation() != LaborSituation.REJEITADO)
                .map(LaborDetailDTO::getLaborPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labors.setTotalLaborsAmount(totalLaborsAmount);
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

    public BigDecimal calculateTotalPriceForUnitSupply(Integer quantity, BigDecimal unitPrice) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
