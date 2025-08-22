package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductCatalogService;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class BuyNowApplicationServiceIT {
    private final BuyNowApplicationService buyNowApplicationService;
    private final Orders orders;
    private final Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @Autowired
    BuyNowApplicationServiceIT(BuyNowApplicationService buyNowApplicationService, Orders orders, Customers customers) {
        this.buyNowApplicationService = buyNowApplicationService;
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldBuyNow() {
        Product product = ProductTestDataBuilder.aProduct().build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(ShippingCostService.CalculationResponse.builder()
                        .cost(Money.of("19.99"))
                        .expectedDeliveryDate(LocalDate.now().plusDays(7))
                        .build());

        BuyNowInput buyNowInput = BuyNowInputTestDataBuilder.aBuyNowInput().build();

        String orderIdString = buyNowApplicationService.buyNow(buyNowInput);

        assertThat(orderIdString).isNotBlank();
        assertThat(orders.exists(new OrderId(orderIdString))).isTrue();
    }
}