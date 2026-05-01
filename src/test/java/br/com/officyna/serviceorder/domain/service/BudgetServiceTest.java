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
import java.util.ArrayList;
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

    @Test
    @DisplayName("Deve calcular orçamento com múltiplos serviços aprovados")
    void calculateBudget_WithMultipleApprovedLabors_ShouldSumCorrectly() {
        LaborDetailDTO labor1 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("100.00"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("150.00"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(labor1, labor2))
                .build();

        SupplyDetailDTO supply = new SupplyDetailDTO("1", "Peça", "Desc", 2, new BigDecimal("50.00"), null);
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(supply));

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .labors(labors)
                .supplys(supplys)
                .build();

        budgetService.calculateBudget(entity);

        // 100 + 150 + (2 * 50) = 250 + 100 = 350
        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo("350.00");
    }

    @Test
    @DisplayName("Deve calcular total de suprimentos com quantidade zero")
    void calculateTotalSupplyAmount_WithZeroQuantity_ShouldCalculateZero() {
        SupplyDetailDTO item = new SupplyDetailDTO("1", "Peça", "Desc", 0, new BigDecimal("50.00"), null);

        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(item));

        budgetService.calculateTotalSupplyAmount(supplys);

        assertThat(item.getTotalPrice()).isEqualByComparingTo("0.00");
        assertThat(supplys.getTotalSupplyAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Deve retornar zero quando lista de labors é vazia")
    void calculateTotalLaborsAmount_WithEmptyList_ShouldReturnZero() {
        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(new ArrayList<>())
                .build();

        budgetService.calculateTotalLaborsAmount(labors);

        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve retornar zero quando lista de suprimentos é vazia")
    void calculateTotalSupplyAmount_WithEmptyList_ShouldReturnZero() {
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(new ArrayList<>());

        budgetService.calculateTotalSupplyAmount(supplys);

        assertThat(supplys.getTotalSupplyAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular orçamento com apenas suprimentos")
    void calculateBudget_WithOnlySupplys_ShouldCalculateSupplyOnly() {
        SupplyDetailDTO supply1 = new SupplyDetailDTO("1", "Peça A", "Desc", 3, new BigDecimal("25.00"), null);
        SupplyDetailDTO supply2 = new SupplyDetailDTO("2", "Peça B", "Desc", 2, new BigDecimal("40.00"), null);

        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(supply1, supply2));

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .supplys(supplys)
                .build();

        budgetService.calculateBudget(entity);

        // (3 * 25) + (2 * 40) = 75 + 80 = 155
        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo("155.00");
    }

    @Test
    @DisplayName("Deve calcular orçamento com apenas serviços")
    void calculateBudget_WithOnlyLabors_ShouldCalculateLaborOnly() {
        LaborDetailDTO labor1 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("200.00"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("300.00"))
                .situation(LaborSituation.APROVADO)
                .build();

        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(labor1, labor2))
                .build();

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .labors(labors)
                .build();

        budgetService.calculateBudget(entity);

        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("Deve calcular preço de múltiplas unidades corretamente")
    void calculateTotalPriceForUnitSupply_WithMultipleUnits_ShouldCalculateCorrectly() {
        BigDecimal result1 = budgetService.calculateTotalPriceForUnitSupply(5, new BigDecimal("20.00"));
        BigDecimal result2 = budgetService.calculateTotalPriceForUnitSupply(10, new BigDecimal("15.50"));

        assertThat(result1).isEqualByComparingTo("100.00");
        assertThat(result2).isEqualByComparingTo("155.00");
    }

    @Test
    @DisplayName("Deve ignorar todos os rejeitados e somar apenas aprovados")
    void calculateTotalLaborsAmount_WithAllRejected_ShouldReturnZero() {
        LaborDetailDTO rejected1 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("100.00"))
                .situation(LaborSituation.REJEITADO)
                .build();
        LaborDetailDTO rejected2 = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("50.00"))
                .situation(LaborSituation.REJEITADO)
                .build();

        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(rejected1, rejected2))
                .build();

        budgetService.calculateTotalLaborsAmount(labors);

        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular com valores decimais precisos")
    void calculateBudget_WithDecimalValues_ShouldMaintainPrecision() {
        LaborDetailDTO labor = LaborDetailDTO.builder()
                .laborPrice(new BigDecimal("99.99"))
                .situation(LaborSituation.APROVADO)
                .build();
        LaborsDTO labors = LaborsDTO.builder()
                .laborsDetails(List.of(labor))
                .build();

        SupplyDetailDTO supply = new SupplyDetailDTO("1", "Peça", "Desc", 3, new BigDecimal("33.33"), null);
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(List.of(supply));

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .labors(labors)
                .supplys(supplys)
                .build();

        budgetService.calculateBudget(entity);

        // 99.99 + (3 * 33.33) = 99.99 + 99.99 = 199.98
        assertThat(entity.getTotalBudgetAmount()).isEqualByComparingTo("199.98");
    }
}
