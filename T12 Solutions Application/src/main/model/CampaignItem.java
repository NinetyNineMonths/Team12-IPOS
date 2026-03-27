package main.model;

public class CampaignItem {
    private final String itemId;
    private final double discountRate;

    public CampaignItem(String itemId, double discountRate) {
        this.itemId = itemId;
        this.discountRate = discountRate;
    }

    // GETTERS
    public String getItemId() { return itemId; }
    public double getDiscountRate() { return discountRate; }

    @Override 
    public String toString() {
        return String.format("CampaignItem[itemId=%s, discount=%.1f%%]", itemId, discountRate);
    }
}