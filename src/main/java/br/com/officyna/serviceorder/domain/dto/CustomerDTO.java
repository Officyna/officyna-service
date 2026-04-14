package br.com.officyna.serviceorder.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CustomerDTO {

    private String id;

    private String fullName;

    private String phoneNumber;

    private String fullAdress;

    private String complement;

    public CustomerDTO(String id,
                       String fullName,
                       String phoneNumber,
                       String street,
                       String number,
                       String neighborhood,
                       String city,
                       String state,
                       String zipCode,
                       String complement)
    {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.fullAdress = street + ", " + number + " - " + neighborhood + ", " + city + " - " + state + ", " + zipCode;
        this.complement = complement;
    }
}
