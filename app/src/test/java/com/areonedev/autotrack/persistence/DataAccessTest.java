package com.areonedev.autotrack.persistence;

import android.util.Log;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataAccessTest extends TestCase {
    private DataAccess dataAccess;
    private static final String dbName = Main.dbName;
    private static final String TAG = "DataAccessTest\t";

    public DataAccessTest(String arg0)
    {
        super(arg0);
    }

    public void setUp() {

        // Use the following statements to run with the stub database:
        stub();
        // or switch to the real database:
        /*real();*/
        // Note the increase in test execution time.
    }

    private void real() {
        Log.d(TAG, "\nStarting Persistence test DataAccess (using REAL)");
        dataAccess = new DataAccessObject(dbName);
        dataAccess.open(Main.getDBPathName());
    }

    private void stub() {
        Services.closeDataAccess();
        Log.d(TAG, "\nStarting Persistence test DataAccess (using STUB)");
        dataAccess = new DataAccessStub(dbName);

        // Injection: Tell Services to use this stub
        Services.createDataAccess(dataAccess);

        // Retrieve it back to ensure we are using the singleton instance
        dataAccess = Services.getDataAccess(dbName);
    }

    public void tearDown() {
        Services.closeDataAccess();
        Log.d(TAG, "Finished Persistence test DataAccess (using STUB)");
    }

    public static void dataAccessTest(DataAccess dataAccess) {
        List<Lead> leads = new ArrayList<>();

        // 1. Test Retrieval
        assertEquals(0, leads.size());
        String result = dataAccess.getLeadSequential(leads);
        assertNull("Database should return null on success", result);
        assertEquals(3, leads.size());
        int initialSize = leads.size();

        // 2. Test Insertion
        // Note: Ensure your Lead constructor matches these parameters
        Lead testLead = new Lead("Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        dataAccess.insertLead(testLead);
        dataAccess.getLeadSequential(leads);
        assertEquals(4, leads.size());

        // 3. Verify Insertion
        leads.clear();
        assertEquals(0, leads.size());
        dataAccess.getLeadSequential(leads);
        assertEquals("List size should increase by 1", initialSize + 1, leads.size());

//        DataAccessTest dataAccessTest = new DataAccessTest("");
//        dataAccessTest.dataAccess=dataAccess;
//        dataAccessTest.testGetLeadSequential();
    }

    public void testGetLeadSequential() {
        System.out.println(TAG + "Running test getLeadSequential: Verify Lead Retrieval");

        List<Lead> leads = new ArrayList<>();
        String result = dataAccess.getLeadSequential(leads);

        // Verify that the database opened correctly (result should be null if no error)
        assertNull("Database should return null on successful retrieval", result);
        // Verify that we can at least access the list
        assertNotNull("Leads list should not be null", leads);
        assertEquals(3, leads.size());

        assertEquals("Alice Chen", leads.getFirst().getName());
    }

    public void testRandom() {
        System.out.println(TAG + "Running test getLeadRandom: Verify Lead Search functionality");
        List<Lead> leads = new ArrayList<>();
        dataAccess.getLeadSequential(leads);
        // Verify that we can at least access the list
        assertEquals(leads.size(), 3);

        // 1. NEGATIVE TEST: Search for a lead that does NOT exist
        Lead nonExistentCriteria = new Lead("Non Existence", "000", 0, "", "", null, "", null);
        ArrayList<Lead> results = dataAccess.getLeadRandom(nonExistentCriteria);

        assertEquals("Should find 0 leads for non-existent criteria", 0, results.size());

        // 2. POSITIVE TEST: Search for a lead that does exist
        Lead testLead = new Lead("Alice Chen", "555-0100", 0, "", "", null, "", null);
        results = dataAccess.getLeadRandom(testLead);
        assertEquals("The leads list should not be empty", 1, results.size());
    }

    public void testInsert() {
        System.out.println(TAG + "Running test insertLead: Verify Lead Insertion");
        ArrayList<Lead>leads = new ArrayList<>();
        dataAccess.getLeadSequential(leads);

        /* Insert Unique */
        Lead testLead = new Lead("Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        ArrayList<Lead> results = dataAccess.getLeadRandom(testLead);
        assertEquals(0, results.size());
        assertEquals(3,leads.size());
        dataAccess.insertLead(testLead); //after insertion
        results = dataAccess.getLeadRandom(testLead);
        assertEquals(1, results.size());
        dataAccess.getLeadSequential(leads);
        assertEquals(4, leads.size());
        assertEquals("Alice Chen",leads.getFirst().getName());
        assertEquals("Test Lead",leads.getLast().getName());

        /* Insert Duplicate */
        testLead = new Lead("Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        results = dataAccess.getLeadRandom(testLead);
        dataAccess.insertLead(testLead);
        // This is a valid lead
        assertEquals(1, results.size());
        dataAccess.getLeadSequential(results);
        //But this insertion should not be inserted
        assertEquals(4,leads.size());

        /* If lead is invalid, then nothing should be inserted*/
        testLead = new Lead();
        results = dataAccess.getLeadRandom(testLead);
        dataAccess.insertLead(testLead);
        // This insertion should not change anything
        assertEquals(0, results.size());
        dataAccess.getLeadSequential(results);
        assertEquals(4,leads.size());
        assertEquals("Alice Chen",leads.getFirst().getName());
        assertEquals("Test Lead",leads.getLast().getName());
    }

    public void testUpdateLead(){
        System.out.println(TAG + "Running test updateLead: Verify Lead Update");

// 1. Arrange: Get an existing lead from the stub (e.g., Alice Chen)
        List<Lead> leads = new ArrayList<>();
        dataAccess.getLeadSequential(leads);
        Lead originalLead = leads.get(0);
        long targetID = originalLead.getID();
        String originalName = originalLead.getName();

        // 2. Act: Modify the lead's information
        // We keep the same ID but change the name and phone
        originalLead.setName("Alice Updated");
        originalLead.setPhoneNumber("999-9999");

        String result = dataAccess.updateLead(originalLead);
        assertNull("Update should return null on success", result);

        // 3. Verify: Fetch the lead again to see if changes stuck
        // We use getLeadRandom with a criteria object containing the ID
        Lead criteria = new Lead();
        criteria.setLeadID(targetID);
        ArrayList<Lead> results = dataAccess.getLeadRandom(criteria);

        // 4. Assertions
        assertEquals("Should still find exactly 1 lead", 1, results.size());
        Lead updatedLead = results.get(0);

        Assert.assertEquals("ID should remain unchanged", targetID, updatedLead.getLeadID());
        assertEquals("Name should be updated to 'Alice Updated'", "Alice Updated", updatedLead.getName());
        Assert.assertEquals("Phone should be updated to '999-9999'", "999-9999", updatedLead.getPhoneNumber());

        // Ensure we didn't accidentally change other leads
        System.out.println(TAG + "Update verified for ID: " + targetID);

    }
}
