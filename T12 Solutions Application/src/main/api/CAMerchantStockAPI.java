package main.api;

/**
 * Interface for stock-related communication with the CA subsystem.
 *
 * This interface defines the operations used to check stock levels,
 * deduct stock after successful sales, search available stock,
 * and submit paid order details to the merchant-side system.
 */
public interface CAMerchantStockAPI {

    /**
     * Returns the current stock level for a given product ID.
     */
    int checkStock(String productId);

    /**
     * Deducts a given quantity of stock for a specified product.
     * Returns true if the stock update is successful.
     */
    boolean deductStock(String productId, int quantity);

    /**
     * Returns a list of available stock items, optionally filtered by keyword.
     */
    String listAvailableStock(String keyword);

    /**
     * Submits a paid order to the CA subsystem for merchant processing.
     */
    String submitPaidOrder(String orderId, String items);
}