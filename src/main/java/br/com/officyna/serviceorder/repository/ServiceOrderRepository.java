package br.com.officyna.serviceorder.repository;

import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServiceOrderRepository extends MongoRepository<ServiceOrderEntity, String> {


}
