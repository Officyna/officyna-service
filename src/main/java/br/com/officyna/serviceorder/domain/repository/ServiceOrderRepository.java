package br.com.officyna.serviceorder.domain.repository;

import br.com.officyna.infrastructure.persistence.repository.IRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para ServiceOrderEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface ServiceOrderRepository extends IRepository<ServiceOrder, String> {
    List<ServiceOrder> findByLaborIdWithCompletedExecutions(String laborId);
    Optional<ServiceOrder> findByServiceOrderNumber(Long serviceOrderNumber);
    List<ServiceOrder> findByCustomerId(String id);
}

