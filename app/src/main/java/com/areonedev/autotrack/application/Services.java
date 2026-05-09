package com.areonedev.autotrack.application;

import android.content.Context;

import com.areonedev.autotrack.persistence.DataAccess;
import com.areonedev.autotrack.persistence.DataAccessObject;

public class Services {
    private static DataAccess dataAccessService = null;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    /*** Both Context and DataAccess are static for the entire app ***/
    public static Context getAppContext() {
        return appContext;
    }
    public static DataAccess createDataAccess(String dbPath)
    {
        if (dataAccessService == null)
        {
            dataAccessService = new DataAccessObject(dbPath);
            dataAccessService.open(dbPath);
        }
        return dataAccessService;
    }

    /*** STUB ***/
    public static DataAccess createDataAccess(DataAccess alternateDataAccessService)
    {
        if (dataAccessService == null)
        {
            dataAccessService = alternateDataAccessService;
            dataAccessService.open(Main.getDBPathName());
        }
        return dataAccessService;
    }

    public static DataAccess getDataAccess(String dbName)
    {
        if (dataAccessService == null)
        {
            System.out.println("Connection to data access has not been established.");
            System.exit(1);
        }
        return dataAccessService;
    }

    public static void closeDataAccess()
    {
        if (dataAccessService != null)
        {
            dataAccessService.close();
        }
        dataAccessService = null;
    }
}
