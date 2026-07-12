package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.validation.DocumentUtils;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.administrative.customer.domain.exception.CustomerBusinessException;
import br.com.officyna.administrative.customer.domain.exception.CustomerNotFoundException;
import java.util.List;

public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        return repository.findByActiveTrue();
    }

    public Customer findById(String id) {
        return findEntityById(id);
    }

    public Customer findByDocument(String document) {
        String normalized = DocumentUtils.normalize(document);
        return repository.findByDocument(normalized)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with document: " + normalized));
    }

    public Customer create(Customer customer) {
        String normalized = DocumentUtils.normalize(customer.getDocument());
        if (repository.existsByDocument(normalized)) {
            throw new CustomerBusinessException("Document already registered: " + normalized);
        }
        customer.setDocument(normalized);
        customer.setActive(true);
        return repository.save(customer);
    }

    public Customer update(String id, Customer changes) {
        Customer entity = findEntityById(id);
        String normalized = DocumentUtils.normalize(changes.getDocument());

        boolean documentChanged = !entity.getDocument().equals(normalized);
        if (documentChanged && repository.existsByDocument(normalized)) {
            throw new CustomerBusinessException("Document already registered: " + normalized);
        }

        entity.setName(changes.getName());
        entity.setDocument(normalized);
        entity.setType(changes.getType());
        entity.setEmail(changes.getEmail());
        entity.setPhone(changes.getPhone());
        entity.setAreaCode(changes.getAreaCode());
        entity.setCountryCode(changes.getCountryCode());
        entity.setAddress(changes.getAddress());
        return repository.save(entity);
    }

    public void delete(String id) {
        Customer entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    // Utility method for internal use (e.g. VehicleService)
    public Customer findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> CustomerNotFoundException.of(id));
    }
}