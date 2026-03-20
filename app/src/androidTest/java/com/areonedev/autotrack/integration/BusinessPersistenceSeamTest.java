package com.areonedev.autotrack.integration;

import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.persistence.DataAccessObject;
import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.application.*;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BusinessPersistenceSeamTest {
    private AccessLeads al;
    private static final String dbName = Main.dbName;
    private static final String TAG = "BusinessPersistenceSeamTest";

    @Before
    public void setUp() throws Exception {
        // 1. Get the REAL Android path
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String dbPath = context.getDatabasePath(dbName).getAbsolutePath();

        // 2. Initialize the REAL DataAccessObject
        DataAccess dao = new DataAccessObject(dbName);
        dao.open(dbPath);

        // 3. Inject into Services
        Services.closeDataAccess();
        Services.createDataAccess(dao);

        al = new AccessLeads(); // Business controller now talks to REAL DB
    }

    @Test
    public void testInsertLeadSeam() {
        List<Lead> leads = new ArrayList<>();
        al.getLeads(leads);
        int initialSize = leads.size();

        // Create dates using java.util.Date
        Date followUp = new Date();
        Date created = new Date();

        Lead newLead = new Lead("Integration", "User", "555-0000", 100.0, "SUV", "New", followUp, "Test Note", created);

        // Verify initial state
        Assert.assertEquals("New lead should have ID 0 before insertion", 0, newLead.getLeadID());

        // Perform the Seam Insert (Business -> Persistence -> SQLite)
        String result = al.insertLead(newLead);
        Assert.assertNull("Insert should return null on success, but got: " + result, result);

        // Verify ID Assignment (The DB should have updated the object)
        Assert.assertTrue("Lead ID should be > 0 after SQLite insertion", newLead.getLeadID() > 0);

        // Verify Retrieval
        List<Lead> updatedLeads = new ArrayList<>();
        al.getLeads(updatedLeads);
        Assert.assertEquals("Database size should have increased by 1", initialSize + 1, updatedLeads.size());

        Log.d(TAG, "Successfully inserted lead with assigned ID: " + newLead.getLeadID());}
}
