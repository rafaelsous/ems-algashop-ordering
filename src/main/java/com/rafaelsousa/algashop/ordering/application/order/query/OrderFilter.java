package com.rafaelsousa.algashop.ordering.application.order.query;

import com.rafaelsousa.algashop.ordering.application.utility.PageFilter;
import com.rafaelsousa.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {
    private String status;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime placedAtFrom;
    private OffsetDateTime placedAtTo;
    private BigDecimal totalAmountFrom;
    private BigDecimal totalAmountTo;

    public OrderFilter(int size, int page) {
        super(size, page);
    }

    public static OrderFilter ofDefault() {
        return new OrderFilter();
    }

    public static OrderFilter of(int size, int page) {
        return new OrderFilter(size, page);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return Objects.isNull(getSortByProperty()) ? SortType.PLACED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return Objects.isNull(getSortDirection()) ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        CANCELED_AT("canceledAt"),
        READY_AT("readyAt"),
        STATUS("status");

        private final String propertyName;
    }
}