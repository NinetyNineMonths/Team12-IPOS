public interface I_PU_CommsAPI {

	/**
	 * 
	 * @param to
	 * @param subject
	 * @param body
	 */
	boolean sendEmail(String to, String subject, String body);

	/**
	 * 
	 * @param orderId
	 * @param amount
	 */
	boolean authorisePayment(String orderId, double amount);

	/**
	 * 
	 * @param refId
	 * @param type
	 * @param outcome
	 * @param timestamp
	 */
	void recordTransaction(String refId, String type, String outcome, DateTime timestamp);

}