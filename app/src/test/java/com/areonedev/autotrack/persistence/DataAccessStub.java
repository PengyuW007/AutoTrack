package com.areonedev.autotrack.persistence;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.persistence.DataAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

public class DataAccessStub implements DataAccess {
    private String dbName;
    private String dbType = "stub";

    private ArrayList<Lead> leads;

    public DataAccessStub(String dbName) {
        this.dbName = dbName;
    }

    public DataAccessStub() {
        this(Main.dbName);
    }

    public void open(String dbPath) {
        Lead lead;
        Date today, createdDate;
        leads = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.MARCH, 8);
        today = calendar.getTime();

        calendar.set(2023, Calendar.JANUARY, 1);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Alice Chen",
                "204-555-8123",
                32000,
                "Volkswagen Jetta",
                "NEW",
                today,          // follow-up today
                "First inquiry from website",
                createdDate);   // created
        leads.add(lead);

        calendar.set(2024, Calendar.FEBRUARY, 2);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Brian Miller",
                "204-555-1290",
                45000,
                "Volkswagen Tiguan",
                "VISITED",
                today,          // follow-up today
                "Visited showroom, interested in financing",
                createdDate);   // created Christmas;
        leads.add(lead);

        calendar.set(2025, Calendar.MARCH, 3);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Sophia Martinez",
                "204-555-6677",
                52000,
                "Volkswagen Atlas",
                "NEGOTIATION",
                today,
                "Negotiating trade-in value",
                createdDate);   // created Christmas;
        leads.add(lead);
    }

    @Override
    public void close() {
        System.out.println("Closed " +dbType +" database " +dbName);
    }

    @Override
    public String getLeadSequential(List<Lead> leadResult) {
        leadResult.clear();
        leadResult.addAll(leads);
        return null;
    }

    @Override
    public ArrayList<Lead> getLeadRandom(Lead currLead) {
        ArrayList<Lead> newLeads;
        int index;

        newLeads = new ArrayList<>();
        index = leads.indexOf(currLead);

        if (index >= 0) {
            newLeads.add(leads.get(index));
        }

        return newLeads;
    }

    @Override
    public String insertLead(Lead currLead) {
        leads.add(currLead);
        return null;
    }

    @Override
    public String updateLead(Lead currLead) {
        int index;

        index = leads.indexOf(currLead);
        if (index >= 0) {
            leads.set(index, currLead);
        }
        return null;
    }

    @Override
    public String deleteLead(Lead currLead) {
        int index;

        index = leads.indexOf(currLead);
        if (index >= 0) {
            leads.remove(index);
        }
        return null;
    }
}
