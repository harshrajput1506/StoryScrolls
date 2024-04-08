package com.hn.farm.listeners;

import com.hn.farm.models.Product;

public interface CartListener {
    void onDelete(String id, int index);
    void onAddQuantity(Product product, int quantity);
    void onRemoveQuantity(Product product, int quantity);
}
