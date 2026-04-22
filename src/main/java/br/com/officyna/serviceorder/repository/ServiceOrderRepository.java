package br.com.officyna.serviceorder.repository;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository extends MongoRepository<ServiceOrderEntity, String> {

    @Query("{ 'labors.laborsDetails': { $elemMatch: { 'laborId': ?0, 'startDate': { $ne: null }, 'endDate': { $ne: null } } } }")
    List<ServiceOrderEntity> findByLaborIdWithCompletedExecutions(String laborId);

    Optional<ServiceOrderEntity> findByServiceOrderNumber(Long serviceOrderNumber);

    List<ServiceOrderEntity> findByCustomerId(String id);

}