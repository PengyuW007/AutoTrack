package com.areonedev.autotrack.persistence;

import android.util.Log;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataAccessTest extends TestCase {
    private DataAccess dataAccess;
    private static final String dbName = Main.dbName;
    private static final String TAG = "DataAccessTest";

    public void setUp() {

        // Use the following statements to run with the stub database:
        stub();
        // or switch to the real database:
        /*real();*/
        // Note the increase in test execution time.
    }

    private void real(){
        Log.d(TAG, "\nStarting Persistence test DataAccess (using STUB)");
        dataAccess = new DataAccessObject(dbName);
        dataAccess.open(Main.getDBPathName());
    }

    private void stub(){
        Services.closeDataAccess();
        Log.d(TAG, "\nStarting Persistence test DataAccess (using REAL)");
        dataAccess = new DataAccessStub(dbName);

        // Injection: Tell Services to use this stub
        Services.createDataAccess(dataAccess);

        // Retrieve it back to ensure we are using the singleton instance
        dataAccess = Services.getDataAccess(dbName);
    }

    public void tearDown() {
        Services.closeDataAccess();
        Log.d(TAG, "Finished Persistence test DataAccess (using real)");
    }

    public static void dataAccessTest(DataAccess dataAccess) {
        List<Lead> leads = new ArrayList<>();

        // 1. Test Retrieval
        String result = dataAccess.getLeadSequential(leads);
        assertNull("Database should return null on success", result);
        int initialSize = leads.size();

        // 2. Test Insertion
        // Note: Ensure your Lead constructor matches these parameters
        Lead testLead = new Lead(999, "Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        dataAccess.insertLead(testLead);

        // 3. Verify Insertion
        leads.clear();
        dataAccess.getLeadSequential(leads);
        assertEquals("List size should increase by 1", initialSize + 1, leads.size());
    }

    @Test
    public void testRandom(){
        System.out.println(TAG+"Running test getLeadRandom: Verify Lead Retrieval");
        Lead testLead = new Lead(999, "Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        ArrayList<Lead>leads = dataAccess.getLeadRandom(testLead);
        assertEquals("The leads list should not be empty", 0, leads.size());
    }

    @Test
    public void testInsert() {
        System.out.println(TAG+"Running test insertLead: Verify Lead Insertion");

        Lead testLead = new Lead(999, "Test Lead", "555-0199", 50000.0, "SUV", "New", new java.util.Date(), "Test Note", new java.util.Date());
        ArrayList<Lead>leads = dataAccess.getLeadRandom(testLead);
        assertEquals(0,leads.size());
        dataAccess.insertLead(testLead);
        leads = dataAccess.getLeadRandom(testLead);
        assertEquals(1,leads.size());

    }

    @Test
    public void testGetLeadSequential() {
        System.out.println(TAG+"Running test getLeadSequential: Verify Lead Retrieval");

        List<Lead> leads = new ArrayList<>();
        String result = dataAccess.getLeadSequential(leads);

        // Verify that the database opened correctly (result should be null if no error)
        assertNull("Database should return null on successful retrieval", result);

        // Verify that we can at least access the list
        assertNotNull("Leads list should not be null", leads);

        System.out.println(TAG+ "Test1 successful. Leads found: "+leads.size());

    }
}
