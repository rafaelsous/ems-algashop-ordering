package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.shipping;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ShippingEmbeddable {

    private BigDecimal cost;
    private LocalDate expectedDate;

    @Embedded
    private RecipientEmbeddable recipient;

    @Embedded
    private AddressEmbeddable address;
}