package com.rafaelsousa.algashop.ordering.infrastructure.config.security.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

public class SecurityAnnotations {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_orders:read')")
	public @interface CanReadOrders {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_orders:write')")
	public @interface CanWriteOrders {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_customers:read')")
	public @interface CanReadCustomers {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_customers:write')")
	public @interface CanWriteCustomers {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_shopping-carts:read')")
	public @interface CanReadShoppingCarts {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_shopping-carts:write')")
	public @interface CanWriteShoppingCarts {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@PreAuthorize("hasAuthority('SCOPE_shipping-costs:preview')")
	public @interface CanPreviewShippingCosts {}
}