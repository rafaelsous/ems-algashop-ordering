package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QuantityTest {

    @Test
    void given_validQuantity_whenInstantiate_shouldReturnNewQuantity() {
        Quantity quantity = Quantity.of(1);

        Assertions.assertThat(quantity.value()).isEqualTo(1);
    }

    @Test
    void given_nullQuantity_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Quantity.of(null));
    }

    @Test
    void given_negativeQuantity_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Quantity.of(-1));
    }

    @Test
    void given_otherValidQuantity_whenAddQuantity_shouldReturnSum() {
        Quantity sum = Quantity.of(1).add(Quantity.of(1));
        Quantity expectedQuantity = Quantity.of(2);

        Assertions.assertThat(expectedQuantity).isEqualByComparingTo(sum);
    }

    @Test
    void given_otherNullQuantity_whenAddQuantity_shouldGenerateException() {
        Quantity quantity = Quantity.of(1);

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> quantity.add(null));
    }

    @Test
    void given_zeroValue_whenToInstantiate_shouldGenerate() {
        Quantity quantity = Quantity.ZERO;

        Assertions.assertThat(quantity).isEqualTo(Quantity.of(0));
    }

    @Test
    void given_validQuantity_whenCallToString_shouldReturnStringValue() {
        Assertions.assertThat(Quantity.of(1).toString()).hasToString("1");
    }
}