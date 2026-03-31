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
import com.areonedev.autotrack.objects.Vehicle;

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
                    "FirstName TEXT, LastName TEXT, PhoneNumber TEXT, Email TEXT, " +
                    "Division TEXT, Address TEXT, City TEXT, Province TEXT, " +
                    "Country TEXT, PostalCode TEXT, Budget REAL, Stage TEXT, " +
                    "Notes TEXT, FollowUpDate TEXT, CreatedAt TEXT, " +
                    // Vehicle Interest Columns
                    "VI_Make TEXT, VI_Model TEXT, VI_Year TEXT, VI_Trim TEXT, " +
                    "VI_Price REAL, VI_Color TEXT, VI_InStock INTEGER, VI_VIN TEXT, VI_Trans TEXT, " +
                    // Trade-In Vehicle Columns
                    "TI_Make TEXT, TI_Model TEXT, TI_Year TEXT, TI_Trim TEXT, " +
                    "TI_Price REAL, TI_Color TEXT, TI_InStock INTEGER, TI_VIN TEXT, TI_Trans TEXT" +
                    ")");

            Log.d(TAG, "Database opened successfully at: " + dbPath);

            /* Insert three leads to the DB */
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LEADS, null);

            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count == 0) {
                Log.d(TAG, "Database is empty. Adding initial dummy leads.");
                addDummyLeads();
            } else {
                Log.d(TAG, "Database already contains " + count + " leads. Skipping dummy data.");
            }
        } catch (Exception e) {
            Log.e(TAG, "CRITICAL: Failed to open database: " + e.getMessage());
            db = null; // Ensure it's explicitly null if it fails
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
        values.put("FirstName", lead.getLeadFirstName());
        values.put("LastName", lead.getLeadLastName());
        values.put("PhoneNumber", lead.getLeadPhoneNumber());
        values.put("Email", lead.getLeadEmail());
        values.put("Division", lead.getLeadDivision());
        values.put("Address", lead.getLeadAddress());
        values.put("City", lead.getLeadCity());
        values.put("Province", lead.getLeadProvince());
        values.put("Country", lead.getLeadCountry());
        values.put("PostalCode", lead.getLeadPostalCode());
        values.put("Budget", lead.getLeadBudget());
        values.put("Stage", lead.getLeadStage());
        values.put("Notes", lead.getLeadNotes());
        values.put("FollowUpDate", lead.getLeadFollowUpDate() != null ? formatter.format(lead.getLeadFollowUpDate()) : "");
        values.put("CreatedAt", formatter.format(lead.getLeadCreatedAt()));

        // Flatten Vehicle Interest
        Vehicle vi = lead.getLeadVehicleInterest();
        if (vi != null) {
            values.put("VI_Make", vi.getMake());
            values.put("VI_Model", vi.getModel());
            values.put("VI_Year", vi.getYear());
            values.put("VI_Trim", vi.getTrim());
            values.put("VI_Price", vi.getPrice());
            values.put("VI_Color", vi.getColor());
            values.put("VI_InStock", vi.isInStock() ? 1 : 0);
            values.put("VI_VIN", vi.getVin());
            values.put("VI_Trans", vi.getTransmission());
        }

        // Flatten Trade-In
        Vehicle ti = lead.getTradeInVehicle();
        if (ti != null) {
            values.put("TI_Make", ti.getMake());
            values.put("TI_Model", ti.getModel());
            values.put("TI_Year", ti.getYear());
            values.put("TI_Trim", ti.getTrim());
            values.put("TI_Price", ti.getPrice());
            values.put("TI_Color", ti.getColor());
            values.put("TI_InStock", ti.isInStock() ? 1 : 0);
            values.put("TI_VIN", ti.getVin());
            values.put("TI_Trans", ti.getTransmission());
        }

        return values;
    }

    public String processSQLError(Exception e) {
        String errorMsg = "*** SQL Error: " + e.getMessage();
        e.printStackTrace();
        return errorMsg;
    }

    private void parseCursor(Cursor cursor, List<Lead> list) throws Exception {
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("LeadID"));

                // 1. Reconstruct Vehicle Interest
                Vehicle vi = null;
                int viMakeIdx = cursor.getColumnIndexOrThrow("VI_Make");
                if (!cursor.isNull(viMakeIdx)) {
                    vi = new Vehicle(
                            cursor.getString(viMakeIdx),
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_Model")),
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_Year")),
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_Trim")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("VI_Price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_Color")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("VI_InStock")) == 1,
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_VIN")),
                            cursor.getString(cursor.getColumnIndexOrThrow("VI_Trans"))
                    );
                }

// 2. Reconstruct Trade-In
                Vehicle ti = null;
                int tiMakeIdx = cursor.getColumnIndexOrThrow("TI_Make");
                if (!cursor.isNull(tiMakeIdx)) {
                    ti = new Vehicle(
                            cursor.getString(tiMakeIdx),
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_Model")),
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_Year")),
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_Trim")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("TI_Price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_Color")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("TI_InStock")) == 1,
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_VIN")),
                            cursor.getString(cursor.getColumnIndexOrThrow("TI_Trans"))
                    );
                }

                // 3. Parse Dates
                Date followUp = null;
                String fDateStr = cursor.getString(cursor.getColumnIndexOrThrow("FollowUpDate"));
                if (fDateStr != null && !fDateStr.isEmpty()) followUp = formatter.parse(fDateStr);

                Date createdAt = formatter.parse(cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt")));

                // 4. Create Lead Object
                Lead lead = new Lead(
                        cursor.getString(cursor.getColumnIndexOrThrow("FirstName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("LastName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Division")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Address")),
                        cursor.getString(cursor.getColumnIndexOrThrow("City")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Province")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Country")),
                        cursor.getString(cursor.getColumnIndexOrThrow("PostalCode")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("Budget")),
                        vi,
                        ti,
                        cursor.getString(cursor.getColumnIndexOrThrow("Stage")),
                        followUp,
                        cursor.getString(cursor.getColumnIndexOrThrow("Notes")),
                        createdAt
                );

                lead.setLeadID(id);
                list.add(lead);
            } while (cursor.moveToNext());
        }
    }

    private void addDummyLeads() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();

        // 1. Create reusable Vehicle objects using the PARTIAL constructor (Make, Model, Year)
        Vehicle atlas = new Vehicle("Volkswagen", "Atlas", "2024", "Execline");
        Vehicle tiguan = new Vehicle("Volkswagen", "Tiguan", "2024", "Highline R-Line");
        Vehicle jetta = new Vehicle("Volkswagen", "Jetta", "2024", "Highline");

        // Trade-in vehicles
        Vehicle oldCivic = new Vehicle("Honda", "Civic", "2018","Highline");
        Vehicle oldRav4 = new Vehicle("Toyota", "RAV4", "2015","Highline");

        /* --- HISTORICAL LEADS --- */

        // Darren (Old lead)
        calendar.set(2023, Calendar.JANUARY, 1);
        insertLead(new Lead("Darren", "Adam", "416-278-6191", "darren@example.com", "New Cars",
                "123 Bay St", "Toronto", "ON", "Canada", "M5H 2N2",
                32000, jetta, oldCivic, "NEW", today, "First inquiry", calendar.getTime()));

        // Darryl (Old lead)
        calendar.set(2024, Calendar.FEBRUARY, 2);
        insertLead(new Lead("Darryl", "Kessel", "647-282-9967", "darryl@example.com", "New Cars",
                "456 Yonge St", "Toronto", "ON", "Canada", "M4Y 1W9",
                45000, tiguan, null, "VISITED", today, "Visited showroom", calendar.getTime()));

        // Jamie (Old lead)
        calendar.set(2025, Calendar.MARCH, 3);
        insertLead(new Lead("Jamie", "Alizadeh", "416-543-8045", "jamie@example.com", "New Cars",
                "789 Queen St", "Toronto", "ON", "Canada", "M6J 1G1",
                52000, atlas, oldRav4, "NEGOTIATION", today, "Negotiating", calendar.getTime()));

        /* --- TODAY LEADS --- */
// Use a fresh date object for "Now" (March 25)
        Date now = new Date();

        insertLead(new Lead("Pengyu", "Wang", "613-802-7195", "pengyu@example.com", "New Cars",
                "101 Elgin St", "Ottawa", "ON", "Canada", "K1P 5K7",
                52000, atlas, null, "NEGOTIATION", today, "Interested in Atlas", now));

        insertLead(new Lead("Irfan", "Nassir", "416-891-9798", "irfan@example.com", "Fleet",
                "202 King St", "Toronto", "ON", "Canada", "M5V 1J2",
                52000, atlas, null, "NEW", today, "Fleet inquiry", now));

        /* --- YESTERDAY LEADS --- */
// Reset calendar to exactly 24 hours ago
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        insertLead(new Lead("Anna", "Ivashchenko", "905-782-9571", "anna@example.com", "Pre-owned",
                "303 Main St", "Mississauga", "ON", "Canada", "L5B 1M2",
                28000, jetta, null, "VISITED", today, "Looking for a commuter", yesterday));

//        // Inside addDummyLeads()
//        today = new Date(); // This is the actual 'today'
//        insertLead(new Lead("Test 2", "Priority", "000-000-0000", "test@test.com", "New Cars",
//                "Address", "City", "ON", "Canada", "M5H 2N2",
//                100000, atlas, null, "NEGOTIATION", today, "I should appear in PE", today));
    }
}