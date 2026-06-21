package br.com.officyna.monitoring.domain.repository;

import br.com.officyna.infrastructure.persistence.repository.IRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;

import java.util.Optional;

/**
 * Interface de repositório pura para LaborMonitoringEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface LaborMonitoringRepository extends IRepository<LaborMonitoring, String> {
    Optional<LaborMonitoring> findByLaborId(String laborId);
}

