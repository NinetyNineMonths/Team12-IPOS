package tests.service;

import main.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MembershipServiceTest {

    private MembershipService membershipService;

    @BeforeEach
    void setUp() {
        membershipService = new MembershipService();
    }

    // Expected: returns true and stores one application for valid input.
    @Test
    void testSubmitCommercialApplication_ValidInput_ReturnsTrueAndStoresApplication() {
        boolean result = membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                "Email"
        );

        List<String> applications = membershipService.getCommercialApplications();
        assertTrue(result);
        assertEquals(1, applications.size());
    }

    // Expected: returns false and stores nothing when company name is blank.
    @Test
    void testSubmitCommercialApplication_BlankCompanyName_ReturnsFalse() {
        boolean result = membershipService.submitCommercialApplication(
                " ",
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                "Email"
        );

        assertFalse(result);
        assertTrue(membershipService.getCommercialApplications().isEmpty());
    }

    // Expected: returns false and stores nothing when required fields are null.
    @Test
    void testSubmitCommercialApplication_NullRequiredFields_ReturnsFalse() {
        assertFalse(membershipService.submitCommercialApplication(
                null,
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                "Email"
        ));

        assertFalse(membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                null,
                "Jane Director",
                "01234 567890",
                "Email"
        ));

        assertFalse(membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                null,
                "01234 567890",
                "Email"
        ));

        assertFalse(membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                "Jane Director",
                null,
                "Email"
        ));

        assertFalse(membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                null
        ));

        assertTrue(membershipService.getCommercialApplications().isEmpty());
    }

    // Expected: stored application contains all submitted details in readable format.
    @Test
    void testSubmitCommercialApplication_StoredStringContainsSubmittedData() {
        membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                "SMS"
        );

        String application = membershipService.getCommercialApplications().get(0);
        assertTrue(application.contains("Company: MediCorp Ltd"));
        assertTrue(application.contains("Address: 10 Health Street, London"));
        assertTrue(application.contains("Director: Jane Director"));
        assertTrue(application.contains("Contact: 01234 567890"));
        assertTrue(application.contains("Notify by: SMS"));
    }

    // Expected: applications list reflects multiple successful submissions.
    @Test
    void testGetCommercialApplications_MultipleSubmissions_ReturnsAll() {
        membershipService.submitCommercialApplication(
                "MediCorp Ltd",
                "10 Health Street, London",
                "Jane Director",
                "01234 567890",
                "Email"
        );

        membershipService.submitCommercialApplication(
                "PharmaTrade Plc",
                "88 Clinic Road, Manchester",
                "John Manager",
                "02000 123456",
                "Phone"
        );

        List<String> applications = membershipService.getCommercialApplications();
        assertEquals(2, applications.size());
    }
}
