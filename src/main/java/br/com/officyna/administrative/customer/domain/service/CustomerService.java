package br.com.officyna.customer.domain.service;

import br.com.officyna.customer.api.resources.CustomerRequest;
import br.com.officyna.customer.api.resources.CustomerResponse;
import br.com.officyna.customer.domain.CustomerEntity;
import br.com.officyna.customer.domain.mapper.CustomerMapper;
import br.com.officyna.customer.repository.CustomerRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerResponse> findAll() {
        return customerRepository.findByActiveTrue()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse findById(String id) {
        return customerMapper.toResponse(findEntityById(id));
    }

    public CustomerResponse findByDocument(String document) {
        return customerRepository.findByDocument(document)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found with document: " + document));
    }

    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByDocument(request.document())) {
            throw new DomainException("Document already registered: " + request.document());
        }
        CustomerEntity entity = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    public CustomerResponse update(String id, CustomerRequest request) {
        CustomerEntity entity = findEntityById(id);

        boolean documentChanged = !entity.getDocument().equals(request.document());
        if (documentChanged && customerRepository.existsByDocument(request.document())) {
            throw new DomainException("Document already registered: " + request.document());
        }

        customerMapper.updateEntity(entity, request);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    public void delete(String id) {
        CustomerEntity entity = findEntityById(id);
        entity.setActive(false);
        customerRepository.save(entity);
    }

    // Utility method for internal use (e.g. VehicleService)
    public CustomerEntity findEntityById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Customer", id));
    }
}