package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart deleteCart(int userId);
    ShoppingCartItem getByProductId(int product_id);
    int addItem(int userId, int productId);
    int addItem(int userId, int productId, int quantity);
}
