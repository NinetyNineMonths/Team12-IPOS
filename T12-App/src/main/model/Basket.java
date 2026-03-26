package main.model;

public class Basket {
   private List<OrderLine> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        items.add(new OrderLine(product, quantity));
    }

    public void removeItem(Product product) {
        items.removeIf(item -> item.getProduct().equals(product));
    }

    public List<OrderLine> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (OrderLine item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
