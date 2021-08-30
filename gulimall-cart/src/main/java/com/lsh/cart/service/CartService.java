package com.lsh.cart.service;

import com.lsh.cart.vo.Cart;
import com.lsh.cart.vo.CartItem;

public interface CartService {
	CartItem addToCart(Long skuId, Integer num);

	CartItem getCartItem(Long skuId);

	Cart getCartList();

	void clearCart(String cartKey);

	void checkItem(Long skuId, Integer checked);

	void updateNumItem(Long skuId, Integer num);

	void deleteItem(Long skuId);
}
