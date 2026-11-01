package com.rafaelsousa.algashop.ordering.core.application.invoice.command;

import com.rafaelsousa.algashop.ordering.core.application.OutboundIntegrationCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelInvoiceIntegrationCommand implements OutboundIntegrationCommand {

    @NotBlank
    private String orderId;

    @Override
    public String getAggregateId() {
        return orderId;
    }
}
