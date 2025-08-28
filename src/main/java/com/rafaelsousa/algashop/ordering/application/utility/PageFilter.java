package com.rafaelsousa.algashop.ordering.application.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor@AllArgsConstructor
public class PageFilter {
    private int size = 15;
    private int page = 0;

    public static PageFilter of(int size, int page) {
        return new PageFilter(size, page);
    }
}