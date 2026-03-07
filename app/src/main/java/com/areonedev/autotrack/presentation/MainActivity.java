package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String DB_NAME = "LEADS";
    //public static String[]args = {DB_PATH,DB_NAME};
    private static final String TAG = "LEADS_DB_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // 1. Initialize Database
            Log.d(TAG, "Starting Main.main...");
            Main.main(this);

//            // 2. Setup Test Data
//            AccessLeads accessLeads = new AccessLeads();
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(2024, Calendar.DECEMBER, 25);
//            Date christmas = calendar.getTime();
//            Date today = new Date();
//
//            // Use a unique ID to avoid Primary Key crashes on re-run
//            int uniqueID = (int) (System.currentTimeMillis() % 100000);
//            Lead testLead = new Lead(uniqueID, "Test Lead " + uniqueID, "555-0101", 5000.0, "SUV", "New", christmas, "Notes", today);
//
//            // 3. TEST: Insert
//            Log.d(TAG, "Attempting to insert lead ID: " + uniqueID);
//            String insertResult = accessLeads.insertLead(testLead);
//
//            if (insertResult == null) {
//                Log.d(TAG, "Insert successful!");
//            } else {
//                Log.e(TAG, "Insert failed: " + insertResult);
//            }

            // 4. TEST: Retrieval
            Log.d(TAG, "Attempting to retrieve leads...");
            List<Lead> leadList = new ArrayList<>();
            AccessLeads accessLeads = new AccessLeads();
            String getResult = accessLeads.getLeads(leadList);

            if (getResult == null) {
                Log.d(TAG, "Retrieval successful! Count: " + leadList.size());
                for (Lead l : leadList) {
                    Log.d(TAG, "Found Lead: ID=" + l.getID() + ", Name=" + l.getName());
                }
            } else {
                Log.e(TAG, "Retrieval failed: " + getResult);
            }

        } catch (Exception e) {
            // This will catch the crash and print it to Logcat so you can see WHY it ended
            Log.e(TAG, "CRASH IN ONCREATE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the app is closed
        Main.shutDown();
    }
}