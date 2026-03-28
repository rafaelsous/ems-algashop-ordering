package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shipping;

import com.rafaelsousa.algashop.ordering.core.application.shipping.ShippingCostPreviewInput;
import com.rafaelsousa.algashop.ordering.core.application.shipping.ShippingCostPreviewOutput;
import com.rafaelsousa.algashop.ordering.core.application.shipping.ShippingManagementApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shipping-cost-previews")
public class ShippingCostController {
	private final ShippingManagementApplicationService shippingManagementApplicationService;

	@PostMapping
	public ShippingCostPreviewOutput previewCost(@RequestBody @Valid ShippingCostPreviewInput input) {
		return shippingManagementApplicationService.previewCost(input);
	}
}