package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.exceptionhandler;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException() {
    }

    public GatewayTimeoutException(String message) {
        super(message);
    }

    public GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
