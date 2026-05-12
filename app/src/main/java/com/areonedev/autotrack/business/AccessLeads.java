package com.areonedev.autotrack.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;
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

    public Lead getLeadByContactInfo(String contactInfo) {
        List<Lead> allLeads = new ArrayList<>();
        dataAccess.getLeadSequential(allLeads);

        for (Lead lead : allLeads) {
            // Check Phone
            if (lead.getLeadPhoneNumber() != null && lead.getLeadPhoneNumber().equals(contactInfo)) {
                return lead;
            }
            // Check Email (Case-insensitive)
            if (lead.getLeadEmail() != null && lead.getLeadEmail().equalsIgnoreCase(contactInfo)) {
                return lead;
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

    public List<Lead> getLeadsFiltered(String query, String status, String stage, String division, String year, String make, String model) {
        List<Lead> allLeads = new ArrayList<>();
        dataAccess.getLeadSequential(allLeads); // Fetch all data from persistence

        List<Lead> filtered = new ArrayList<>();

        for (Lead lead : allLeads) {
            if (matchesSearch(lead, query) &&
                    matchesStatus(lead, status) &&
                    matchesStage(lead, stage) &&
                    matchesDivision(lead, division) &&
                    matchesCar(lead, year, make, model)) {

                filtered.add(lead);
            }
        }
        return filtered;
    }

    private boolean matchesSearch(Lead lead, String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String lowerQuery = query.toLowerCase().trim();

        String fullName = (lead.getLeadFirstName() + " " + lead.getLeadLastName()).toLowerCase();
        String phone = lead.getLeadPhoneNumber() != null ? lead.getLeadPhoneNumber() : "";

        return fullName.contains(lowerQuery) || phone.contains(lowerQuery);
    }

    private boolean matchesStatus(Lead lead, String status) {
        if (status == null || status.trim().isEmpty() || status.toLowerCase().contains("all")) return true;

        // Lead status is boolean: true = Active, false = Lost
        boolean isActive = status.equalsIgnoreCase("Active");
        return lead.getLeadStatus() == isActive;
    }

    private boolean matchesStage(Lead lead, String stage) {
        if (stage == null || stage.trim().isEmpty() || stage.toLowerCase().contains("all")) return true;

        String leadStage = lead.getLeadStage();
        return leadStage != null && leadStage.equalsIgnoreCase(stage);
    }

    private boolean matchesDivision(Lead lead, String division) {
        if (division == null || division.trim().isEmpty() || division.toLowerCase().contains("all")) return true;

        String leadDivision = lead.getLeadDivision();
        return leadDivision != null && leadDivision.equalsIgnoreCase(division);
    }

    private boolean matchesCar(Lead lead, String year, String make, String model) {
        boolean isYearFiltered = year != null && !year.equalsIgnoreCase("Year") && !year.trim().isEmpty();
        boolean isMakeFiltered = make != null && !make.equalsIgnoreCase("Make") && !make.trim().isEmpty();
        boolean isModelFiltered = model != null && !model.equalsIgnoreCase("Model") && !model.trim().isEmpty();

        // If no car filters are selected, this lead is a match (don't filter it out)
        if (!isYearFiltered && !isMakeFiltered && !isModelFiltered) return true;

        Vehicle vehicle = lead.getLeadVehicleInterest();
        // If user IS filtering by car but this lead has no vehicle data, it's not a match
        if (vehicle == null) return false;

        // Check each field: Match if filter is "Year/Make/Model" (ignore) OR if it matches the data
        boolean yearMatch = !isYearFiltered || (vehicle.getYear() != null && vehicle.getYear().equalsIgnoreCase(year));
        boolean makeMatch = !isMakeFiltered || (vehicle.getMake() != null && vehicle.getMake().equalsIgnoreCase(make));
        boolean modelMatch = !isModelFiltered || (vehicle.getModel() != null && vehicle.getModel().equalsIgnoreCase(model));

        return yearMatch && makeMatch && modelMatch;
    }

    public List<String> getUniqueVehicleYears() {
        return dataAccess.getUniqueColumnValues("VI_Year");
    }

    public List<String> getMakesByYear(String year) {
        return dataAccess.getFilteredColumnValues("VI_Make", "VI_Year = ?", new String[]{year});
    }

    public List<String> getModelsByYearAndMake(String year, String make) {
        return dataAccess.getFilteredColumnValues("VI_Model", "VI_Year = ? AND VI_Make = ?", new String[]{year, make});
    }
}
