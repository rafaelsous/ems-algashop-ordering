package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductNameTest {

    @Test
    void given_validProductName_whenInstantiate_shouldGenerate() {
        ProductName productName = ProductName.of("Product 01");

        Assertions.assertThat(productName.value()).hasToString("Product 01");
    }

    @Test
    void given_nullValue_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> ProductName.of(null));
    }

    @Test
    void given_blankValue_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ProductName.of(" "));
    }

    @Test
    void given_validProductName_whenCallToString_shouldReturnStringValue() {
        ProductName productName = ProductName.of("Product 01");

        Assertions.assertThat(productName.toString()).hasToString("Product 01");
    }
}