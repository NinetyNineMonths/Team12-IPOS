public interface I_SA_MerchantOrderingAPI {

	/**
	 * 
	 * @param keyword
	 */
	String searchCatalogue(String keyword);

	/**
	 * 
	 * @param productId
	 */
	String getProductDetails(String productId);

	/**
	 * 
	 * @param merchantId
	 * @param items
	 */
	String createOrder(String merchantId, String items);

	/**
	 * 
	 * @param merchantId
	 * @param orderId
	 */
	boolean cancelOrder(String merchantId, String orderId);

	/**
	 * 
	 * @param merchantId
	 * @param orderId
	 */
	String getOrderStatus(String merchantId, String orderId);

	/**
	 * 
	 * @param merchantId
	 * @param orderId
	 */
	String getInvoice(String merchantId, String orderId);

}