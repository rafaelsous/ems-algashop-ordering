package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.rafaelsousa.algashop.ordering.domain.model.DomainService;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;

import java.util.Set;

@DomainService
public class CheckoutService {

    public Order checkout(ShoppingCart shoppingCart, Billing billing, Shipping shipping, PaymentMethod paymentMethod) {
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException(shoppingCart.id());
        }

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);
        order.changeShipping(shipping);
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
}