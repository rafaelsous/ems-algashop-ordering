package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.application.commons.AddressData;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {
    private final CustomerManagementApplicationService customerManagementApplicationService;

    @Autowired
    CustomerManagementApplicationServiceIT(CustomerManagementApplicationService customerManagementApplicationService) {
        this.customerManagementApplicationService = customerManagementApplicationService;
    }

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @Test
    void shouldRegisterAndFindCustomer() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId),
                co -> assertThat(co.getFirstName()).isEqualTo(customerInput.getFirstName()),
                co -> assertThat(co.getLastName()).isEqualTo(customerInput.getLastName()),
                co -> assertThat(co.getBirthDate()).isEqualTo(customerInput.getBirthDate()),
                co -> assertThat(co.getDocument()).isEqualTo(customerInput.getDocument()),
                co -> assertThat(co.getPhone()).isEqualTo(customerInput.getPhone()),
                co -> assertThat(co.getEmail()).isEqualTo(customerInput.getEmail()),
                co -> assertThat(co.getPromotionNotificationsAllowed()).isFalse(),
                co -> assertThat(co.getArchived()).isFalse(),
                co -> assertThat(co.getRegisteredAt()).isNotNull(),
                co -> assertThat(co.getLoyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO.value()),
                co -> {
                    AddressData address = customerInput.getAddress();
                    assertThat(co.getAddress()).satisfies(
                            ad -> assertThat(ad.getStreet()).isEqualTo(address.getStreet()),
                            ad -> assertThat(ad.getNumber()).isEqualTo(address.getNumber()),
                            ad -> assertThat(ad.getComplement()).isEqualTo(address.getComplement()),
                            ad -> assertThat(ad.getNeighborhood()).isEqualTo(address.getNeighborhood()),
                            ad -> assertThat(ad.getCity()).isEqualTo(address.getCity()),
                            ad -> assertThat(ad.getState()).isEqualTo(address.getState()),
                            ad -> assertThat(ad.getZipCode()).isEqualTo(address.getZipCode())
                    );
                }
        );

        verify(customerEventListener).listen(any(CustomerRegisteredEvent.class));
    }

    @Test
    void shouldUpdateAndFindCustomer() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerUpdateInput customerUpdateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, customerUpdateInput);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId),
                co -> assertThat(co.getFirstName()).isEqualTo(customerUpdateInput.getFirstName()),
                co -> assertThat(co.getLastName()).isEqualTo(customerUpdateInput.getLastName()),
                co -> assertThat(co.getPhone()).isEqualTo(customerUpdateInput.getPhone()),
                co -> assertThat(co.getPromotionNotificationsAllowed()).isTrue(),
                co -> assertThat(co.getArchived()).isFalse(),
                co -> assertThat(co.getRegisteredAt()).isNotNull(),
                co -> assertThat(co.getLoyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO.value()),
                co -> {
                    AddressData address = customerUpdateInput.getAddress();
                    assertThat(co.getAddress()).satisfies(
                            ad -> assertThat(ad.getStreet()).isEqualTo(address.getStreet()),
                            ad -> assertThat(ad.getNumber()).isEqualTo(address.getNumber()),
                            ad -> assertThat(ad.getComplement()).isEqualTo(address.getComplement()),
                            ad -> assertThat(ad.getNeighborhood()).isEqualTo(address.getNeighborhood()),
                            ad -> assertThat(ad.getCity()).isEqualTo(address.getCity()),
                            ad -> assertThat(ad.getState()).isEqualTo(address.getState()),
                            ad -> assertThat(ad.getZipCode()).isEqualTo(address.getZipCode())
                    );
                }
        );
    }

    @Test
    void shouldArchiveCustomer() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId),
                co -> assertThat(co.isArchived()).isTrue(),
                co -> assertThat(co.getArchivedAt()).isNotNull(),
                co -> assertThat(co.getFirstName()).isEqualTo("Anonymous"),
                co -> assertThat(co.getLastName()).isEqualTo("Anonymous"),
                co -> assertThat(co.getPhone()).isEqualTo("000-000-0000"),
                co -> assertThat(co.getDocument()).isEqualTo("000-00-0000"),
                co -> assertThat(co.getEmail()).endsWith("@anonymous.com"),
                co -> assertThat(co.getBirthDate()).isNull(),
                co -> assertThat(co.getPromotionNotificationsAllowed()).isFalse(),
                co -> assertThat(co.getAddress()).satisfies(
                        ad -> assertThat(ad.getNumber()).isEqualTo("Anonymized"),
                        ad -> assertThat(ad.getComplement()).isNull()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenTryToArchiveInexistingCustomer() {
        UUID customerId = UUID.randomUUID();

        assertThat(customerId).isNotNull();

        assertThatThrownBy(() -> customerManagementApplicationService.archive(customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(customerId));
    }

    @Test
    void shouldThrowExceptionWhenTryToArchiveCustomerAlreadyArchived() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        assertThatThrownBy(() -> customerManagementApplicationService.archive(customerId))
                .isInstanceOf(CustomerArchivedException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_ARCHIVED);
    }

    @Test
    void shouldChangeEmail() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        String newEmail = "new-email@example.com";
        customerManagementApplicationService.changeEmail(customerId, newEmail);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId),
                co -> assertThat(co.getEmail()).isEqualTo(newEmail)
        );
    }

    @Test
    void shouldThrowExceptionWhenTryToChangeEmailOfNonExistentCustomer() {
        UUID customerId = UUID.randomUUID();

        String newEmail = "new-email@example.com";

        assertThatThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, newEmail))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(customerId));
    }

    @Test
    void shouldThrowExceptionWhenTryToChangeEmailOfArchivedCustomer() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        String newEmail = "new-email@example.com";

        assertThatThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, newEmail))
                .isInstanceOf(CustomerArchivedException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_ARCHIVED);
    }

    @Test
    void shouldThrowExceptionWhenTryToChangeEmailWithInvalidEmail() {
        CustomerInput customerInput = CustomerInputTestDataBuilder.aCustomer().build();

        UUID customerId = customerManagementApplicationService.create(customerInput);

        assertThat(customerId).isNotNull();

        String newEmail = "invalid-email";

        assertThatThrownBy(() -> customerManagementApplicationService.changeEmail(customerId, newEmail))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenTryToChangeEmailUsingAlreadyExistingEmail() {
        CustomerInput customerInput1 = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerInput customerInput2 = CustomerInputTestDataBuilder.aCustomer().email("customer2@email.com").build();

        UUID customerId1 = customerManagementApplicationService.create(customerInput1);
        UUID customerId2 = customerManagementApplicationService.create(customerInput2);

        assertThat(customerId1).isNotNull();
        assertThat(customerId2).isNotNull();

        String newEmail = customerInput2.getEmail();

        assertThatThrownBy(() -> customerManagementApplicationService.changeEmail(customerId1, newEmail))
                .isInstanceOf(CustomerEmailAlreadyExistsException.class);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId1);

        assertThat(customerOutput).satisfies(
                co -> assertThat(co.getId()).isEqualTo(customerId1),
                co -> assertThat(co.getEmail()).isEqualTo(customerInput1.getEmail())
        );
    }
}