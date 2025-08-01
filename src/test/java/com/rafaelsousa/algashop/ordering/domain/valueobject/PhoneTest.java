package com.rafaelsousa.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhoneTest {

    @Test
    void given_validPhone_whenInstantiate_shouldGenerate() {
        String phone = "000-00-0000";
        Phone validPhone = Phone.of(phone);

        Assertions.assertThat(validPhone.value()).isEqualTo(phone);
        Assertions.assertThat(phone).isEqualTo(validPhone.toString());
    }

    @Test
    void given_blankValue_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Phone.of(" "));
    }
}