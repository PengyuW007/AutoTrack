package com.areonedev.autotrack.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.areonedev.autotrack.objects.Lead;

public class DataAccessObject implements DataAccess {
    private SQLiteDatabase db;
    private String dbName;
    private String dbType;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private String result;
    private static final String TAG = "DataAccessObject";
    private static final String EOF = "  ";

    public DataAccessObject(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void open(String dbPath) {
        try {
            // dbPath should be: context.getDatabasePath("autotrack.db").getPath()
            db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            dbType = "SQLite";

            // Optional: Ensure the table exists if this is the first run
            db.execSQL("CREATE TABLE IF NOT EXISTS Leads (" +
                    "LeadID INTEGER PRIMARY KEY, " +
                    "LeadFirstName TEXT, " +
                    "LeadLastName TEXT, " +
                    "LeadPhone TEXT, " +
                    "Budget REAL, " +
                    "VehicleInterest TEXT, " +
                    "Stage TEXT, " +
                    "Follow_Up_Date TEXT, " +
                    "Notes TEXT, " +
                    "Created_At_Date TEXT)");
        } catch (Exception e) {
            processSQLError(e);
        }
    }

    @Override
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public String getLeadSequential(List<Lead> leadResult) {
        result = null;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM Leads", null);
            parseCursor(cursor, leadResult);
            cursor.close();
        } catch (Exception e) {
            result = processSQLError(e);
        }
        return result;
    }

    @Override
    public ArrayList<Lead> getLeadRandom(Lead newLead) {
        ArrayList<Lead> leads = new ArrayList<>();
        try {
            // Use selectionArgs (?) to prevent SQL injection
            String[] selectionArgs = { String.valueOf(newLead.getLeadID()) };
            Cursor cursor = db.rawQuery("SELECT * FROM Leads WHERE LeadID = ?", selectionArgs);

            parseCursor(cursor, leads);
            cursor.close();
        } catch (Exception e) {
            processSQLError(e);
        }
        return leads;
    }

    /**
     * Helper method to reduce code duplication when reading from a Cursor
     */
    private void parseCursor(Cursor cursor, List<Lead> list) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("LeadID"));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("LeadFirstName"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("LeadLastName"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("LeadPhone"));
                double budget = cursor.getDouble(cursor.getColumnIndexOrThrow("Budget"));
                String vehicleInterest = cursor.getString(cursor.getColumnIndexOrThrow("VehicleInterest"));
                String stage = cursor.getString(cursor.getColumnIndexOrThrow("Stage"));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow("Notes"));

                Date followUp = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("Follow_Up_Date")));
                Date createdAt = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("Created_At_Date")));

                list.add(new Lead(firstName,lastName, phone, budget, vehicleInterest, stage, followUp, notes, createdAt));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public String insertLead(Lead lead) {
        result = null;
        try {
            ContentValues values = getLeadContentValues(lead);
            long rowId = db.insert("Leads", null, values);
            if (rowId == -1) result = "Error inserting lead";
        } catch (Exception e) {
            result = processSQLError(e);
        }
        return result;
    }

    @Override
    public String updateLead(Lead lead) {
        result = null;
        try {
            ContentValues values = getLeadContentValues(lead);
            String whereClause = "LeadID = ?";
            String[] whereArgs = { String.valueOf(lead.getLeadID()) };

            int rowsAffected = db.update("Leads", values, whereClause, whereArgs);
            if (rowsAffected == 0) result = "Lead not found.";
        } catch (Exception e) {
            result = processSQLError(e);
        }
        return result;
    }

    @Override
    public String deleteLead(Lead lead) {
        result = null;
        try {
            String whereClause = "LeadID = ?";
            String[] whereArgs = { String.valueOf(lead.getLeadID()) };

            int rowsAffected = db.delete("Leads", whereClause, whereArgs);
            if (rowsAffected == 0) result = "Lead not found.";
        } catch (Exception e) {
            result = processSQLError(e);
        }
        return result;
    }

    /**
     * Helper to map Lead object to ContentValues for Insert/Update
     */
    private ContentValues getLeadContentValues(Lead lead) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put("LeadID", lead.getLeadID());
        values.put("LeadName", lead.getLeadName());
        values.put("LeadPhone", lead.getLeadPhoneNumber());
        values.put("Budget", lead.getLeadBudget());
        values.put("VehicleInterest", lead.getLeadVehicleInterest());
        values.put("Stage", lead.getLeadStage());
        values.put("Follow_Up_Date", formatter.format(lead.getLeadFollowUpDate()));
        values.put("Notes", lead.getLeadNotes());
        values.put("Created_At_Date", formatter.format(lead.getLeadCreatedAt()));
        return values;
    }

    public String processSQLError(Exception e) {
        String errorMsg = "*** SQL Error: " + e.getMessage();
        e.printStackTrace();
        return errorMsg;
    }
}