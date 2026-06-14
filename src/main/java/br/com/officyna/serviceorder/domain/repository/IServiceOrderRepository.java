package br.com.officyna.serviceorder.domain.repository;

import br.com.officyna.infrastructure.persistence.repository.IRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para ServiceOrderEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface IServiceOrderRepository extends IRepository<ServiceOrderEntity, String> {
    List<ServiceOrderEntity> findByLaborIdWithCompletedExecutions(String laborId);
    Optional<ServiceOrderEntity> findByServiceOrderNumber(Long serviceOrderNumber);
    List<ServiceOrderEntity> findByCustomerId(String id);
}

