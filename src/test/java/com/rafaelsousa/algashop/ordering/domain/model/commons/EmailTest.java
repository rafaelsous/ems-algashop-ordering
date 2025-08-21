package com.rafaelsousa.algashop.ordering.domain.model.commons;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void given_validEmail_whenInstantiate_shouldGenerate() {
        String value = "example@email.com";
        Email validEmail = Email.of(value);

        Assertions.assertThat(validEmail.value()).isEqualTo(value);
        Assertions.assertThat(value).isEqualTo(validEmail.toString());
    }

    @Test
    void given_emptyEmail_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Email.of(" "));
    }

    @Test
    void given_nullEmail_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Email.of(null));
    }

    @Test
    void given_invalidEmail_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Email.of("invalid-email"))
                .withMessage(ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID);
    }
}