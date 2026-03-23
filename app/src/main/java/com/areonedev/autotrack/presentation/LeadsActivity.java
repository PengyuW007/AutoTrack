package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.R;
import com.areonedev.autotrack.objects.Lead;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeadsActivity extends AppCompatActivity {
    private static final String TAG = "LeadsActivity";
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab;
    private SearchView searchView;

    private AccessLeads accessLeads;
    private List<Lead> leadList;
    private LeadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_leads); // You will create this XML next

        // 1. Initialize UI Components
        intiViews();

        // 2. Initialize Business Logic (Uses DB opened in MainActivity)
        accessLeads = new AccessLeads();
        leadList = new ArrayList<>();

        // 3. Load and Display Data
        loadLeadsFromDB();

        // 4. Setup Listeners (Search, Navigation, FAB)
        setupListeners();

        // Handle FAB Click (Add Lead)
        fab.setOnClickListener(v -> {
            // TODO: Open AddLeadActivity
        });
    }

    private void intiViews(){
        recyclerView = findViewById(R.id.recyclerViewLeads);
        bottomNav = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab_add_lead);
        searchView = findViewById(R.id.searchView);

        // Configure RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNav.setSelectedItemId(R.id.nav_leads);
    }

    private void loadLeadsFromDB() {
        leadList.clear();
        // Fetch leads using the business logic layer
        String error = accessLeads.getLeads(leadList);

        if (error == null) {
            // Sort by CreatedAt date (Newest first) for categorization
            Collections.sort(leadList, (l1, l2) -> l2.getLeadCreatedAt().compareTo(l1.getLeadCreatedAt()));

            // Initialize and set the adapter
            adapter = new LeadAdapter(leadList);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Failed to load leads: " + error);
            Toast.makeText(this, "Error loading leads", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // Search Logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadLeadsFromDB(); // Reset list if search is cleared
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_leads) {
                return true; // Already here
            } else if (id == R.id.nav_calendar) {
                // TODO: Start CalendarActivity
                Toast.makeText(this, "Calendar coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_notifications) {
                // TODO: Start NotificationsActivity
                Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // FAB Logic
        fab.setOnClickListener(v -> {
            // TODO: Intent intent = new Intent(this, AddLeadActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "Add Lead clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void performSearch(String query) {
        // Use the search logic you already tested in your Business Layer
        Lead foundLead = accessLeads.getLeadByName_Phone(query, query);

        if (foundLead != null) {
            List<Lead> searchResults = new ArrayList<>();
            searchResults.add(foundLead);
            adapter.updateList(searchResults);
        } else {
            // If no exact match, we could implement a partial search later
            // For now, we show an empty list or a toast
            adapter.updateList(new ArrayList<>());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data whenever we return to this screen (e.g., after adding a lead)
        loadLeadsFromDB();
    }
}
