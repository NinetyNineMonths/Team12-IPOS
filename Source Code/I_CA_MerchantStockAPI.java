public interface I_CA_MerchantStockAPI {

	/**
	 * 
	 * @param productId
	 */
	int checkStock(String productId);

	/**
	 * 
	 * @param productId
	 * @param quantity
	 */
	boolean deductStock(String productId, int quantity);

	/**
	 * 
	 * @param keyword
	 */
	String listAvailableStock(String keyword);

	/**
	 * 
	 * @param orderId
	 * @param items
	 */
	String submitPaidOrder(String orderId, String items);

}