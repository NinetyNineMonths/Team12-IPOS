package main.model;

/**
 * Represents a product in the IPOS-PU catalogue.
 *
 * This class stores identifying, descriptive, pricing,
 * packaging, and stock-related information for a product.
 */

public class Product {
    private final String id;
    private final String name;
    private final String description;
    private final String packageType;
    private final String unitType;
    private final int packSize;
    private final double wholesaleCost;
    private final double retailPrice;
    private final int stock;
    private final int stockLimit;

    public Product(String id, String name, String description, String packageType, String unitType, int packSize, double wholesaleCost, double retailPrice, int stock, int stockLimit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.packageType = packageType;
        this.unitType = unitType;
        this.packSize = packSize;
        this.wholesaleCost = wholesaleCost;
        this.retailPrice = retailPrice;
        this.stock = stock;
        this.stockLimit = stockLimit;
    }

    //GETTERS

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPackageType() { return packageType; }
    public String getUnitType() { return unitType; }
    public int getPackSize() { return packSize; }
    public double getWholesaleCost() { return wholesaleCost; }
    public double getRetailPrice() { return retailPrice; }
    public int getStock() { return stock; }
    public int getStockLimit() { return stockLimit; }
}
