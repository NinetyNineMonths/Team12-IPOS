package main.model;

import java.util.ArrayList;
import java.util.List;
import main.model.OrderItem;
import main.model.Product;

/**
 * Represents a customer's shopping basket.
 *
 * This class stores selected products as order items,
 * allows items to be added or removed, and calculates
 * the current basket total.
 */

public class Basket {
   private List<OrderItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        items.add(new OrderItem(product, quantity));
    }

    public void removeItem(Product product) {
        items.removeIf(item -> item.getProduct().equals(product));
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
