package main.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple service for handling commercial membership application submissions.
 *
 * This class stores submitted application details in memory
 * and performs basic validation on required fields.
 */

public class MembershipService {

    private final List<String> commercialApplications = new ArrayList<>();

    /**
     * Submits a commercial membership application if all required fields are present.
     * Returns true if the application is accepted for storage.
     */
    public boolean submitCommercialApplication(
            String companyName,
            String companyAddress,
            String directorName,
            String directorContact,
            String notificationMethod
    ) {
        if (companyName == null || companyName.trim().isEmpty()) return false;
        if (companyAddress == null || companyAddress.trim().isEmpty()) return false;
        if (directorName == null || directorName.trim().isEmpty()) return false;
        if (directorContact == null || directorContact.trim().isEmpty()) return false;
        if (notificationMethod == null || notificationMethod.trim().isEmpty()) return false;

        String application = "Company: " + companyName
                + ", Address: " + companyAddress
                + ", Director: " + directorName
                + ", Contact: " + directorContact
                + ", Notify by: " + notificationMethod;

        commercialApplications.add(application);
        return true;
    }

    /**
     * Returns all stored commercial membership applications.
     */
    public List<String> getCommercialApplications() {
        return commercialApplications;
    }
}