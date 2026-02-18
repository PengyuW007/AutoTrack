package com.areonedev.autotrack.persistance;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.objects.Lead;
public class DataAccessObject implements DataAccess{
    private Statement st1, st2, st3;
    private Connection c1;
    private ResultSet rs2, rs3, rs4, rs5;

    private String dbName;
    private String dbType;

    private ArrayList<Lead> leads;

    private String cmdString;
    private int updateCount;
    private String result;
    private static String EOF = "  ";
    public DataAccessObject(String dbName)
    {
        this.dbName = dbName;
    }
    @Override
    public void open(String string) {

    }

    @Override
    public void close() {

    }

    //This function adds all the leads from the DB to the leads list, original leadResult is empty
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
