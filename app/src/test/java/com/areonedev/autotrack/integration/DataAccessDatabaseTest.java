package com.areonedev.autotrack.integration;

import android.util.Log;

import junit.framework.TestCase;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.persistence.DataAccessTest;

public class DataAccessDatabaseTest extends TestCase{
    private static String dbName = Main.dbName;
    private static final String TAG = "DataAccessDatabaseTest";
    public DataAccessDatabaseTest(String arg0){
        super(arg0);
    }

    public void testDataAccess(){
        DataAccess dataAccess;

        Services.closeDataAccess();
        Log.d(TAG, "Starting Main.main...");
        Services.createDataAccess(dbName);
        dataAccess = Services.getDataAccess(dbName);
        Log.d(TAG, "App initialized and DB connected.");

        DataAccessTest.dataAccessTest(dataAccess);

        Services.closeDataAccess();
        Log.d(TAG, "Finished Integration test DataAccess (using default DB)");
    }
}
