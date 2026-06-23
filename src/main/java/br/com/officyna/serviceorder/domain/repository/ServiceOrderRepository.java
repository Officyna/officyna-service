package br.com.officyna.serviceorder.domain.repository;

import br.com.officyna.serviceorder.domain.entity.ServiceOrder;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para ServiceOrderEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface ServiceOrderRepository {
    List<ServiceOrder> findByLaborIdWithCompletedExecutions(String laborId);
    Optional<ServiceOrder> findByServiceOrderNumber(Long serviceOrderNumber);
    List<ServiceOrder> findByCustomerId(String id);
    ServiceOrder save(ServiceOrder entity);
    Optional<ServiceOrder> findById(String id);
    List<ServiceOrder> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

