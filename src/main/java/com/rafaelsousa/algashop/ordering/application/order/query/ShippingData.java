package com.rafaelsousa.algashop.ordering.application.order.query;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
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
public class ShippingData {
    private BigDecimal cost;
    private LocalDate expectedDeliveryDate;
    private RecipientData recipient;
    private AddressData address;
}