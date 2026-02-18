package com.areonedev.autotrack.application;

import android.util.Log;

import com.areonedev.autotrack.business.PriorityManager;
import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.presentation.CLI;

import java.util.Date;

public class Main {

    public static final String dbName = "LEADS";
    private static String dbPathName = "database/LEADS";

    public static void main(String[]args) {
        startUp();
        CLI.run();

        shutDown();
        System.out.println("All done");
    }

    public static void startUp()
    {
//        ScoringService scoringService = new ScoringService();
//        PriorityManager priorityManager = new PriorityManager(scoringService);
//
//        Lead lead1 = new Lead(1, "Alice", "123",
//                30000, "BMW X3", "NEW",
//                new Date(), "", new Date());
//
//        Lead lead2 = new Lead(2, "Bob", "456",
//                50000, "Audi A4", "NEGOTIATION",
//                new Date(), "", new Date());
//
//        priorityManager.addOrUpdateLead(lead1);
//        priorityManager.addOrUpdateLead(lead2);
//
//        Lead top = priorityManager.peekTopLead();
//        System.out.println(top.toString());
        Services.createDataAccess(dbName);
    }

    public static void shutDown()
    {
        Services.closeDataAccess();
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
