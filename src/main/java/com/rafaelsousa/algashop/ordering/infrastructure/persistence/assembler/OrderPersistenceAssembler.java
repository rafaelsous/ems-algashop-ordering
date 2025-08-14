package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Address;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Billing;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Recipient;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Shipping;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderPersistenceAssembler {

    public OrderPersistence fromDomain(Order order) {
        return merge(new OrderPersistence(), order);
    }

    public OrderPersistence merge(OrderPersistence orderPersistence, Order order) {
        orderPersistence.setId(order.id().value().toLong());
        orderPersistence.setCustomerId(order.customerId().value());
        orderPersistence.setTotalAmount(order.totalAmount().value());
        orderPersistence.setTotalItems(order.totalItems().value());
        orderPersistence.setStatus(order.status().name());
        orderPersistence.setPaymentMethod(order.paymentMethod().name());
        orderPersistence.setPlacedAt(order.placedAt());
        orderPersistence.setPaidAt(order.paidAt());
        orderPersistence.setCanceledAt(order.canceledAt());
        orderPersistence.setReadyAt(order.readyAt());
        orderPersistence.setVersion(order.version());
        orderPersistence.setBilling(this.buildBilling(order.billing()));
        orderPersistence.setShipping(this.buildShipping(order.shipping()));

        return orderPersistence;
    }

    private BillingEmbeddable buildBilling(Billing billing) {
        Objects.requireNonNull(billing);

        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lasstName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(buildAddress(billing.address()))
                .build();
    }

    private ShippingEmbeddable buildShipping(Shipping shipping) {
        Objects.requireNonNull(shipping);

        return ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .expectedDate(shipping.expectedDate())
                .recipient(buildRecipient(shipping.recipient()))
                .address(buildAddress(shipping.address()))
                .build();
    }

    private AddressEmbeddable buildAddress(Address address) {
        Objects.requireNonNull(address);

        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }

    private RecipientEmbeddable buildRecipient(Recipient recipient) {
        Objects.requireNonNull(recipient);

        return RecipientEmbeddable.builder()
                .firstName(recipient.fullName().firstName())
                .lasstName(recipient.fullName().lastName())
                .document(recipient.document().value())
                .phone(recipient.phone().value())
                .build();
    }
}