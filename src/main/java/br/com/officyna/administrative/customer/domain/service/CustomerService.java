package br.com.officyna.administrative.customer.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.validation.DocumentUtils;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.administrative.customer.domain.exception.CustomerBusinessException;
import br.com.officyna.administrative.customer.domain.exception.CustomerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CustomerService {

    private final CustomerRepository repository;

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        logger.info("Finding all active customers");

        List<Customer> customers = repository.findByActiveTrue();

        logger.info("Found {} active customers", customers.size());

        return customers;
    }

    public Customer findById(String id) {
        logger.info("Finding customer by id: {}", id);

        Customer customer = findEntityById(id);

        logger.info("Customer found by id: {}", id);

        return customer;
    }

    public Customer findByDocument(String document) {
        logger.info("Finding customer by document");

        String normalized = DocumentUtils.normalize(document);

        return repository.findByDocument(normalized)
                .map(customer -> {
                    logger.info("Customer found by document");
                    return customer;
                })
                .orElseThrow(() -> {
                    logger.warn("Customer not found with provided document");

                    return new CustomerNotFoundException(
                            "Customer not found with document: " + normalized
                    );
                });
    }

    public Customer create(Customer customer) {
        logger.info("Creating customer");

        String normalized =
                DocumentUtils.normalize(customer.getDocument());

        if (repository.existsByDocument(normalized)) {
            logger.warn("Customer creation failed: document already registered");

            throw new CustomerBusinessException(
                    "Document already registered: " + normalized
            );
        }

        customer.setDocument(normalized);
        customer.setActive(true);

        Customer savedCustomer = repository.save(customer);

        logger.info("Customer created successfully with id: {}",
                savedCustomer.getId());

        return savedCustomer;
    }

    public Customer update(String id, Customer changes) {
        logger.info("Updating customer with id: {}", id);

        Customer entity = findEntityById(id);

        String normalized =
                DocumentUtils.normalize(changes.getDocument());

        boolean documentChanged =
                !entity.getDocument().equals(normalized);

        if (documentChanged && repository.existsByDocument(normalized)) {
            logger.warn(
                    "Customer update failed: document already registered"
            );

            throw new CustomerBusinessException(
                    "Document already registered: " + normalized
            );
        }

        entity.setName(changes.getName());
        entity.setDocument(normalized);
        entity.setType(changes.getType());
        entity.setEmail(changes.getEmail());
        entity.setPhone(changes.getPhone());
        entity.setAreaCode(changes.getAreaCode());
        entity.setCountryCode(changes.getCountryCode());
        entity.setAddress(changes.getAddress());

        Customer updatedCustomer = repository.save(entity);

        logger.info("Customer updated successfully with id: {}", id);

        return updatedCustomer;
    }

    public void delete(String id) {
        logger.info("Deactivating customer with id: {}", id);

        Customer entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        logger.info("Customer deactivated successfully with id: {}", id);
    }

    // Utility method for internal use (e.g. VehicleService)
    public Customer findEntityById(String id) {
        logger.info("Finding customer entity by id: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with id: {}", id);
                    return CustomerNotFoundException.of(id);
                });
    }
}