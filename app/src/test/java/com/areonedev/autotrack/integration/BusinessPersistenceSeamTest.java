package com.areonedev.autotrack.integration;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.persistence.DataAccessStub;

import junit.framework.TestCase;

import org.junit.Assert;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class BusinessPersistenceSeamTest extends TestCase {

    private static final String TAG = "BusinessPersistenceSeamTest, ";
    private AccessLeads al;
    public BusinessPersistenceSeamTest(String arg0)
    {
        super(arg0);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Services.createDataAccess(new DataAccessStub());
        al = new AccessLeads(); // Initialize the business controller
    }

    // Runs AFTER every test method
    @Override
    public void tearDown() throws Exception {
        Services.closeDataAccess(); // Reset the Stub/Database
        super.tearDown();
    }

    public void testGetLeadsSeam() {
        System.out.println(TAG + " Running testGetLeadsSeam.");
        List<Lead> leads = new ArrayList<>();
        al.getLeads(leads);
        assertEquals(3, leads.size());
    }

    public void testGetSequential(){
        System.out.println(TAG + " Running testGetSequential.");
        Lead l1 = al.getSequential();
        assertEquals("Alice",l1.getLeadFirstName());

        // Second call -> Brian
        Lead l2 = al.getSequential();
        assertEquals("Brian Miller", l2.getLeadName());

        // Third call -> Sophia
        Lead l3 = al.getSequential();
        assertEquals("Sophia Martinez", l3.getLeadName());

        // Fourth call -> Already hit the end of the list, so should return null
        Lead l4 = al.getSequential();
        assertNull(l4);

        System.out.println(TAG+" Finished testGetSequential.");
    }

    public void testGetRandom(){
        System.out.println(TAG+" Running testGetRandom.");
        List<Lead> leads = new ArrayList<>();


        Lead lead = al.getRandom(1);

    }

    public void testInsertLeadSeam() {
        System.out.println(TAG + " Running testInsertLeadSeam.");
        List<Lead> leads = new ArrayList<>();
        al.getLeads(leads);
        int initialSize = leads.size();
        Lead newLead = new Lead("Integration", "User", "555-0000", 100.0, "SUV", "New", null, "Test", null);

        String result = al.insertLead(newLead);
        al.getLeads(leads);
        assertNull("Insert should return null on success", result);
        Assert.assertEquals("Database size should increase by 1", initialSize + 1, leads.size());

        System.out.println(TAG+" Finished testInsertLeadSeam.");
    }

    public void testUpdateLeadSeam() {
        System.out.println(TAG + " Running testUpdateLeadSeam.");
        List<Lead> leads = new ArrayList<>();
        al.getLeads(leads);
        Lead target = leads.get(0);
        target.setLeadName("Alice Updated");

        String result = al.updateLead(target);
        assertNull("Update should return null on success", result);

        // Verify the change reached the persistence layer
        Assert.assertEquals("Alice Updated", leads.get(0).getLeadName());

        System.out.println(TAG+" Finished testUpdateLeadSeam.");
    }

    public void testDeleteLeadSeam() {
        System.out.println(TAG + " Running testDeleteLeadSeam.");
        List<Lead> leads = new ArrayList<>();
        al.getLeads(leads);
        int initialSize = leads.size();
        Lead target = leads.get(0);

        String result = al.deleteLead(target);
        al.getLeads(leads);
        assertNull("Delete should return null on success", result);
        Assert.assertEquals("Database size should decrease", initialSize - 1, leads.size());

        System.out.println(TAG+" Finished testDeleteLeadSeam.");
    }
}
