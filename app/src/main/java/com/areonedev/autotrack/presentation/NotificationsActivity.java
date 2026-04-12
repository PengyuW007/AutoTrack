package com.areonedev.autotrack.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.business.AccessNotifications;
import com.areonedev.autotrack.objects.Notification;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private BottomNavigationView bottomNavigationView;
    private AccessNotifications accessNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();

        // Initialize data access
        accessNotifications = new AccessNotifications();
        notificationList = new ArrayList<>();

        loadNotifications();

        setupNavigation();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set LayoutManager for RecyclerView
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);
    }

    private void loadNotifications() {
        // 1. Initialize the list
        notificationList.clear();
        String error = accessNotifications.getNotifications(notificationList);

        if (error == null) {
            // 3. Sort: Newest at the top (Descending by Date)
            Collections.sort(notificationList, (n1, n2) -> n2.getDate().compareTo(n1.getDate()));

            // 4. Set the adapter
            adapter = new NotificationAdapter(notificationList);
            rvNotifications.setAdapter(adapter);
        } else {
            Log.e(TAG, "Failed to load notifications: " + error);
            Toast.makeText(this, "Error loading notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavigation() {
        // 1. Set the correct item as selected in the bottom bar
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);

        // 2. Handle navigation clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_notifications) {
                // Already here, do nothing
                return true;
            } else if (id == R.id.nav_leads) {
                // Navigate to Leads
                Intent intent = new Intent(NotificationsActivity.this, LeadsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Optional: finish to keep backstack clean
                return true;
            } else if (id == R.id.nav_calendar) {
                // Navigate to Calendar/Agenda
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning to this page
        loadNotifications();
    }
}