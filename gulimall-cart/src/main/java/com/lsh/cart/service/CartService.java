package com.lsh.cart.service;

import com.lsh.cart.vo.Cart;
import com.lsh.cart.vo.CartItem;

public interface CartService {
	CartItem addToCart(Long skuId, Integer num);

	CartItem getCartItem(Long skuId);

	Cart getCartList();

	void clearCart(String cartKey);

}
