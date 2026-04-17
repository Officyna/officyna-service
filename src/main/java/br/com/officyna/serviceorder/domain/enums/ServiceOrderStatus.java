package br.com.officyna.serviceorder.domain.enums;

import lombok.Getter;

@Getter
public enum ServiceOrderStatus {
    RECEBIDA(1, "Recebida"),
    EM_DIAGNOSTICO(2, "Em diagnóstico"),
    AGUARDANDO_APROVACAO(3, "Aguardando aprovação"),
    EM_EXECUCAO(4, "Em execução"),
    FINALIZADA(5, "Finalizada"),
    APROVADA(6, "Aprovada"),
    ENTREGUE(7,"Entregue"),
    RECUSADA(8, "Recusada");

    private final Integer idStatus;

    private final String statusName;

    ServiceOrderStatus (Integer idStatus, String statusName){
        this.idStatus = idStatus;
        this.statusName = statusName;
    }

    public static ServiceOrderStatus fromId(Integer id) {
        for (ServiceOrderStatus status : ServiceOrderStatus.values()) {
            if (status.idStatus.equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ServiceOrderStatus ID: " + id);
    }
}
