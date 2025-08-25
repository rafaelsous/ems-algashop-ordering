package com.rafaelsousa.algashop.ordering.application.customer.notification;

import lombok.Builder;

import java.util.UUID;

public interface CustomerNotificationApplicationService {
    void notifyNewRegistration(NotifyNewRegistrationInput notifyNewRegistrationInput);

    @Builder
    record NotifyNewRegistrationInput(UUID rawCustomerId, String firstName, String email) { }
}