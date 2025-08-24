package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;

public class CheckoutInputTestDataBuilder {

    public static CheckoutInput.CheckoutInputBuilder aCheckoutInput() {
        return CheckoutInput.builder()
                .shoppingCartId(ShoppingCartTestDataBuilder.DEFAULT_SHOPPING_CART_ID.value())
                .paymentMethod("CREDIT_CARD")
                .shipping(buildShipping())
                .billing(buildBilling());
    }

    private static BillingData buildBilling() {
        return BillingData.builder()
                .firstName("Matt")
                .lastName("Damon")
                .phone("123-321-1112")
                .document("123-45-6789")
                .email("matt.damon@email.com")
                .address(buildAddress())
                .build();
    }

    private static ShippingInput buildShipping() {
        return ShippingInput.builder()
                .recipient(RecipientData.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .document("255-08-0578")
                        .phone("478-256-2604")
                        .build())
                .address(buildAddressAlt())
                .build();
    }

    private static AddressData buildAddress() {
        return AddressData.builder()
                .street("Amphitheatre Parkway")
                .number("1600")
                .complement("")
                .neighborhood("Mountain View")
                .city("Mountain View")
                .state("California")
                .zipCode("94043")
                .build();
    }

    private static AddressData buildAddressAlt() {
        return AddressData.builder()
                .street("Elm Street")
                .number("456")
                .complement("House A")
                .neighborhood("Central Park")
                .city("Springfield")
                .state("Illinois")
                .zipCode("62704")
                .build();
    }
}