package com.areonedev.autotrack.persistence;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.objects.Lead;

import java.util.ArrayList;
import java.util.List;

public class DataAccessStub implements DataAccess{
    private String dbName;
    private String dbType = "stub";

    public DataAccessStub(String dbName)
    {
        this.dbName = dbName;
    }

    public DataAccessStub()
    {
        this(Main.dbName);
    }

    public void open(String dbPath){

    }

    @Override
    public void close() {

    }

    @Override
    public String getLeadSequential(List<Lead> leadResult) {
        return "";
    }

    @Override
    public ArrayList<Lead> getLeadRandom(Lead lead) {
        return null;
    }

    @Override
    public String insertLead(Lead lead) {
        return "";
    }

    @Override
    public String updateLead(Lead lead) {
        return "";
    }

    @Override
    public String deleteLead(Lead lead) {
        return "";
    }
}
