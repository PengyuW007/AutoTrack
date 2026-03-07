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
    public static final String DB_PATH = "LEADS";
    public static final String DB_NAME = "LEADS";
    //public static String[]args = {DB_PATH,DB_NAME};
    private static final String TAG = "LEADS_DB_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Main.main(this);

        AccessLeads accessLeads = new AccessLeads();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.DECEMBER, 25); // Dec 25, 2024
        Date christmas = calendar.getTime();
        Date today = new Date();

        int uniqueID = (int) (System.currentTimeMillis() % 100000);
        Lead testLead = new Lead(uniqueID, "Test Lead", "555-0101", 5000.0, "SUV", "New",christmas , "Notes", today);
        //accessLeads.insertLead(testLead);
        Log.d(TAG, "Attempting to insert lead...");
        String insertResult = accessLeads.insertLead(testLead);

        if (insertResult == null) {
            Log.d(TAG, "Insert successful!");
        } else {
            Log.e(TAG, "Insert failed: " + insertResult);
        }

        // 5. TEST: Retrieval (Get all leads)
        Log.d(TAG, "Attempting to retrieve leads...");
        List<Lead> leadList = new ArrayList<>();
        String getResult = accessLeads.getLeads(leadList);

        if (getResult == null) {
            Log.d(TAG, "Retrieval successful! Count: " + leadList.size());
            for (Lead l : leadList) {
                Log.d(TAG, "Found Lead: ID=" + l.getID() + ", Name=" + l.getName() + ", Budget=" + l.getBudget());
            }
        } else {
            Log.e(TAG, "Retrieval failed: " + getResult);
        }
    }
}