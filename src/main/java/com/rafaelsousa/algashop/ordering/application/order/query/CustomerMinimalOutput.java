package com.rafaelsousa.algashop.ordering.application.order.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMinimalOutput {
    private UUID id;
    private String firstName;
    private String lastName;
    private String document;
    private String email;
    private String phone;
}