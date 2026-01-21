package com.rafaelsousa.algashop.ordering.core.application.order.query;

import com.rafaelsousa.algashop.ordering.core.ports.commons.AddressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingData {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String document;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull @Valid
    private AddressData address;
}