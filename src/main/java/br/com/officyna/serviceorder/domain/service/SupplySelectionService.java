package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.serviceorder.domain.exception.ServiceOrderBusinessException;
import br.com.officyna.serviceorder.api.resources.SupplysRequest;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SupplySelectionService {private final SupplyService service;

    public SupplySelectionService(SupplyService service) {
        this.service = service;
    }

    SupplyDTO addSupplys(
            List<SupplysRequest> supplysIdList,
            List<SupplyDetailDTO> supplysDetails) {

        log.info(
                "Adding supplies to service order. Requested supplies: {}",
                supplysIdList != null ? supplysIdList.size() : 0
        );

        List<SupplyDetailDTO> allSupplys = new ArrayList<>(
                supplysDetails != null ? supplysDetails : List.of()
        );

        log.debug(
                "Existing supplies in service order: {}",
                allSupplys.size()
        );

        if (supplysIdList != null && !supplysIdList.isEmpty()) {

            List<SupplyDetailDTO> newSupplys = supplysIdList.stream()
                    .map(request -> {

                        log.debug(
                                "Finding supply by id: {}",
                                request.getId()
                        );

                        Supply supply = service.findById(request.getId());

                        log.debug(
                                "Supply found successfully. Id: {}, Name: {}",
                                supply.getId(),
                                supply.getName()
                        );

                        BigDecimal totalPrice =
                                this.calculateTotalPriceForUnitSupply(
                                        request.getQuantity(),
                                        supply.getSalePrice()
                                );

                        log.debug(
                                "Supply total price calculated. Supply id: {}, Quantity: {}, Unit price: {}, Total price: {}",
                                supply.getId(),
                                request.getQuantity(),
                                supply.getSalePrice(),
                                totalPrice
                        );

                        return new SupplyDetailDTO(
                                supply.getId(),
                                supply.getName(),
                                supply.getDescription(),
                                request.getQuantity(),
                                supply.getSalePrice(),
                                totalPrice
                        );
                    })
                    .toList();

            allSupplys.addAll(newSupplys);

            log.debug(
                    "New supplies added successfully. New supplies: {}, Total supplies: {}",
                    newSupplys.size(),
                    allSupplys.size()
            );
        }

        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(allSupplys);

        log.info(
                "Supplies added successfully. Total supplies in service order: {}",
                allSupplys.size()
        );

        return supplys;
    }

    public void removeSupply(SupplyDTO supplys, String supplyId) {

        log.info(
                "Removing supply from service order. Supply id: {}",
                supplyId
        );

        if (supplys == null
                || supplys.getSupplysDetails() == null
                || supplys.getSupplysDetails().isEmpty()
                || supplyId == null) {

            log.warn(
                    "Unable to remove supply. Service order has no supplies or supply id is invalid. Supply id: {}",
                    supplyId
            );

            throw new ServiceOrderBusinessException(
                    "A Ordem de Serviço não possui suprimentos cadastrados."
            );
        }

        boolean removed = supplys.getSupplysDetails()
                .removeIf(supply -> supply.getId().equals(supplyId));

        if (!removed) {

            log.warn(
                    "Supply not found in service order. Supply id: {}",
                    supplyId
            );

            return;
        }

        log.info(
                "Supply removed successfully. Supply id: {}",
                supplyId
        );
    }

    private BigDecimal calculateTotalPriceForUnitSupply(
            Integer quantity,
            BigDecimal unitPrice) {

        log.debug(
                "Calculating total supply price. Quantity: {}, Unit price: {}",
                quantity,
                unitPrice
        );

        BigDecimal totalPrice = unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );

        log.debug(
                "Total supply price calculated successfully: {}",
                totalPrice
        );

        return totalPrice;
    }
    }