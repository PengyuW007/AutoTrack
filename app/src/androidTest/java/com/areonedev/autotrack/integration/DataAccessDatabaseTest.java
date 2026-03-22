package com.areonedev.autotrack.integration;

import android.util.Log;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.persistence.DataAccessObject; // Use the REAL one
import com.areonedev.autotrack.persistence.DataAccessTest;

@RunWith(AndroidJUnit4.class)
public class DataAccessDatabaseTest {
    private static String dbName = Main.dbName;
    private static final String TAG = "DataAccessDatabaseTest";

    @Before
    public void setUp() {
        // Ensure we start fresh for every test
        Services.closeDataAccess();
    }

    @Test
    public void testDataAccess() {
        Log.d(TAG, "Starting Integration test DataAccess (using REAL DB)");

        // 1. Get the correct Android internal path
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String dbPath = context.getDatabasePath(dbName).getAbsolutePath();

        // 2. Initialize the REAL DataAccessObject
//        DataAccess dataAccess = new DataAccessObject(dbName);
//        dataAccess.open(dbPath);
//
//        // 3. Inject into Services so the rest of the app can use it
//        Services.createDataAccess(dataAccess);
//        dataAccess = Services.getDataAccess(dbName);

        Services.createDataAccess(dbName);
        DataAccess dataAccess = Services.getDataAccess(dbName);
        if(dataAccess!=null){
            dataAccess.open(dbPath);
        }
        System.out.println(dataAccess);

        assertNotNull("DataAccess should not be null", dataAccess);
        Log.d(TAG, "App initialized and REAL DB connected at: " + dbPath);

        // 4. Run the shared test suite
        // Note: If your DB is not empty, you might need to clear it first
        // or update DataAccessTest to handle existing data.
        DataAccessTest.dataAccessTest(dataAccess);

        Services.closeDataAccess();
        Log.d(TAG, "Finished Integration test DataAccess");
    }
}