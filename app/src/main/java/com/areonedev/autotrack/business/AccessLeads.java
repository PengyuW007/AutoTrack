package com.areonedev.autotrack.business;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.persistence.DataAccess;

public class AccessLeads {
    private DataAccess dataAccess;
    private List<Lead> leads;
    private Lead lead;
    private int currLead;

    public AccessLeads() {
        dataAccess = Services.getDataAccess(Main.dbName);
        leads = new ArrayList<>();
        lead = null;
        currLead = 0;
    }

    public String getLeads(List<Lead> leads) {
        leads.clear();
        return dataAccess.getLeadSequential(leads);
    }

    public Lead getSequential() {
        String result = null;

        if (leads == null || leads.isEmpty()) {
            // the following line was added as a result of a failing test in AccessCoursesTest!
            leads = new ArrayList<>();
            dataAccess.getLeadSequential(leads);
            currLead = 0; // Reset counter for a fresh list
        }
        if (currLead < leads.size()) {
            lead = leads.get(currLead);
            currLead++;
        } else {
            //now hit the end of the list, so reset the counter
            lead = null;
            leads = null;
        }
        return lead;
    }

    public Lead getRandom(long id){
        if (id == 0) {
            lead = null;
        }

        Lead temp = new Lead();
        leads = dataAccess.getLeadRandom(temp);

        currLead = 0;
        if(currLead<leads.size()){
            lead = leads.get(currLead);
            currLead++;
        }else{
            lead = null;
            leads = null;
            currLead =0;
        }
        return lead;
    }

    public String insertLead(Lead currLead) {
        return dataAccess.insertLead(currLead);
    }

    public String updateLead(Lead currLead) {
        return dataAccess.updateLead(currLead);
    }

    public String deleteLead(Lead currLead) {
        return dataAccess.deleteLead(currLead);
    }

    private Lead getLeadByName_Phone(String name,String phone) {
        Lead found = null;

        // 1. Basic validation to ensure we aren't searching with empty data
        if (name != null && !name.trim().isEmpty() && phone != null && !phone.trim().isEmpty()) {

            // 2. Create a "criteria" lead object to act as a search template
            Lead criteria = new Lead();
            criteria.setLeadName(name);
            criteria.setLeadPhoneNumber(phone); // Ensure your Lead object has a setLeadPhone method

            // 3. Ask the persistence layer for leads matching this criteria
            List<Lead> results = dataAccess.getLeadRandom(criteria);

            // 4. If the list is not empty, we found our match
            if (results != null && !results.isEmpty()) {
                found = results.get(0);
            }
        }

        return found;
    }
}
