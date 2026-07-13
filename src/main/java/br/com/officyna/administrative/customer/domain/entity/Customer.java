package br.com.officyna.administrative.customer.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    private String id;

    private String name;

    private String document;

    private CustomerType type;

    private String email;

    private String phone;

    private String areaCode;

    private String countryCode;

    private Address address;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}