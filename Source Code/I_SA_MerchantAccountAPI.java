public interface I_SA_MerchantAccountAPI {

	/**
	 * 
	 * @param merchantId
	 */
	String getAccountStatus(String merchantId);

	/**
	 * 
	 * @param merchantId
	 */
	double getOutstandingBalance(String merchantId);

	/**
	 * 
	 * @param merchantId
	 */
	String listOrders(String merchantId);

}