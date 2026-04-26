package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.validation.DocumentUtils;
import br.com.officyna.administrative.customer.repository.CustomerRepository;
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
        String normalized = DocumentUtils.normalize(document);
        return customerRepository.findByDocument(normalized)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found with document: " + normalized));
    }

    public CustomerResponse create(CustomerRequest request) {
        String normalized = DocumentUtils.normalize(request.document());
        if (customerRepository.existsByDocument(normalized)) {
            throw new DomainException("Document already registered: " + normalized);
        }
        CustomerEntity entity = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    public CustomerResponse update(String id, CustomerRequest request) {
        CustomerEntity entity = findEntityById(id);
        String normalized = DocumentUtils.normalize(request.document());

        boolean documentChanged = !entity.getDocument().equals(normalized);
        if (documentChanged && customerRepository.existsByDocument(normalized)) {
            throw new DomainException("Document already registered: " + normalized);
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