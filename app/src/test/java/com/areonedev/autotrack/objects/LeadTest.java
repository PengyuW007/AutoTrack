package com.areonedev.autotrack.objects;

import junit.framework.TestCase;

import org.junit.Assert;

public class LeadTest extends TestCase {

    private static final String TAG = "LeadTest";
    public LeadTest(String arg0){
        super(arg0);
    }

    // 1. Test basic data storage and retrieval
    public void testLeadConstructor_Getters(){
        System.out.println(TAG + " Running testLeadConstructorAndGetters.");

        Lead lead = new Lead("Alice", "Chen", "204-555-8123", 50000.0, "SUV", "New", null, "Wants a blue one", null);

        assertNotNull("Lead object should be created", lead);
        assertEquals("Alice", lead.getLeadFirstName());
        assertEquals("Chen", lead.getLeadLastName());
        assertEquals("Alice Chen", lead.getLeadName());
        assertEquals(50000.0, lead.getLeadBudget());
        Assert.assertEquals("SUV", lead.getLeadVehicleInterest());
        assertEquals("Wants a blue one", lead.getLeadNotes());

        System.out.println(TAG+ " test testLeadConstructor_Getters finished.");
    }

    // 2. Test that IDs are unique and incrementing (since setLeadID is not allowed)
    public void testLeadIDGeneration() {
        System.out.println(TAG + " Running testLeadIDGeneration.");

        Lead lead1 = new Lead("Alice", "Chen", "111", 0, "", "", null, "", null);
        Lead lead2 = new Lead("Bob", "Smith", "222", 0, "", "", null, "", null);

        assertTrue("Each lead should have a unique ID", lead1.getLeadID() != lead2.getLeadID());
        assertEquals("IDs should increment by 1", lead1.getLeadID() + 1, lead2.getLeadID());

        System.out.println(TAG+ " test testLeadGeneration finished.");
    }

    // 3. Test the logic that splits a full name into First and Last
    public void testLeadNameSplitting() {
        System.out.println(TAG + " Running testLeadNameSplitting.");

        Lead lead = new Lead();

        // Standard case
        lead.setLeadName("John Wick");
        assertEquals("John", lead.getLeadFirstName());
        assertEquals("Wick", lead.getLeadLastName());

        // Edge case: Single name (no space)
        lead.setLeadName("Cher");
        assertEquals("Cher", lead.getLeadFirstName());
        assertTrue("Last name should be empty or null for single names",
                lead.getLeadLastName() == null || lead.getLeadLastName().isEmpty());

        System.out.println(TAG+ " test testLeadNameSplitting finished.");
    }

    // 4. Test Equality logic (equal, Crucial for DataAccessStub.indexOf)
    public void testLeadEquality() {
        System.out.println(TAG + " Running testLeadEquality.");

        // Create two leads with identical data
        Lead lead1 = new Lead("Alice", "Chen", "204-555-8123", 50000.0, "SUV", "New", null, "", null);
        Lead lead2 = new Lead("Alice", "Chen", "204-555-8123", 50000.0, "SUV", "New", null, "", null);

        // Because IDs are auto-generated and unique, duplicate should be checked
        assertTrue("Leads with different IDs should not be equal even if data matches", lead1.equals(lead2));

        // A lead should be equal to itself
        assertTrue("Lead should be equal to itself",lead1.equals(lead1));

        // Test against null and other types
        assertFalse("Lead should not equal null", lead1.equals(null));
        assertFalse("Lead should not equal a different object type", lead1.equals("Some String"));

        System.out.println(TAG+ " test testLeadEquality finished.");
    }

    // 5. Test Null Handling (Prevention of NullPointerException)
    public void testLeadNullSafety() {
        System.out.println(TAG + " Running testLeadNullSafety.");

        // Case A: Verify that providing NO names throws an exception
        try {
            new Lead(null, null, null, 0, null, null, null, null, null);
            fail("Constructor should have thrown IllegalArgumentException for missing both names");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
            assertNotNull(e.getMessage());
        }

        // Case B: Verify that providing ONLY First Name is valid
        Lead leadOnlyFirst = new Lead("Alice", null, null, 0, null, null, null, null, null);
        assertNotNull("First name should not be null", leadOnlyFirst.getLeadFirstName());
        assertEquals("Alice", leadOnlyFirst.getLeadFirstName());
        assertEquals("", leadOnlyFirst.getLeadLastName());
        assertEquals("Alice", leadOnlyFirst.getLeadName()); // Should not have trailing space

        // Case C: Verify that providing ONLY Last Name is valid
        Lead leadOnlyLast = new Lead(null, "Chen", null, 0, null, null, null, null, null);
        assertNotNull("Last name should not be null", leadOnlyLast.getLeadLastName());
        assertEquals("Chen", leadOnlyLast.getLeadLastName());
        assertEquals("", leadOnlyLast.getLeadFirstName());
        assertEquals("Chen", leadOnlyLast.getLeadName()); // Should not have leading space

        System.out.println(TAG+ " test testLeadNullSafety finished.");
    }
}
