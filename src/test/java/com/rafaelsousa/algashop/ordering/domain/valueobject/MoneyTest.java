package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTest {

    @Test
    void given_validBigDecimalValue_whenInstantiate_shouldGenerate() {
        Money money = Money.of(new BigDecimal("10.9899"));
        BigDecimal roundedValue = new BigDecimal("10.99");

        Assertions.assertThat(money.value().scale()).isEqualTo(2);
        Assertions.assertThat(money.value()).isEqualTo(roundedValue);
        Assertions.assertThat(Money.of(roundedValue)).isEqualByComparingTo(money);
    }

    @Test
    void given_validStringValue_whenToInstantiate_shouldGenerate() {
        Money money = Money.of("10.9899");
        BigDecimal roundedValue = new BigDecimal("10.99");

        Assertions.assertThat(Money.of(roundedValue)).isEqualByComparingTo(money);
    }

    @Test
    void given_zeroValue_whenToInstantiate_shouldGenerate() {
        Money money = Money.ZERO;

        Assertions.assertThat(money).isEqualTo(Money.of("0"));
    }

    @Test
    void given_NullValue_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Money(null));
    }

    @Test
    void given_NegativeValue_whenTryInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Money.of("-10"));
    }

    @Test
    void given_validQuantity_whenMultiplyMoney_shouldReturnNewMoney() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Quantity quantity = Quantity.of(2);
        Money expectedMoney = Money.of(BigDecimal.valueOf(20.0));

        Assertions.assertThat(money.multiply(quantity)).isEqualByComparingTo(expectedMoney);
    }

    @Test
    void given_nullQuantity_whenTryMultiplyMoney_shouldGenerateException() {
        Money money = Money.of(BigDecimal.valueOf(10.0));

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> money.multiply(null));
    }

    @Test
    void given_quantityLessThanOne_whenTryMultiplyMoney_shouldGenerateException() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Quantity quantity = Quantity.of(0);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> money.multiply(quantity));
    }

    @Test
    void given_quantityEqualToOne_whenTryMultiplyMoney_shouldReturnSameMoney() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Quantity quantity = Quantity.of(1);
        Money multiplied = money.multiply(quantity);

        Assertions.assertThat(multiplied).isSameAs(money);
    }

    @Test
    void given_otherValidMoney_whenAddMoney_shouldReturnSum() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Money other = Money.of(BigDecimal.valueOf(15.0));
        Money actualResult = money.add(other);
        Money expectedResult = Money.of(BigDecimal.valueOf(25.0));

        Assertions.assertThat(actualResult).isEqualByComparingTo(expectedResult);
    }

    @Test
    void given_otherNullMoney_whenTryAddMoney_shouldGenerateException() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Money other = null;

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> money.add(other));
    }

    @Test
    void given_otherValidMoney_whenDivideMoney_shouldReturnResult() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Money other = Money.of(BigDecimal.valueOf(2.0));
        Money actualResult = money.divide(other);
        Money expectedResult = Money.of(BigDecimal.valueOf(5.0));

        Assertions.assertThat(actualResult).isEqualByComparingTo(expectedResult);
    }

    @Test
    void given_otherNullMoney_whenTryDivideMoney_shouldGenerateException() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        Money other = null;

        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> money.divide(other));
    }

    @Test
    void given_validMoney_whenCallToString_shouldReturnStringValue() {
        Money money = Money.of(BigDecimal.valueOf(10.0));
        String expectedToString = "10.00";

        Assertions.assertThat(money.toString()).hasToString(expectedToString);
    }
}