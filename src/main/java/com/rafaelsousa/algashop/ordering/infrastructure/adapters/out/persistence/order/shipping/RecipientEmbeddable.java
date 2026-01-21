package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.shipping;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RecipientEmbeddable {

    private String firstName;
    private String lastName;
    private String document;
    private String phone;
}