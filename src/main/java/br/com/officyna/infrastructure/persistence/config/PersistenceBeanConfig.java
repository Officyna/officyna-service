package br.com.officyna.infrastructure.persistence.config;

import br.com.officyna.administrative.customer.domain.controller.CustomerControllerAdapter;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.presenter.CustomerPresenter;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.labor.domain.controller.LaborControllerAdapter;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.presenter.LaborPresenter;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.administrative.supply.domain.controller.SupplyControllerAdapter;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.presenter.SupplyPresenter;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.administrative.user.domain.controller.UserControllerAdapter;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.presenter.UserPresenter;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.administrative.vehicle.domain.controller.VehicleControllerAdapter;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.presenter.VehiclePresenter;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.monitoring.domain.controller.MonitoringControllerAdapter;
import br.com.officyna.monitoring.domain.presenter.LaborMonitoringPresenter;
import br.com.officyna.monitoring.domain.repository.LaborMonitoringRepository;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import br.com.officyna.serviceorder.domain.controller.CustomerServiceOrderControllerAdapter;
import br.com.officyna.serviceorder.domain.controller.ServiceOrderControllerAdapter;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import br.com.officyna.serviceorder.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PersistenceBeanConfig {

    @Bean
    public CustomerService customerService(CustomerRepository customerRepository) {
        return new CustomerService(customerRepository);
    }

    @Bean
    public CustomerControllerAdapter customerControllerAdapter(CustomerService customerService,
                                                              CustomerMapper customerMapper,
                                                              CustomerPresenter customerPresenter) {
        return new CustomerControllerAdapter(customerService, customerMapper, customerPresenter);
    }

    @Bean
    public LaborMonitoringService laborMonitoringService(LaborMonitoringRepository monitoringRepository,
                                                         LaborRepository laborRepository,
                                                         ServiceOrderRepository serviceOrderRepository) {
        return new LaborMonitoringService(monitoringRepository, laborRepository, serviceOrderRepository);
    }

    @Bean
    public MonitoringControllerAdapter monitoringControllerAdapter(LaborMonitoringService laborMonitoringService,
                                                                   LaborMonitoringPresenter laborMonitoringPresenter) {
        return new MonitoringControllerAdapter(laborMonitoringService, laborMonitoringPresenter);
    }

    @Bean
    public LaborService laborService(LaborRepository laborRepository, LaborMonitoringService laborMonitoringService) {
        return new LaborService(laborRepository, laborMonitoringService);
    }

    @Bean
    public LaborControllerAdapter laborControllerAdapter(LaborService laborService,
                                                         LaborMapper laborMapper,
                                                         LaborPresenter laborPresenter) {
        return new LaborControllerAdapter(laborService, laborMapper, laborPresenter);
    }

    @Bean
    public SupplyService supplyService(SupplyRepository supplyRepository) {
        return new SupplyService(supplyRepository);
    }

    @Bean
    public SupplyControllerAdapter supplyControllerAdapter(SupplyService supplyService,
                                                           SupplyMapper supplyMapper,
                                                           SupplyPresenter supplyPresenter) {
        return new SupplyControllerAdapter(supplyService, supplyMapper, supplyPresenter);
    }

    @Bean
    public StockService stockService(SupplyRepository supplyRepository) {
        return new StockService(supplyRepository);
    }

    @Bean
    public VehicleService vehicleService(VehicleRepository vehicleRepository, CustomerService customerService) {
        return new VehicleService(vehicleRepository, customerService);
    }

    @Bean
    public VehicleControllerAdapter vehicleControllerAdapter(VehicleService vehicleService,
                                                             VehicleMapper vehicleMapper,
                                                             VehiclePresenter vehiclePresenter) {
        return new VehicleControllerAdapter(vehicleService, vehicleMapper, vehiclePresenter);
    }

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    public UserControllerAdapter userControllerAdapter(UserService userService,
                                                       UserMapper userMapper,
                                                       UserPresenter userPresenter) {
        return new UserControllerAdapter(userService, userMapper, userPresenter);
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
    public ServiceOrderService serviceOrderService(ServiceOrderRepository serviceOrderRepository,
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
    public ServiceOrderControllerAdapter serviceOrderControllerAdapter(ServiceOrderService serviceOrderService,
                                                                       ServiceOrderPresenter serviceOrderPresenter) {
        return new ServiceOrderControllerAdapter(serviceOrderService, serviceOrderPresenter);
    }

    @Bean
    public CustomerServiceOrderService customerServiceOrderService(ServiceOrderRepository serviceOrderRepository,
                                                                   CustomerAndMecnichalService customerAndMecnichalService,
                                                                   ServiceOrderService serviceOrderService) {
        return new CustomerServiceOrderService(serviceOrderRepository, customerAndMecnichalService, serviceOrderService);
    }

    @Bean
    public CustomerServiceOrderControllerAdapter customerServiceOrderControllerAdapter(CustomerServiceOrderService customerServiceOrderService,
                                                                                       ServiceOrderPresenter serviceOrderPresenter) {
        return new CustomerServiceOrderControllerAdapter(customerServiceOrderService, serviceOrderPresenter);
    }
}

