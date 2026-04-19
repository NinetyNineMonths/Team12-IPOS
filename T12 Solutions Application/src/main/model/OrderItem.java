package main.model;

import main.model.Product;

/**
 * Represents a single product entry within an order.
 *
 * This class stores the selected product, the quantity ordered,
 * and the calculated total price for that item line.
 */

public class OrderItem {
   private Product product;
    private int quantity;
    private double price;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getRetailPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
