package com.areonedev.autotrack.persistence;

import android.util.Log;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;
import com.google.type.Date;

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
        dataAccess = new DataAccessObject(Main.dbName);
        dataAccess.open(Main.getDBPathName());
    }

    private void stub(){
        Log.d(TAG, "\nStarting Persistence test DataAccess (using REAL)");
//        dataAccess = new DataAccessStub(Main.dbName);
//        dataAccess.open(Main.getDBPathName());
        Services.createDataAccess(dbName);
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


//    @Test
//    public void test1() {
//        Log.d(TAG, "Running test1: Verify Lead Retrieval");
//
//        List<Lead> leads = new ArrayList<>();
//        String result = dataAccess.getLeadSequential(leads);
//
//        // Verify that the database opened correctly (result should be null if no error)
//        assertNull("Database should return null on successful retrieval", result);
//
//        // Verify that we can at least access the list
//        assertNotNull("Leads list should not be null", leads);
//
//        Log.d(TAG, "Test1 successful. Leads found: " + leads.size());
//    }
}
