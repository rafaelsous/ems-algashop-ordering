package com.rafaelsousa.algashop.ordering.core.application.utility;

public interface Mapper {
    <T> T convert(Object source, Class<T> destinationType);
}