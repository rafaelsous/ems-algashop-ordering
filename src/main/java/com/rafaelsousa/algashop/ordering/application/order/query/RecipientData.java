package com.rafaelsousa.algashop.ordering.application.order.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientData {
    private String firstName;
    private String lastName;
    private String document;
    private String phone;
}