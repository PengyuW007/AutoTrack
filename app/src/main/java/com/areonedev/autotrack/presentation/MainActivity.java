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
            Log.d(TAG, "App initialized and DB connected.");

        } catch (Exception e) {
            Log.e(TAG, "Initialization Error: " + e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the app is closed
        Main.shutDown();
    }
}