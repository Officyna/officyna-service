package br.com.officyna.serviceorder.api.resources;

import br.com.officyna.serviceorder.domain.dto.*;


public record ServiceOrderResponse (

    String serviceOrderId,

    String serviceOrderNumber,

    CustomerDTO customer,

    MechanicDTO mechanic,

    VehicleDTO vehicle,

    LaborsDTO labors,

    SupplyDTO supplys,

    String informationText,

    String serviceOrderStatus,

    String statusDate,

    String totalBudgetAmount,

    String createdAt
){}
