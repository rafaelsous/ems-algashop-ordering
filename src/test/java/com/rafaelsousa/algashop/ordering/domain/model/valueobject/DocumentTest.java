package com.rafaelsousa.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    void given_validDocument_whenInstantiate_shouldGenerate() {
        String value = "123-12-1234";
        Document document = Document.of(value);

        Assertions.assertThat(document.value()).isEqualTo(value);
    }

    @Test
    void given_nullDocument_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Document.of(null));
    }

    @Test
    void given_blankDocument_whenTryToInstantiate_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Document.of(" "));
    }

    @Test
    void given_validDocument_whenCallToString_shouldReturnDocumentValue() {
        String value = "00000000000";
        Document document = Document.of(value);

        Assertions.assertThat(value).isEqualTo(document.toString());
    }
}