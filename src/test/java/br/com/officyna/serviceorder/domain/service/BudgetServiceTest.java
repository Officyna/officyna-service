package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetServiceTest {

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService();
    }

    @Test
    @DisplayName("Deve calcular o orçamento total somando serviços e suprimentos")
    void calculateBudget_ShouldSumLaborsAndSupplys() {
        // Arrange
        // Adicionando um serviço de 100.00
        LaborDetailDTO labor = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("100.00"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(labor))
                .build();
        
        // Adicionando um suprimento de 50.00 (1 unidade de 50.00)
        SupplyDetailDTO supplyDetail = new SupplyDetailDTO("1", "Peça", "Desc", 1, new BigDecimal("50.00"), null);
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(supplyDetail));

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .labors(labors)
                .supplys(supplys)
                .build();

        // Act
        budgetService.calculateBudget(entity);

        // Assert
        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Deve calcular o total de serviços ignorando os rejeitados")
    void calculateTotalLaborsAmount_ShouldIgnoreRejectedLabors() {
        // Arrange
        LaborDetailDTO labor1 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("100.00"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("50.00"))
                .situation(LaborSituation.REJEITADO)
                .build();
        
        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(labor1, labor2))
                .build();

        // Act
        budgetService.calculateTotalLaborsAmount(labors);

        // Assert
        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Deve calcular o total de suprimentos considerando quantidade e preço unitário")
    void calculateTotalSupplyAmount_ShouldCalculateTotalsCorrectly() {
        // Arrange
        SupplyDetailDTO item1 = new SupplyDetailDTO("1", "Peça A", "Desc", 2, new BigDecimal("25.00"), null);
        SupplyDetailDTO item2 = new SupplyDetailDTO("2", "Peça B", "Desc", 1, new BigDecimal("10.00"), null);
        
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(item1, item2));

        // Act
        budgetService.calculateTotalSupplyAmount(supplys);

        // Assert
        assertThat(item1.getTotalPrice()).isEqualByComparingTo("50.00");
        assertThat(item2.getTotalPrice()).isEqualByComparingTo("10.00");
        assertThat(supplys.getTotalSupplyAmount()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("Deve setar total como zero se não houver serviços ou suprimentos")
    void calculateBudget_WithNullComponents_ShouldSetZero() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder().build();

        budgetService.calculateBudget(entity);

        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular corretamente o preço total unitário por quantidade")
    void calculateTotalPriceForUnitSupply_ShouldReturnCorrectMultiplication() {
        BigDecimal result = budgetService.calculateTotalPriceForUnitSupply(3, new BigDecimal("10.50"));
        assertThat(result).isEqualByComparingTo("31.50");
    }
}
