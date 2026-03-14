package com.areonedev.autotrack.business;

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
        currLead = 0;

        if (currLead < leads.size()) {
            lead = leads.get(currLead);
            currLead++;
        } else {
            lead = null;
            leads = null;
        }
        return lead;
    }

    public Lead getRandom(String name){

        if(name.trim().equals("")){
            lead = null;
        }else{
            lead = new Lead();
            lead.setLeadName(name);
            leads = dataAccess.getLeadRandom(lead);
            if(leads.size()==1){
                lead = leads.get(0);
            }else{
                lead = null;
            }
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
}
