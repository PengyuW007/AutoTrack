package com.areonedev.autotrack.application;

import android.content.Context;
import android.util.Log;

import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.presentation.CLI;

public class Main {

    public static final String dbName = "AutoTrack";
    private static String dbPathName = "AutoTrack.db";

    public static void main(Context context) {
        startUp(context);
        CLI.run();

//        shutDown();
//        System.out.println("All done");
    }

    public static void startUp(Context context)
    {
// Use the Android context to get the correct path
        String realPath = context.getDatabasePath(dbPathName).getPath();
        setDBPathName(realPath);

        // Initialize the DataAccessObject via your Services class
        Services.initialize(context);// Pass the Android context to get Vehicle CSV import working
        Services.createDataAccess(dbName);

        // Open the database using the real Android path
        DataAccess dao = Services.getDataAccess(dbName);
        if (dao != null) {
            dao.open(realPath);
            Log.d("Main", "Database opened at: " + realPath);
        }
    }

    public static void shutDown()
    {
        Services.closeDataAccess();
        Log.d("Main", "Database shut down successfully.");
    }

    public static String getDBPathName() {
        if (dbPathName == null)
            return dbName;
        else
            return dbPathName;
    }

    public static void setDBPathName(String pathName) {
        System.out.println("Setting DB path to: " + pathName);
        dbPathName = pathName;
    }
}
