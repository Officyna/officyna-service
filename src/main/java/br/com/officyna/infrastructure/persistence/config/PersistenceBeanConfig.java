package br.com.officyna.infrastructure.persistence.config;

import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.repository.ICustomerRepository;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.repository.ILaborRepository;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.repository.ISupplyRepository;
import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.repository.IUserRepository;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.repository.IVehicleRepository;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.monitoring.domain.repository.ILaborMonitoringRepository;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.domain.repository.IServiceOrderRepository;
import br.com.officyna.serviceorder.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for manual instantiation of domain services.
 * This enables decoupling domain services from Spring annotations and allows
 * wiring infrastructure gateways (implementing domain repository interfaces)
 * into the domain layer following Dependency Inversion.
 */
@Configuration
public class PersistenceBeanConfig {

    @Bean
    public CustomerService customerService(ICustomerRepository customerRepository, CustomerMapper customerMapper) {
        return new CustomerService(customerRepository, customerMapper);
    }

    @Bean
    public LaborMonitoringService laborMonitoringService(ILaborMonitoringRepository monitoringRepository,
                                                          ILaborRepository laborRepository,
                                                          IServiceOrderRepository serviceOrderRepository) {
        return new LaborMonitoringService(monitoringRepository, laborRepository, serviceOrderRepository);
    }

    @Bean
    public LaborService laborService(ILaborRepository laborRepository, LaborMapper laborMapper, LaborMonitoringService laborMonitoringService) {
        return new LaborService(laborRepository, laborMapper, laborMonitoringService);
    }

    @Bean
    public SupplyService supplyService(ISupplyRepository supplyRepository, SupplyMapper supplyMapper) {
        return new SupplyService(supplyRepository, supplyMapper);
    }

    @Bean
    public StockService stockService(ISupplyRepository supplyRepository) {
        return new StockService(supplyRepository);
    }

    @Bean
    public VehicleService vehicleService(IVehicleRepository vehicleRepository, VehicleMapper vehicleMapper, CustomerService customerService) {
        return new VehicleService(vehicleRepository, vehicleMapper, customerService);
    }

    @Bean
    public UserService userService(IUserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, userMapper, passwordEncoder);
    }

    // ServiceOrder related beans
    @Bean
    public LaborSelectionService laborSelectionService(LaborService laborService) {
        return new LaborSelectionService(laborService);
    }

    @Bean
    public SupplySelectionService supplySelectionService(SupplyService supplyService) {
        return new SupplySelectionService(supplyService);
    }

    @Bean
    public VehicleSelectionService vehicleSelectionService(VehicleService vehicleService) {
        return new VehicleSelectionService(vehicleService);
    }

    @Bean
    public CustomerAndMecnichalService customerAndMecnichalService(UserService userService, CustomerService customerService) {
        return new CustomerAndMecnichalService(userService, customerService);
    }

    @Bean
    public ServiceOrderService serviceOrderService(IServiceOrderRepository serviceOrderRepository,
                                                   LaborSelectionService laborSelectionService,
                                                   SupplySelectionService supplySelectionService,
                                                   CustomerAndMecnichalService customerAndMecnichalService,
                                                   VehicleSelectionService vehicleSelectionService,
                                                   ServiceOrderMapper mapper,
                                                   LaborMonitoringService laborMonitoringService,
                                                   StockService stockService) {
        return new ServiceOrderService(serviceOrderRepository,
                laborSelectionService,
                supplySelectionService,
                customerAndMecnichalService,
                vehicleSelectionService,
                mapper,
                laborMonitoringService,
                stockService);
    }

    @Bean
    public CustomerServiceOrderService customerServiceOrderService(IServiceOrderRepository serviceOrderRepository,
                                                                     CustomerAndMecnichalService customerAndMecnichalService,
                                                                     ServiceOrderMapper mapper,
                                                                     ServiceOrderService serviceOrderService) {
        return new CustomerServiceOrderService(serviceOrderRepository, customerAndMecnichalService, mapper, serviceOrderService);
    }
}

