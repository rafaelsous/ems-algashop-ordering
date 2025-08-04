package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShippingInfoTest {

    @Test
    void given_allValidShippingInfoProperties_whenInstantiate_shouldGenerate() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        ShippingInfo billingInfo = ShippingInfo.of(fullName, document, phone, address);

        Assertions.assertWith(billingInfo, b -> {
            Assertions.assertThat(b.fullName()).isEqualTo(fullName);
            Assertions.assertThat(b.document()).isEqualTo(document);
            Assertions.assertThat(b.phone()).isEqualTo(phone);
            Assertions.assertThat(b.address()).isEqualTo(address);
        });
    }

    @Test
    void given_nullFullName_whenTryInstantiate_shouldGenerateException() {
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> ShippingInfo.of(null, document, phone, address));
    }

    @Test
    void given_nullDocument_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Phone phone = Phone.of("11912345678");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> ShippingInfo.of(fullName, null, phone, address));
    }

    @Test
    void given_nullPhone_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> ShippingInfo.of(fullName, document, null, address));
    }

    @Test
    void given_nullAddress_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> ShippingInfo.of(fullName, document, phone, null));
    }
}