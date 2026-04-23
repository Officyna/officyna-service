package br.com.officyna.serviceorder.domain.service;


import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class BudgetService {

    public void calculateBudget(ServiceOrderEntity entity) {
        log.info("Iniciando cálculo de orçamento para a O.S ID: {}", entity.getId());

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

}
