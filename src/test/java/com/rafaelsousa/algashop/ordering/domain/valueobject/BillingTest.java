package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BillingTest {

    @Test
    void given_allValidBillingInfoProperties_whenInstantiate_shouldGenerate() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");
        Email email = Email.of("rafael.sousa@email.com");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Billing billing = Billing.of(fullName, document, phone, email, address);

        Assertions.assertWith(billing, b -> {
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
        Email email = Email.of("rafael.sousa@email.com");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Billing.of(null, document, phone, email, address));
    }

    @Test
    void given_nullDocument_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Phone phone = Phone.of("11912345678");
        Email email = Email.of("rafael.sousa@email.com");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Billing.of(fullName, null, phone, email, address));
    }

    @Test
    void given_nullPhone_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Email email = Email.of("rafael.sousa@email.com");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Billing.of(fullName, document, null, email, address));
    }

    @Test
    void given_nullEmail_whenTryInstantiante_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");
        Address address = Address.of(
                "Bourbon Street", null,
                "North Ville", "1324", "New York", "South California",
                new ZipCode("12345"));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Billing.of(fullName, document, phone, null, address));
    }

    @Test
    void given_nullAddress_whenTryInstantiate_shouldGenerateException() {
        FullName fullName = FullName.of("Rafael", "Sousa");
        Document document = Document.of("12345678901");
        Phone phone = Phone.of("11912345678");
        Email email = Email.of("rafael.sousa@email.com");

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Billing.of(fullName, document, phone, email, null));
    }
}