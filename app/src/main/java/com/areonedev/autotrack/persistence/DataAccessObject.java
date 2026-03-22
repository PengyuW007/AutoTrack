package com.areonedev.autotrack.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.areonedev.autotrack.objects.Lead;

public class DataAccessObject implements DataAccess {
    private SQLiteDatabase db;
    private String dbName;
    private String dbType;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final String TAG = "DataAccessObject";
    private static final String EOF = "  ";
    private static final String TABLE_LEADS = "Leads";

    public DataAccessObject(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void open(String dbPath) {
        try {
            // 1. Safety Check: Ensure the directory exists
            java.io.File dbFile = new java.io.File(dbPath);
            java.io.File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                dbDir.mkdirs(); // Create the /databases/ folder if it's missing
            }

            // 2. Open or Create the database
            db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            dbType = "SQLite";

            // 3. Create Table (Matches Lead class fields)
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LEADS + " (" +
                    "LeadID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FirstName TEXT, " +
                    "LastName TEXT, " +
                    "PhoneNumber TEXT, " +
                    "Budget REAL, " +
                    "VehicleInterest TEXT, " +
                    "Stage TEXT, " +
                    "FollowUpDate TEXT, " +
                    "Notes TEXT, " +
                    "CreatedAt TEXT)");

            Log.d(TAG, "Database opened successfully at: " + dbPath);
        } catch (Exception e) {
            Log.e(TAG, "CRITICAL: Failed to open database: " + e.getMessage());
            db = null; // Ensure it's explicitly null if it fails
        }

        /* Insert three leads to the DB */
        addDummyLeads();

    }

    @Override
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public String getLeadSequential(List<Lead> leadResult) {
        if (db == null) return "DB Null";

        // Best practice: clear the list so the caller doesn't get duplicate data
        leadResult.clear();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LEADS, null);
            parseCursor(cursor, leadResult);
            cursor.close();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public ArrayList<Lead> getLeadRandom(Lead criteria) {
        ArrayList<Lead> results = new ArrayList<>();
        try {
            Cursor cursor;
            // If ID is 0, we search by Business Keys (Name/Phone) to find the ID
            if (criteria.getLeadID() > 0) {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_LEADS + " WHERE LeadID = ?",
                        new String[]{String.valueOf(criteria.getLeadID())});
            } else {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_LEADS + " WHERE FirstName = ? AND LastName = ? AND PhoneNumber = ?",
                        new String[]{criteria.getLeadFirstName(), criteria.getLeadLastName(), criteria.getLeadPhoneNumber()});
            }

            parseCursor(cursor, results);
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Search error: " + e.getMessage());
        }
        return results;
    }



    @Override
    public String insertLead(Lead lead) {
        try {
            ContentValues values = getLeadContentValues(lead);

            // IMPORTANT: Remove LeadID from values so SQLite generates a new one
            values.remove("LeadID");

            long rowId = db.insert(TABLE_LEADS, null, values);

            if (rowId != -1) {
                // SUCCESS: Now we assign the DB-generated ID back to the Lead object
                lead.setLeadID((int) rowId);
                return null;
            } else {
                return "Insert failed: rowId is -1";
            }
        } catch (Exception e) {
            return "SQL Error: " + e.getMessage();
        }
    }

    @Override
    public String updateLead(Lead lead) {
        try {
            ContentValues values = getLeadContentValues(lead);
            int rows = db.update(TABLE_LEADS, values, "LeadID = ?", new String[]{String.valueOf(lead.getLeadID())});
            return (rows > 0) ? null : "Update failed: Lead not found";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String deleteLead(Lead lead) {
        try {
            int rows = db.delete(TABLE_LEADS, "LeadID = ?", new String[]{String.valueOf(lead.getLeadID())});
            return (rows > 0) ? null : "Delete failed: Lead not found";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Helper to map Lead object to ContentValues for Insert/Update
     */
    private ContentValues getLeadContentValues(Lead lead) {
        ContentValues values = new ContentValues();
        // We include LeadID here for updates, but it's removed during inserts
        values.put("LeadID", lead.getLeadID());
        values.put("FirstName", lead.getLeadFirstName());
        values.put("LastName", lead.getLeadLastName());
        values.put("PhoneNumber", lead.getLeadPhoneNumber());
        values.put("Budget", lead.getLeadBudget());
        values.put("VehicleInterest", lead.getLeadVehicleInterest());
        values.put("Stage", lead.getLeadStage());
        values.put("Notes", lead.getLeadNotes());

        // Format Dates to Strings for SQLite storage
        values.put("FollowUpDate", formatter.format(lead.getLeadFollowUpDate()));
        values.put("CreatedAt", formatter.format(lead.getLeadCreatedAt()));

        return values;
    }

    public String processSQLError(Exception e) {
        String errorMsg = "*** SQL Error: " + e.getMessage();
        e.printStackTrace();
        return errorMsg;
    }

    private void parseCursor(Cursor cursor, List<Lead> list) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (cursor.moveToFirst()) {
            do {
                // Extract data using column names
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("LeadID"));
                String fName = cursor.getString(cursor.getColumnIndexOrThrow("FirstName"));
                String lName = cursor.getString(cursor.getColumnIndexOrThrow("LastName"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber"));
                double budget = cursor.getDouble(cursor.getColumnIndexOrThrow("Budget"));
                String interest = cursor.getString(cursor.getColumnIndexOrThrow("VehicleInterest"));
                String stage = cursor.getString(cursor.getColumnIndexOrThrow("Stage"));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow("Notes"));

                // Parse Dates
                Date followUp = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("FollowUpDate")));
                Date createdAt = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")));

                // Create Lead object using the constructor
                Lead lead = new Lead(fName, lName, phone, budget, interest, stage, followUp, notes, createdAt);
                lead.setLeadID(id); // Set the persistent ID found in the DB

                list.add(lead);
            } while (cursor.moveToNext());
        }
    }

    private void addDummyLeads(){
        Lead lead;
        Date today, createdDate;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.MARCH, 8);
        today = calendar.getTime();

        calendar.set(2023, Calendar.JANUARY, 1);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Alice",                // First Name
                "Chen",                 // Last Name
                "204-555-8123",
                32000,
                "Volkswagen Jetta",
                "NEW",
                today,
                "First inquiry from website",
                createdDate);
        insertLead(lead);

        calendar.set(2024, Calendar.FEBRUARY, 2);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Brian",                // First Name
                "Miller",               // Last Name
                "204-555-1290",
                45000,
                "Volkswagen Tiguan",
                "VISITED",
                today,
                "Visited showroom, interested in financing",
                createdDate);
        insertLead(lead);

        calendar.set(2025, Calendar.MARCH, 3);
        createdDate = calendar.getTime();
        lead = new Lead(
                "Sophia",               // First Name
                "Martinez",             // Last Name
                "204-555-6677",
                52000,
                "Volkswagen Atlas",
                "NEGOTIATION",
                today,
                "Negotiating trade-in value",
                createdDate);
        insertLead(lead);
    }

}