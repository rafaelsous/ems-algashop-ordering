package com.rafaelsousa.algashop.ordering.domain.valueobject;

import com.rafaelsousa.algashop.ordering.domain.entity.OrderTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ShippingTest {

    @Test
    void given_allValidShippingInfoProperties_whenInstantiate_shouldGenerate() {
        Recipient recipient = Recipient.builder()
                .fullName(FullName.of("Rafael", "Sousa"))
                .document(Document.of("12345678901"))
                .phone(Phone.of("11912345678"))
                .build();
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Shipping shipping = Shipping.of(
                recipient,
                Money.of("100.00"),
                LocalDate.now().plusDays(5),
                address
        );

        Assertions.assertThat(shipping).isNotNull();
    }

    @Test
    void given_nullRecipient_whenTryInstantiate_shouldGenerateException() {
        Address address = OrderTestDataBuilder.anAddress();
        Money shippingCost = Money.of("15.00");
        LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(1);

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Shipping.of(null, shippingCost, expectedDeliveryDate, address));
    }

    @Test
    void given_nullShippingCost_whenTryInstantiate_shouldGenerateException() {
        Address address = OrderTestDataBuilder.anAddress();
        LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(1);
        Recipient recipient = Recipient.builder()
                .fullName(FullName.of("Rafael", "Sousa"))
                .document(Document.of("12345678901"))
                .phone(Phone.of("11912345678"))
                .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Shipping.of(recipient, null, expectedDeliveryDate, address));
    }

    @Test
    void given_nullExpectedDate_whenTryInstantiate_shouldGenerateException() {
        Address address = OrderTestDataBuilder.anAddress();
        Money shippingCost = Money.of("15.00");
        Recipient recipient = Recipient.builder()
                .fullName(FullName.of("Rafael", "Sousa"))
                .document(Document.of("12345678901"))
                .phone(Phone.of("11912345678"))
                .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Shipping.of(recipient, shippingCost, null, address));
    }

    @Test
    void given_nullAddress_whenTryInstantiate_shouldGenerateException() {
        Money shippingCost = Money.of("15.00");
        LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(1);
        Recipient recipient = Recipient.builder()
                .fullName(FullName.of("Rafael", "Sousa"))
                .document(Document.of("12345678901"))
                .phone(Phone.of("11912345678"))
                .build();

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Shipping.of(recipient, shippingCost, expectedDeliveryDate, null));
    }
}