package com.rafaelsousa.algashop.ordering.core.ports.in.shopping;

import java.util.UUID;

public interface ForManagingShoppingCarts {
    void addItem(ShoppingCartItemInput shoppingCartItemInput);
    UUID createNew(UUID rawCustomerId);
    void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId);
    void empty(UUID rawShoppingCartId);
    void delete(UUID rawShoppingCartId);
}