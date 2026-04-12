package com.areonedev.autotrack.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        if (id <= 0) {
            lead = null;
        }

        Lead temp = new Lead();
        temp.setLeadID(id);
        leads = dataAccess.getLeadRandom(temp);

        currLead = 0;
        if(currLead<leads.size()){
            lead = leads.get(currLead);
            currLead++;
        }else{
            lead = null;
            leads = null;
        }
        return lead;
    }

    public Lead getLeadByName_Phone(String name,String phone) {
        dataAccess.getLeadSequential(leads);

        // 1. Basic validation to ensure we aren't searching with empty data
        if (name != null && !name.trim().isEmpty() && phone != null && !phone.trim().isEmpty()) {

            // 2. Create a "criteria" lead object to act as a search template
            for(int i = 0;i<leads.size();i++){
                lead =leads.get(i);
                if(lead.getLeadName().equals(name) && lead.getLeadPhoneNumber().equals(phone)) {
                    return lead;
                }
            }
        }
        return null;
    }

    public Lead getLeadByPhone(String phone) {
        List<Lead> allLeads = new ArrayList<>();
        dataAccess.getLeadSequential(allLeads);

        if (phone != null && !phone.trim().isEmpty()) {
            for (Lead lead : allLeads) {
                // Compare the incoming phone with the database phone
                if (lead.getLeadPhoneNumber() != null && lead.getLeadPhoneNumber().equals(phone)) {
                    return lead;
                }
            }
        }
        return null;
    }

    public List<Lead> getAllLeads() {
        List<Lead> allLeads = new ArrayList<>();
        dataAccess.getLeadSequential(allLeads);
        return allLeads;
    }

    public List<Lead> getLeadsByDate(Date date) {
        List<Lead> allLeads = new ArrayList<>();
        List<Lead> filteredLeads = new ArrayList<>();

        // Fetch all leads from the database
        dataAccess.getLeadSequential(allLeads);

        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String targetDate = sdf.format(date);

            for (Lead lead : allLeads) {
                // A "Task" is just a Lead with a follow-up date matching the selected day
                if (lead.getLeadFollowUpDate() != null) {
                    String leadDate = sdf.format(lead.getLeadFollowUpDate());
                    if (targetDate.equals(leadDate)) {
                        filteredLeads.add(lead);
                    }
                }
            }
        }
        return filteredLeads;
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
}
