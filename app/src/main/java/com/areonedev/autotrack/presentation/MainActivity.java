package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.application.Main;

public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "LEADS";
    //public static String[]args = {DB_PATH,DB_NAME};
    private static final String TAG = "Main_Activity";
    private static final int SPLASH_TIME_OUT = 2500; // 2.5 seconds

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

        // 2. Simple Animation (Optional: Add a TextView with ID 'logo_text' in your XML)
        TextView logoText = findViewById(R.id.logo_text); // Ensure this ID exists in activity_main.xml
        if (logoText != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(1500);
            logoText.startAnimation(fadeIn);
        }

        // 3. Transition to Leads Page after 5 seconds
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try {
                Log.d(TAG, "Transitioning to LeadsActivity...");
                android.content.Intent intent = new android.content.Intent(MainActivity.this, LeadsActivity.class);
                startActivity(intent);

                // finish() ensures the user cannot go "back" to the splash screen
                finish();

                // Optional: Add a smooth transition animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } catch (Exception e) {
                Log.e(TAG, "Transition Error: " + e.getMessage());
            }
        }, 3000); // 3000 milliseconds = 3 seconds

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the app is closed
        //Main.shutDown();
    }
}