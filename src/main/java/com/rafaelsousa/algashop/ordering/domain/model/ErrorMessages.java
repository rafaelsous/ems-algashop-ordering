package com.rafaelsousa.algashop.ordering.domain.model;

public class ErrorMessages {
    private ErrorMessages() { }

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email must not be null, blank or invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "Birthdate must be a past date";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_BLANK = "FullName cannot be blank";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived it cannot be changed";

    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED = "Cannot change order %s status from %s to %s";

    public static final String ERROR_ORDER_DELIVERY_DATE_CANNOT_IN_THE_PAST = "Order %s expected delivery date cannot be in the past";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS = "Order %s cannot be placed, it has no items";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO = "Order %s cannot be placed, it has no shipping info";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO = "Order %s cannot be placed, it has no billing info";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD = "Order %s cannot be placed, it has no payment method";

    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ITEM = "Order %s does not contain item %s";

    public static final String ERROR_PRODUCT_OUT_OF_STOCK = "Product %s is out of stock";

    public static final String ERROR_ORDER_CANNOT_BE_EDITED = "Order %s with status %s cannot be edited";

    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM = "Shopping cart %s does not contain item %s";

    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT = "Shopping cart %s does not contain product %s";

    public static final String ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT = "Shopping cart item %s is incompatible with product %s";

    public static final String ERROR_CANNOT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY = "Can not add loyalty points because order %s is not ready";

    public static final String ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER = "Order %s not belongs to customer %s";

    public static final String ERROR_CUSTOMER_EMAIL_IS_IN_USE = "Customer email %s is in use";

    public static final String ERROR_SHOPPING_CART_CANT_PROCEED_TO_CHECKOUT = "Shopping cart %s cant proceed to checkout";

    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer %s not found";

    public static final String ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART = "Customer %s already have shopping car %s";
}