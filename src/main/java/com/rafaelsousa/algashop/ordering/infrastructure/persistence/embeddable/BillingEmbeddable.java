package com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BillingEmbeddable {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;
    private String email;

    @Embedded
    private AddressEmbeddable address;
}