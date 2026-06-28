package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.rafaelsousa.algashop.ordering.core.application.security.SecurityChecks;
import com.rafaelsousa.algashop.ordering.core.ports.in.customer.*;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanWriteMyCustomerProfile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/me")
public class MyCustomerController {
	private final ForManagingCustomers forManagingCustomers;
	private final ForQueryingCustomers forQueryingCustomers;
	private final SecurityChecks securityChecks;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CanWriteMyCustomerProfile
	public CustomerOutput create(@RequestBody @Valid CustomerInput customerInput, HttpServletResponse httpServletResponse) {
		UUID customerId = forManagingCustomers.create(securityChecks.getAuthenticatedUserId(), customerInput);

		UriComponentsBuilder uriComponentsBuilder = fromMethodCall(on(MyCustomerController.class).load());
		httpServletResponse.setHeader(HttpHeaders.LOCATION, uriComponentsBuilder.toUriString());

		return forQueryingCustomers.findById(customerId);
	}

	@GetMapping
	@CanReadMyCustomerProfile
	public CustomerOutput load() {
		return forQueryingCustomers.findById(securityChecks.getAuthenticatedUserId());
	}

	@PutMapping
	@CanWriteMyCustomerProfile
	public CustomerOutput update(@RequestBody @Valid CustomerUpdateInput customerUpdateInput) {
		forManagingCustomers.update(securityChecks.getAuthenticatedUserId(), customerUpdateInput);

		return forQueryingCustomers.findById(securityChecks.getAuthenticatedUserId());
	}
}
