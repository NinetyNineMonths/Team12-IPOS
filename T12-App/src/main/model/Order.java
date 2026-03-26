package main.model;

public class Order {
  private String orderId;
    private List<OrderLine> items;
    private double total;

    public Order(String orderId, List<OrderLine> items) {
        this.orderId = orderId;
        this.items = items;
        calculateTotal();
    }

    private void calculateTotal() {
        total = 0;
        for (OrderLine item : items) {
            total += item.getPrice();
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderLine> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}
}
