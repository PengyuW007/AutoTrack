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

        Log.d(TAG, "Starting Main.main...");
        System.out.println(TAG+"Starting Integration test DataAccess (using default DB)");
        /* Use the following two statements to run with the REAL database */
        Services.createDataAccess(dbName);
        dataAccess = Services.getDataAccess(dbName);

        /* Use the following two statements to run with the STUB database */
//        dataAccess = new DataAccessStub(dbName);
//        Services.createDataAccess(dataAccess);
//        dataAccess = Services.getDataAccess(dbName);
//        Log.d(TAG, "App initialized and DB connected.");

        DataAccessTest.dataAccessTest(dataAccess);
        Services.closeDataAccess();
        Log.d(TAG, "Finished Integration test DataAccess (using default DB)");
        System.out.println(TAG+"Finished Integration test DataAccess (using default DB)");
    }
}
