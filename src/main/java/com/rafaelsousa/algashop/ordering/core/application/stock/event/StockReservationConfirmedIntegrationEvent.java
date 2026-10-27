package com.rafaelsousa.algashop.ordering.core.application.stock.event;

import com.rafaelsousa.algashop.ordering.core.application.IntegrationEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationConfirmedIntegrationEvent implements IntegrationEvent {

    @NotNull
    private UUID reservationId;

    @NotBlank
    private String orderId;

    @NotNull
    private OffsetDateTime confirmedAt;

    @Override
    public String getAggregateId() {
        return orderId;
    }
}
