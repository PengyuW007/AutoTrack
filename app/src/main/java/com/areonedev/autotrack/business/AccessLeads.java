package com.areonedev.autotrack.business;

import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.persistance.DataAccess;

public class AccessLeads {
    private DataAccess dataAccess;
    private List<Lead>leads;
    private Lead lead;
    private int currLead;

    public AccessLeads(){
        dataAccess = Services.getDataAccess(Main.dbName);
        leads = new ArrayList<>();
        lead = null;
        currLead = 0;
    }

    public String getLeads(List<Lead>leads){
        leads.clear();
        return dataAccess.get
    }
}
