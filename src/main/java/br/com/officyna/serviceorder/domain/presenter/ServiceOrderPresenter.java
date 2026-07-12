package br.com.officyna.serviceorder.domain.presenter;

import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ServiceOrderPresenter {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public ServiceOrderResponse toResponse(ServiceOrder entity) {
        return new ServiceOrderResponse(
                entity.getId(),
                entity.getServiceOrderNumber().toString(),
                entity.getCustomer(),
                entity.getMechanic(),
                entity.getVehicle(),
                entity.getLabors(),
                entity.getSupplys(),
                entity.getInformationText(),
                entity.getStatus().getStatusName(),
                this.getStatusDateByLastStatus(entity),
                this.formatMoney(entity.getTotalBudgetAmount()),
                this.formatLocalDateTime(entity.getCreatedAt())
        );
    }

    private String getStatusDateByLastStatus(ServiceOrder entity) {
        LocalDateTime statusDate = null;
        switch (entity.getStatus()) {
            case RECEBIDA -> statusDate = entity.getRegistrationDate();
            case EM_DIAGNOSTICO -> statusDate = entity.getDiagnosisStartDate();
            case AGUARDANDO_APROVACAO -> statusDate = entity.getClientSendDate();
            case APROVADA -> statusDate = entity.getApprovalDate();
            case EM_EXECUCAO -> statusDate = entity.getExecutionStartDate();
            case ENTREGUE -> statusDate = entity.getDeliveryDate();
            case FINALIZADA -> statusDate = entity.getFinalizationDate();
            case RECUSADA -> statusDate = entity.getRefuseDate();
        }
        return this.formatLocalDateTime(statusDate);
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private String formatMoney(BigDecimal value) {
        return String.format(PT_BR, "R$ %.2f", value);
    }
}