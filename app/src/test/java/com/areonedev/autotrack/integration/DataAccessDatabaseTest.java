package com.areonedev.autotrack.integration;

import android.util.Log;

import junit.framework.TestCase;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.persistence.DataAccessStub;
import com.areonedev.autotrack.persistence.DataAccessTest;

public class DataAccessDatabaseTest extends TestCase{
    private static String dbName = Main.dbName;
    private static final String TAG = "DataAccessDatabaseTest, ";
    public DataAccessDatabaseTest(String arg0){
        super(arg0);
    }

    public void testDataAccess(){
        DataAccess dataAccess;

        Services.closeDataAccess();

        System.out.println("\nStarting Integration test DataAccess (using STUB DB)");

        // Use the following two statements to run with the real database
//        Services.createDataAccess(dbName);
//        dataAccess = Services.getDataAccess(dbName);
        dataAccess = new DataAccessStub(dbName);
        Services.createDataAccess(dataAccess);
        dataAccess = Services.getDataAccess(dbName);
        Log.d(TAG, "App initialized and DB connected.");


        DataAccessTest.dataAccessTest(dataAccess);

        Services.closeDataAccess();

        System.out.println("Finished Integration test DataAccess (using default DB)");
    }
}
