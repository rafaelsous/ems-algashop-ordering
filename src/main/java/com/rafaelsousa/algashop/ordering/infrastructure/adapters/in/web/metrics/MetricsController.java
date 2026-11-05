package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.metrics;

import com.rafaelsousa.algashop.ordering.core.ports.in.metrics.ForQueryingMetrics;
import com.rafaelsousa.algashop.ordering.core.ports.out.metrics.SalesOverviewOutput;
import com.rafaelsousa.algashop.ordering.core.ports.out.metrics.TotalOverviewOutput;
import com.rafaelsousa.algashop.ordering.infrastructure.config.security.check.SecurityAnnotations.CanReadMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
public class MetricsController {
	private final ForQueryingMetrics forQueryingMetrics;

	@CanReadMetrics
	@GetMapping("/sales-overview")
	public List<SalesOverviewOutput> getSalesOverview(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonthStart,
	                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonthEnd) {
		if (yearMonthStart == null) {
			yearMonthStart = Year.now().atMonth(1);
		}

		if (yearMonthEnd == null) {
			yearMonthEnd = Year.now().atMonth(12);
		}

		return forQueryingMetrics.getSalesOverview(yearMonthStart, yearMonthEnd);
	}

	@CanReadMetrics
	@GetMapping("/total-overview")
	public TotalOverviewOutput getTotalOverview() {
		return forQueryingMetrics.getTotalOverview();
	}
}
