public interface I_SA_MerchantApplicationAPI {

	/**
	 * 
	 * @param application
	 */
	String submitMerchantApplication(String application);

	/**
	 * 
	 * @param applicationId
	 */
	String getApplicationStatus(String applicationId);

}