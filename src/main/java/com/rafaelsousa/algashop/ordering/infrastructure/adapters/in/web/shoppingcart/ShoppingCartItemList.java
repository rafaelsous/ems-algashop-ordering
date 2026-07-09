package com.rafaelsousa.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartItemOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemList {
	private List<ShoppingCartItemOutput> items;
}
