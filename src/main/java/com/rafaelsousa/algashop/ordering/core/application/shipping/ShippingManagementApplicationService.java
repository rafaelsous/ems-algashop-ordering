package com.rafaelsousa.algashop.ordering.core.application.shipping;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Address;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.ZipCode;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingManagementApplicationService {
	private final OriginAddressService originAddressService;
	private final ShippingCostService shippingCostService;

	public ShippingCostPreviewOutput previewCost(ShippingCostPreviewInput input) {
		Address originAddress = originAddressService.originAddress();
		ShippingCostService.CalculationRequest calculationRequest = ShippingCostService.CalculationRequest.builder()
				.origin(originAddress.zipCode())
				.destination(ZipCode.of(input.getZipCode()))
				.build();

		ShippingCostService.CalculationResponse calculationResponse = shippingCostService.calculate(calculationRequest);

		return ShippingCostPreviewOutput.builder()
				.cost(calculationResponse.cost().value())
				.expectedDate(calculationResponse.expectedDate())
				.build();
	}
}