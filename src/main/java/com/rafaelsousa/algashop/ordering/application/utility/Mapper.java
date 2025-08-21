package com.rafaelsousa.algashop.ordering.application.utility;

public interface Mapper {
    <T> T convert(Object source, Class<T> destinationType);
}