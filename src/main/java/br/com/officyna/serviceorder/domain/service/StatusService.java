package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusService {

    public void validateStatusForStartExecution(ServiceOrderEntity entity) {
        if (!(ServiceOrderStatus.APROVADA.equals(entity.getStatus()) || ServiceOrderStatus.EM_EXECUCAO.equals(entity.getStatus()))) {
            log.warn("Falha na validação: Tentativa de operar serviços em O.S. com status inválido. Status atual: {}, O.S. ID: {}", entity.getStatus(), entity.getId());
            throw new DomainException("Um serviço só pode ser iniciado ou finalizado se o status da ordem de serviço for APROVADA ou EM EXECUÇÃO.");
        }
        
        LaborsDTO labors = entity.getLabors();
        if (labors == null || labors.getLaborsDetails() == null || labors.getLaborsDetails().isEmpty()) {
            log.warn("Falha na validação: Tentativa de iniciar execução em O.S. sem serviços cadastrados. O.S. ID: {}", entity.getId());
            throw new DomainException("A ordem de serviço não possui serviços cadastrados.");
        }
    }

    public void updateStatus(ServiceOrderEntity entity, ServiceOrderStatus status){
        log.debug("Validando transição de status para a O.S. ID: {}. De {} para {}", entity.getId(), entity.getStatus(), status);
        if(status.equals(entity.getStatus())) {
            throw new DomainException("A Ordem de Serviço já foi processada com status " + status.getStatusName() + ".");
        }else if(status.equals(ServiceOrderStatus.RECEBIDA) && entity.getStatus() != null){
            throw new DomainException("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
        }else if(status.equals(ServiceOrderStatus.EM_DIAGNOSTICO)){
            if (!ServiceOrderStatus.RECEBIDA.equals(entity.getStatus())) {
                throw new DomainException("Para iniciar o diagnóstico, a O.S. deve estar no status RECEBIDA.");
            }
        }else if(status.equals(ServiceOrderStatus.AGUARDANDO_APROVACAO)){
            if (!ServiceOrderStatus.EM_DIAGNOSTICO.equals(entity.getStatus())) {
                throw new DomainException("Para aguardar aprovação, a O.S. deve ter passado pelo diagnóstico.");
            }
        }else if(status.equals(ServiceOrderStatus.APROVADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
            }
            this.validateLaborsForAProvalStatus(entity);
        }else if(status.equals(ServiceOrderStatus.EM_EXECUCAO)){
            if (!ServiceOrderStatus.APROVADA.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens APROVADAS podem entrar em execução.");
            }
        }else if(status.equals(ServiceOrderStatus.FINALIZADA)){
            if (!ServiceOrderStatus.EM_EXECUCAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens EM EXECUÇÃO podem ser finalizadas.");
            }
            this.validateLaborsForFinishServiceOrder(entity);
        }else if(status.equals(ServiceOrderStatus.ENTREGUE)){
            if (!ServiceOrderStatus.FINALIZADA.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordes FINALIZADAS podem ser consideradas entregues");
            }
        }else if(status.equals(ServiceOrderStatus.RECUSADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser recusadas.");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case RECEBIDA -> entity.setRegistrationDate(now);
                case EM_DIAGNOSTICO -> entity.setDiagnosisStartDate(now);
                case AGUARDANDO_APROVACAO -> entity.setClientSendDate(now);
                case APROVADA -> entity.setApprovalDate(now);
                case EM_EXECUCAO -> entity.setExecutionStartDate(now);
                case ENTREGUE -> entity.setDeliveryDate(now);
                case FINALIZADA -> entity.setFinalizationDate(now);
                case RECUSADA -> entity.setRefuseDate(now);
        }
        entity.setStatus(status);
    }

    private void validateLaborsForFinishServiceOrder(ServiceOrderEntity entity) {
        log.debug("Verificando se todos os serviços foram concluídos para finalizar a O.S. ID: {}", entity.getId());
        List<LaborDetailDTO> labors = entity.getLabors().getLaborsDetails();
        labors.forEach(item -> {
            if (item.getStartDate() == null || item.getEndDate() == null) {
                log.error("Impossível finalizar O.S. ID: {}. Serviço ID: {} ainda está em aberto.", entity.getId(), item.getLaborId());
                throw new DomainException("Não é possível finalizar ordem com serviços em aberto");
            }
        });
    }

    private void validateLaborsForAProvalStatus(ServiceOrderEntity entity){
        if(entity.getLabors() !=null && entity.getLabors().getLaborsDetails() != null){
            entity.getLabors().getLaborsDetails()
                    .forEach(item -> {
                        if(item.getSituation().equals(LaborSituation.PENDING)){
                            throw  new DomainException("Todos os serviços devem ser analisados e rejeitados ou aprovados");
                        }
                    });
        } else{
            throw new DomainException("A O.S precisa ter ao menos um serviço");
        }
    }
}
