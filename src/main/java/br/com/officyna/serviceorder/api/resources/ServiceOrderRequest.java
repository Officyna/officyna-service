package br.com.officyna.serviceorder.api.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrderRequest {

    private String customerId;

    private String vehicleId;

    private List<String> laborsId;

    private List<String> supplysId;

    private String informationText;
}
