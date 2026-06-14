package br.com.officyna.administrative.customer.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {

    private String id;

    private String name;

    private String document;

    private CustomerType type;

    private String email;

    private String phone;

    private String areaCode;

    private String countryCode;

    private AddressEntity address;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}