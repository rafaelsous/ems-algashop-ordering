package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductId;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartUpdateProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        ShoppingCartUpdateProvider.class,
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceAssembler.class,
        ShoppingCartPersistenceDisassembler.class,
        SpringDataAuditingConfig.class,
        HibernateConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShoppingCartUpdateProviderIT {
    private final ShoppingCartUpdateProvider shoppingCartUpdateProvider;
    private final ShoppingCartsPersistenceProvider shoppingCartsPersistenceProvider;
    private final CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public ShoppingCartUpdateProviderIT(ShoppingCartUpdateProvider shoppingCartUpdateProvider,
                                        ShoppingCartsPersistenceProvider shoppingCartsPersistenceProvider,
                                        CustomersPersistenceProvider customersPersistenceProvider) {
        this.shoppingCartUpdateProvider = shoppingCartUpdateProvider;
        this.shoppingCartsPersistenceProvider = shoppingCartsPersistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @BeforeEach
    void setUp() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemPriceAndTotalAmount() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product1 = ProductTestDataBuilder.aProduct().price(Money.of("2000.00")).build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory().price(Money.of("200.00")).build();

        shoppingCart.addItem(product1, Quantity.of(2));
        shoppingCart.addItem(product2, Quantity.of(1));

        shoppingCartsPersistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        Money updatedProduct1Price = Money.of("1500.00");
        Money expectedNewItemTotalPrice = updatedProduct1Price.multiply(Quantity.of(2));
        Money expectedNewCartTotalAmount = expectedNewItemTotalPrice.add(Money.of("200.00"));

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, updatedProduct1Price);

        ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.totalItems()).isEqualTo(Quantity.of(3));
        assertThat(updatedShoppingCart.totalAmount()).isEqualTo(expectedNewCartTotalAmount);

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);

        assertThat(item.totalAmount()).isEqualTo(expectedNewItemTotalPrice);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemAvailability() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product product1 = ProductTestDataBuilder.aProduct()
                .price(Money.of("2000.00"))
                .inStock(true)
                .build();
        Product product2 = ProductTestDataBuilder.aProductAltRamMemory()
                .price(Money.of("200.00"))
                .inStock(true)
                .build();

        shoppingCart.addItem(product1, Quantity.of(2));
        shoppingCart.addItem(product2, Quantity.of(1));

        shoppingCartsPersistenceProvider.add(shoppingCart);

        ProductId productIdToUpdate = product1.id();
        ProductId productIdToNotUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();

        ShoppingCartItem item = updatedShoppingCart.findItem(productIdToUpdate);
        assertThat(item.isAvailable()).isFalse();

        ShoppingCartItem item2 = updatedShoppingCart.findItem(productIdToNotUpdate);
        assertThat(item2.isAvailable()).isTrue();

        assertThat(updatedShoppingCart.containsUnavailableItems()).isTrue();
    }
}