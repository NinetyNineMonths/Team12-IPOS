package main.api;

import main.exception.IntegrationException;
import main.exception.NotFoundException;
import main.exception.ValidationException;
import main.model.CommercialApplication;

import java.util.List;

public interface SAMerchantApplicationAPI {

    void submitMerchantApplication(CommercialApplication application)
            throws ValidationException, IntegrationException;

    CommercialApplication getApplicationById(String applicationId)
            throws ValidationException, NotFoundException, IntegrationException;

    List<CommercialApplication> getPendingApplications()
            throws IntegrationException;

    void updateApplicationStatus(String applicationId, String newStatus)
            throws ValidationException, NotFoundException, IntegrationException;
}