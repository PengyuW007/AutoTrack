package com.areonedev.autotrack.presentation;

import android.util.Log;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;
public class CLI {
    public static BufferedReader console;
    public static String inputLine;
    public static String[] inputTokens;

    public static Lead currentLead;

    public static String leadNumber;

    public static String indent = "  ";

    public static void run() {
        new Thread(() -> {
            try {
                // SIMULATION FOR TESTING:
                // Since you can't easily type into an Android phone,
                // we manually trigger the "get Lead" logic once to prove the DB works.
                Log.d("CLI", "Simulating 'get Lead' command for testing...");
                processGetLead();

                // Standard CLI loop (Works in Emulator via 'adb shell')
                console = new BufferedReader(new InputStreamReader(System.in));
                Log.d("CLI", "CLI Thread started. Waiting for input (use 'adb shell' to interact)...");
                process();
            } catch (Exception e) {
                Log.e("CLI", "Error in CLI: " + e.getMessage());
            }
        }).start();
    }

    public static void process() {
        readLine();
        while ((inputLine != null) && (!inputLine.equalsIgnoreCase("exit"))) {
            inputTokens = inputLine.split("\\s+");
            parse();
            readLine();
        }
    }

    public static void readLine() {
        try {
            inputLine = console.readLine();
        } catch (IOException ioe) {
            Log.e("CLI", "Read error: " + ioe.getMessage());
        }
    }

    public static void parse() {
        if (inputTokens != null && inputTokens.length > 0) {
            if (inputTokens[0].equalsIgnoreCase("get")) {
                processGet();
            } else {
                Log.d("CLI", "Invalid command: " + inputTokens[0]);
            }
        }
    }

    public static void processGet() {
        if (inputTokens.length > 1 && inputTokens[1].equalsIgnoreCase("Lead")) {
            processGetLead();
        } else {
            Log.d("CLI", "Invalid data type. Try 'get Lead'");
        }
    }

    public static void processGetLead() {
        Log.d("CLI", "--- Fetching Leads from Database ---");
        AccessLeads accessLeads = new AccessLeads();
        List<Lead> leads = new ArrayList<>();

        String error = accessLeads.getLeads(leads);

        if (error != null) {
            Log.e("CLI", "Database Error: " + error);
        } else {
            Log.d("CLI", "Found " + leads.size() + " leads in LEADS.db:");
            for (Lead lead : leads) {
                Log.d("CLI", indent + "ID: " + lead.getID() +
                        " | Name: " + lead.getName() +
                        " | Budget: $" + lead.getBudget() +
                        " | Stage: " + lead.getStage());
            }
        }
        Log.d("CLI", "--- End of Lead List ---");
    }

}
