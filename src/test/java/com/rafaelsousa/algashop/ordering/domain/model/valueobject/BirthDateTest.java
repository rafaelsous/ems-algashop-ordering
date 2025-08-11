package com.rafaelsousa.algashop.ordering.domain.model.valueobject;

import com.rafaelsousa.algashop.ordering.domain.model.exception.ErrorMessages;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BirthDateTest {

    @Test
    void given_validBirthDate_whenInstantiate_shouldGenerate() {
        BirthDate birthDate = BirthDate.of(LocalDate.of(1990, 1, 1));

        Assertions.assertThat(birthDate.value()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void given_nullBirthDate_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> BirthDate.of(null));
    }

    @Test
    void given_futureBirthDate_whenTryToInstantiate_shouldGenerateException() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> BirthDate.of(futureDate))
                .withMessage(ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
    }

    @Test
    void given_validBirthDate_whenCallAge_shouldReturnAgeInYears() {
        BirthDate birthDate = BirthDate.of(LocalDate.of(1990, 1, 1));

        Assertions.assertThat(birthDate.age()).isEqualTo(35);
    }

    @Test
    void given_validBirthDate_whenCallToString_shouldReturnStringDate() {
        LocalDate value = LocalDate.of(1990, 1, 1);
        BirthDate birthDate = BirthDate.of(value);
        String birthDateString = value.toString();

        Assertions.assertThat(birthDateString).isEqualTo(birthDate.toString());
    }
}