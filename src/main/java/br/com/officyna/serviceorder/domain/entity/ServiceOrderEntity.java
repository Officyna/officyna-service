package br.com.officyna.serviceorder.domain.entity;

import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.NONE;

@Document(collection = "service_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderEntity {

    public static final String SEQUENCE_NAME = "service_orders_sequence";

    @Id
    private String id;

    private Long serviceOrderNumber;

    private VehicleDTO vehicle;

    private CustomerDTO customer;

    private MechanicDTO mechanic;

    private LaborsDTO labors = new LaborsDTO(new java.util.ArrayList<>(), BigDecimal.ZERO);

    private SupplyDTO supplys = new SupplyDTO(new java.util.ArrayList<>(), BigDecimal.ZERO);

    private LocalDateTime registrationDate;

    private LocalDateTime DiagnosisStartDate;

    private LocalDateTime clientSendDate;

    private LocalDateTime approvalDate;

    private LocalDateTime executionStartDate;

    private LocalDateTime finalizationDate;

    private LocalDateTime deliveryDate;

    private LocalDateTime refuseDate;

    private ServiceOrderStatus status;

    private String informationText;

    private BigDecimal totalBudgetAmount;

    @Setter(NONE)
    @CreatedDate
    private LocalDateTime createdAt;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void setStatus(ServiceOrderStatus status){
        if (status == null) {
            this.status = null;
            return;
        }

        if (this.status == null) {
            setTimeDateByStatus(status);
            this.status = status;
            return;
        }

        if(status.equals(this.status)) {
            throw new DomainException("A Ordem de Serviço já foi processada com status " + status.getStatusName() + ".");
        }else if(status.equals(ServiceOrderStatus.RECEBIDA) && this.status != null){
            throw new DomainException("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
        }else if(status.equals(ServiceOrderStatus.EM_DIAGNOSTICO)){
            if (!ServiceOrderStatus.RECEBIDA.equals(this.status)) {
                throw new DomainException("Para iniciar o diagnóstico, a O.S. deve estar no status RECEBIDA.");
            }
        }else if(status.equals(ServiceOrderStatus.AGUARDANDO_APROVACAO)){
            if (!ServiceOrderStatus.EM_DIAGNOSTICO.equals(this.status)) {
                throw new DomainException("Para aguardar aprovação, a O.S. deve ter passado pelo diagnóstico.");
            }
        }else if(status.equals(ServiceOrderStatus.APROVADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(this.status)) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
            }
            this.validateLaborsForAProvalStatus();
        }else if(status.equals(ServiceOrderStatus.EM_EXECUCAO)){
            if (!ServiceOrderStatus.APROVADA.equals(this.status)) {
                throw new DomainException("Apenas ordens APROVADAS podem entrar em execução.");
            }
        }else if(status.equals(ServiceOrderStatus.FINALIZADA)){
            if (!ServiceOrderStatus.EM_EXECUCAO.equals(this.status)) {
                throw new DomainException("Apenas ordens EM EXECUÇÃO podem ser finalizadas.");
            }
            this.validateLaborsForFinishServiceOrder();
        }else if(status.equals(ServiceOrderStatus.ENTREGUE)){
            if (!ServiceOrderStatus.FINALIZADA.equals(this.status)) {
                throw new DomainException("Apenas ordes FINALIZADAS podem ser consideradas entregues");
            }
        }else if(status.equals(ServiceOrderStatus.RECUSADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(this.status)) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser recusadas.");
            }
        }
        setTimeDateByStatus(status);
        this.status = status;
    }

    private void validateLaborsForFinishServiceOrder() {
        List<LaborDetailDTO> labors = this.getLabors().getLaborsDetails().stream()
                .filter(item->item.getSituation()!= LaborSituation.REJEITADO)
                .toList();
        labors.forEach(item -> {
            if (item.getStartDate() == null || item.getEndDate() == null) {
                throw new DomainException("Não é possível finalizar ordem com serviços em aberto");
            }
        });
    }

    private void validateLaborsForAProvalStatus(){
        if(this.getLabors() !=null && this.getLabors().getLaborsDetails() != null){
            this.getLabors().getLaborsDetails()
                    .forEach(item -> {
                        if(item.getSituation().equals(LaborSituation.PENDENTE)){
                            throw  new DomainException("Todos os serviços devem ser analisados e rejeitados ou aprovados");
                        }
                    });
        } else{
            throw new DomainException("A O.S precisa ter ao menos um serviço");
        }
    }

    private void setTimeDateByStatus(ServiceOrderStatus status){
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case RECEBIDA -> this.setRegistrationDate(now);
            case EM_DIAGNOSTICO -> this.setDiagnosisStartDate(now);
            case AGUARDANDO_APROVACAO -> this.setClientSendDate(now);
            case APROVADA -> this.setApprovalDate(now);
            case EM_EXECUCAO -> this.setExecutionStartDate(now);
            case ENTREGUE -> this.setDeliveryDate(now);
            case FINALIZADA -> this.setFinalizationDate(now);
            case RECUSADA -> this.setRefuseDate(now);
        }
    }

    public void setLabors(LaborsDTO labors) {
        this.calculateBudget();
        this.labors = labors;
    }

    public void setSupplys(SupplyDTO supplys) {
        this.calculateBudget();
        this.supplys = supplys;
    }

    public void calculateBudget() {
        if(this.getLabors()!=null) this.calculateTotalLaborsAmount();
        if(this.getSupplys()!=null) this.calculateTotalSupplyAmount();
        BigDecimal laborTotal = (this.getLabors() != null && this.getLabors().getLaborsDetails() != null)
                ? this.getLabors().getTotalLaborsAmount()
                : BigDecimal.ZERO;

        BigDecimal supplyTotal = (this.getSupplys() != null && this.getSupplys().getTotalSupplyAmount() != null)
                ? this.getSupplys().getTotalSupplyAmount()
                : BigDecimal.ZERO;

        BigDecimal finalTotal = laborTotal.add(supplyTotal);

        this.setTotalBudgetAmount(finalTotal);
    }

    private void calculateTotalLaborsAmount(){
        if(this.labors.getLaborsDetails() == null || this.labors.getLaborsDetails().isEmpty()){
            this.labors.setTotalLaborsAmount(BigDecimal.ZERO);
            return;
        }
        BigDecimal totalLaborsAmount = this.labors.getLaborsDetails().stream()
                .filter(item -> item.getSituation() != LaborSituation.REJEITADO)
                .map(LaborDetailDTO::getLaborPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.labors.setTotalLaborsAmount(totalLaborsAmount);
    }

    private void calculateTotalSupplyAmount() {
        if (this.supplys.getSupplysDetails() == null || this.supplys.getSupplysDetails().isEmpty()) {
            this.supplys.setTotalSupplyAmount(BigDecimal.ZERO);
        } else {
            this.supplys.setTotalSupplyAmount(BigDecimal.ZERO);
            for (SupplyDetailDTO item : supplys.getSupplysDetails()){
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                this.supplys.setTotalSupplyAmount(this.supplys.getTotalSupplyAmount().add(item.getTotalPrice()));
            }
        }
    }
}
