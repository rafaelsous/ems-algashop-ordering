package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.PaymentMethod;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;

@Component
public class OrderPersistenceDisassembler {

    public Order toDomain(OrderPersistence orderPersistence) {
        return Order.existing()
                .id(new OrderId(orderPersistence.getId()))
                .customerId(new CustomerId(orderPersistence.getCustomerId()))
                .totalAmount(Money.of(orderPersistence.getTotalAmount()))
                .totalItems(Quantity.of(orderPersistence.getTotalItems()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistence.getPaymentMethod()))
                .placedAt(orderPersistence.getPlacedAt())
                .paidAt(orderPersistence.getPlacedAt())
                .readyAt(orderPersistence.getReadyAt())
                .canceledAt(orderPersistence.getCanceledAt())
                .status(OrderStatus.valueOf(orderPersistence.getStatus()))
                .items(new HashSet<>())
                .version(orderPersistence.getVersion())
                .billing(this.buildBilling(orderPersistence.getBilling()))
                .shipping(this.buildShipping(orderPersistence.getShipping()))
                .build();
    }

    private Billing buildBilling(BillingEmbeddable billing) {
        if (Objects.isNull(billing)) return null;

        return Billing.builder()
                .fullName(FullName.of(billing.getFirstName(), billing.getLastName()))
                .document(Document.of(billing.getDocument()))
                .phone(Phone.of(billing.getPhone()))
                .email(Email.of(billing.getEmail()))
                .address(this.buildAddress(billing.getAddress()))
                .build();
    }

    private Shipping buildShipping(ShippingEmbeddable shipping) {
        if (Objects.isNull(shipping)) return null;

        return Shipping.builder()
                .cost(Money.of(shipping.getCost()))
                .expectedDate(shipping.getExpectedDate())
                .recipient(this.buildRecipient(shipping.getRecipient()))
                .address(this.buildAddress(shipping.getAddress()))
                .build();
    }

    private Address buildAddress(AddressEmbeddable address) {
        Objects.requireNonNull(address);

        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(ZipCode.of(address.getZipCode()))
                .build();
    }

    private Recipient buildRecipient(RecipientEmbeddable recipient) {
        Objects.requireNonNull(recipient);

        return Recipient.builder()
                .fullName(FullName.of(recipient.getFirstName(), recipient.getLastName()))
                .document(Document.of(recipient.getDocument()))
                .phone(Phone.of(recipient.getPhone()))
                .build();
    }
}