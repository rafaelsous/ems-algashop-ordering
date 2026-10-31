package com.rafaelsousa.algashop.ordering.core.application.invoice.reply;

import com.rafaelsousa.algashop.ordering.core.application.InboundIntegrationReply;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePaidIntegrationReply implements InboundIntegrationReply {

    @NotNull
    private UUID invoiceId;

    @NotBlank
    private String orderId;

    @NotNull
    private OffsetDateTime paidAt;
}