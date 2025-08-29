package com.rafaelsousa.algashop.ordering.application.customer.query;

import com.rafaelsousa.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerFilter extends SortablePageFilter<CustomerFilter.SortType> {
    private String email;
    private String firstName;

    public CustomerFilter(int size, int page) {
        super(size, page);
    }

    public static CustomerFilter ofDefault() {
        return new CustomerFilter();
    }

    public static CustomerFilter of(int size, int page) {
        return new CustomerFilter(size, page);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.REGISTERED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        REGISTERED_AT("registeredAt"),
        FIRST_NAME("firstName");

        private final String propertyName;
    }
}