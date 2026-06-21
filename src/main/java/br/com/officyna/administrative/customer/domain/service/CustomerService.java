package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.validation.DocumentUtils;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import java.util.List;

public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository repository, CustomerMapper customerMapper) {
        this.repository = repository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse findById(String id) {
        return customerMapper.toResponse(findEntityById(id));
    }

    public CustomerResponse findByDocument(String document) {
        String normalized = DocumentUtils.normalize(document);
        return repository.findByDocument(normalized)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found with document: " + normalized));
    }

    public CustomerResponse create(CustomerRequest request) {
        String normalized = DocumentUtils.normalize(request.document());
        if (repository.existsByDocument(normalized)) {
            throw new DomainException("Document already registered: " + normalized);
        }
        Customer entity = customerMapper.toEntity(request);
        return customerMapper.toResponse(repository.save(entity));
    }

    public CustomerResponse update(String id, CustomerRequest request) {
        Customer entity = findEntityById(id);
        String normalized = DocumentUtils.normalize(request.document());

        boolean documentChanged = !entity.getDocument().equals(normalized);
        if (documentChanged && repository.existsByDocument(normalized)) {
            throw new DomainException("Document already registered: " + normalized);
        }

        customerMapper.updateEntity(entity, request);
        return customerMapper.toResponse(repository.save(entity));
    }

    public void delete(String id) {
        Customer entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    // Utility method for internal use (e.g. VehicleService)
    public Customer findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Customer", id));
    }
}