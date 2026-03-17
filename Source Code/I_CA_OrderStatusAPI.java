public interface I_CA_OrderStatusAPI {

	/**
	 * 
	 * @param orderId
	 */
	String getOrderStatus(String orderId);

	/**
	 * 
	 * @param since
	 */
	DateTime listUpdatedOrders(DateTime since);

}