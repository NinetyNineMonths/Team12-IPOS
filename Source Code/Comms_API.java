package IPOS_PUDetailedModel;

public class Comms_API implements I_PU_CommsAPI {

	/**
	 * 
	 * @param to
	 * @param subject
	 * @param body
	 */
	public boolean sendEmail(String to, String subject, String body) {
		// TODO - implement Comms_API.sendEmail
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param orderId
	 * @param amount
	 */
	public boolean authorisePayment(String orderId, double amount) {
		// TODO - implement Comms_API.authorisePayment
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param refId
	 * @param type
	 * @param outcome
	 * @param timestamp
	 */
	public void recordTransaction(String refId, String type, String outcome, DateTime timestamp) {
		// TODO - implement Comms_API.recordTransaction
		throw new UnsupportedOperationException();
	}

}