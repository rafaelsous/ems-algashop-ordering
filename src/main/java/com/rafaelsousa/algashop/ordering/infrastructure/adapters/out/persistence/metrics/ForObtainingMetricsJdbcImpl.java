package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.metrics;

import com.rafaelsousa.algashop.ordering.core.ports.out.metrics.ForObtainingMetrics;
import com.rafaelsousa.algashop.ordering.core.ports.out.metrics.SalesOverviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForObtainingMetricsJdbcImpl implements ForObtainingMetrics {
	private static final String SALES_OVERVIEW_SQL = """
		SELECT
		      to_char(g, 'yyyy-mm') AS year_month,
		      count(o.id) AS quantity,
		      COALESCE(sum(o.total_amount), 0) AS revenue
		  FROM
		      generate_series(
		          CAST(:startDate AS date),
		          CAST(:endDate AS date),
		          INTERVAL '1 month'
		      ) AS g
		      LEFT JOIN "order" o ON date_trunc('month', o.paid_at) = date_trunc('month', g)
		      AND o.paid_at IS NOT NULL
		      AND o.canceled_at IS NULL
		  GROUP BY
		      1
		  ORDER BY
		      1
		""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<SalesOverviewOutput> getSalesOverview(LocalDate startDate, LocalDate endDate) {
		return jdbcTemplate.query(SALES_OVERVIEW_SQL,
			Map.of("startDate", startDate, "endDate", endDate),
			(rs, _) -> new SalesOverviewOutput(
					YearMonth.parse(rs.getString("year_month")),
					rs.getInt("quantity"),
					rs.getBigDecimal("revenue")
				)
			);
	}
}
