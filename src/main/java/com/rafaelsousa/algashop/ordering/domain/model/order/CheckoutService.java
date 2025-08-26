package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {
    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order checkout(Customer customer, ShoppingCart shoppingCart, Billing billing,
                          Shipping shipping, PaymentMethod paymentMethod) {
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException(shoppingCart.id());
        }

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }

        order.changePaymentMethod(paymentMethod);

        addShoppingCartItemsToOrder(shoppingCart.items(), order);

        order.place();
        shoppingCart.empty();

        return order;
    }

    private void addShoppingCartItemsToOrder(Set<ShoppingCartItem> items, Order order) {
        items.forEach(shoppingCartItem -> addShoppingCartItemToOrder(shoppingCartItem, order));
    }

    private void addShoppingCartItemToOrder(ShoppingCartItem item, Order order) {
        Product product = Product.builder()
                .id(item.productId())
                .name(item.productName())
                .price(item.price())
                .inStock(item.isAvailable())
                .build();

        order.addItem(product, item.quantity());
    }

    private boolean haveFreeShipping(Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }
}