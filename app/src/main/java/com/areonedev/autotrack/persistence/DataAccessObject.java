package com.areonedev.autotrack.persistence;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public void open(String dbPath) {
        String url;
        try
        {
            // Setup for HSQL
            dbType = "HSQL";
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            url = "jdbc:hsqldb:file:" + dbPath; // stored on disk mode
            c1 = DriverManager.getConnection(url, "SA", "");
            st1 = c1.createStatement();
            st2 = c1.createStatement();
            st3 = c1.createStatement();

            /*** Alternate setups for different DB engines, just given as examples. Don't use them. ***/

            /*
             * // Setup for SQLite. Note that this is undocumented and is not guaranteed to work.
             * // See also: https://github.com/SQLDroid/SQLDroid
             * dbType = "SQLite";
             * Class.forName("SQLite.JDBCDriver").newInstance();
             * url = "jdbc:sqlite:" + dbPath;
             * c1 = DriverManager.getConnection(url);
             *
             * ... create statements
             */

            /*** The following two work on desktop builds: ***/

            /*
             * // Setup for Access
             * dbType = "Access";
             * Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
             * url = "jdbc:odbc:SC";
             * c1 = DriverManager.getConnection(url,"userid","userpassword");
             *
             * ... create statements
             */

            /*
             * //Setup for MySQL
             * dbType = "MySQL";
             * Class.forName("com.mysql.jdbc.Driver");
             * url = "jdbc:mysql://localhost/database01";
             * c1 = DriverManager.getConnection(url, "root", "");
             *
             * ... create statements
             */
        }
        catch (Exception e)
        {
            processSQLError(e);
        }
        System.out.println("Opened " +dbType +" database " +dbPath);
    }

    @Override
    public void close() {
        try
        {	// commit all changes to the database
            cmdString = "shutdown compact";
            rs2 = st1.executeQuery(cmdString);
            c1.close();
        }
        catch (Exception e)
        {
            processSQLError(e);
        }
        System.out.println("Closed " +dbType +" database " +dbName);
    }

    //This function adds all the leads from the DB to the leads list, original leadResult list is empty
    @Override
    public String getLeadSequential(List<Lead> leadResult) {
        Lead lead;
        long currID;
        double currBudget;
        Date currFollowUpDate,currCreatedAt;
        String currName = EOF,currPhone= EOF,currVehicleInterest= EOF,currStage= EOF,currNotes= EOF;

        result = null;

        try{
            cmdString="Select * from Leads";
            rs2 = st1.executeQuery(cmdString);
        }catch (Exception e){
            processSQLError(e);
        }

        try{
            while(rs2.next()){
                currID=Long.parseLong(rs2.getString("LeadID"));
                currBudget = Double.parseDouble(rs2.getString("Budget"));
                //currScore = Double.parseDouble(rs2.getString("Score")); this value will be calculated by ScoringService
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                currFollowUpDate = formatter.parse(rs2.getString("Follow_Up_Date"));
                currCreatedAt = formatter.parse(rs2.getString("Created_At_Date"));
                currName = rs2.getString("LeadName");
                currPhone =rs2.getString("LeadPhone");
                currVehicleInterest = rs2.getString("VehicleInterest");
                currStage = rs2.getString("Stage");
                currNotes = rs2.getString("Notes");
                lead = new Lead(currID,currName,currPhone,currBudget,currVehicleInterest,currStage,currFollowUpDate,currNotes,currCreatedAt);
                leadResult.add(lead);
            }
            rs2.close();
        }catch (Exception e){
            result = processSQLError(e);
        }
        return result;
    }

    //This function returns the search value by specific lead, for example search a lead by ID = XXX,
    // then return the whole list if it has this Lead with this ID
    @Override
    public ArrayList<Lead> getLeadRandom(Lead newLead) {
        Lead lead;
        long currID;
        double currBudget;
        Date currFollowUpDate,currCreatedAt;
        String currName = EOF,currPhone= EOF,currVehicleInterest= EOF,currStage= EOF,currNotes= EOF;

        leads = new ArrayList<>();

        try{
            cmdString="Select * from Leads where LeadID=" + newLead.getID();
            rs3 = st1.executeQuery(cmdString);
            // ResultSetMetaData md2 = rs3.getMetaData();
            while (rs3.next()){
                currID=Long.parseLong(rs2.getString("LeadID"));
                currBudget = Double.parseDouble(rs2.getString("Budget"));
                //currScore = Double.parseDouble(rs2.getString("Score")); this value will be calculated by ScoringService
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                currFollowUpDate = formatter.parse(rs2.getString("Follow_Up_Date"));
                currCreatedAt = formatter.parse(rs2.getString("Created_At_Date"));
                currName = rs2.getString("LeadName");
                currPhone =rs2.getString("LeadPhone");
                currVehicleInterest = rs2.getString("VehicleInterest");
                currStage = rs2.getString("Stage");
                currNotes = rs2.getString("Notes");
                lead = new Lead(currID,currName,currPhone,currBudget,currVehicleInterest,currStage,currFollowUpDate,currNotes,currCreatedAt);
                leads.add(lead);
            }
            rs3.close();
        } catch (Exception e) {
            processSQLError(e);
        }
        return leads;
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

    public String processSQLError(Exception e)
    {
        String result = "*** SQL Error: " + e.getMessage();

        // Remember, this will NOT be seen by the user!
        e.printStackTrace();

        return result;
    }
}
