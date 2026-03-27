package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

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
    private View emptyStateView; // Added for "No Results"
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
    }

    private void intiViews(){
        recyclerView = findViewById(R.id.recyclerViewLeads);
        bottomNav = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab_add_lead);
        searchView = findViewById(R.id.searchView);
        emptyStateView = findViewById(R.id.empty_state_view); // Ensure this ID exists in your XML

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
            toggleEmptyState(leadList.isEmpty());
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

        fab.setOnClickListener(v -> {
            // This will open the activity to create a new lead
            Intent intent = new Intent(LeadsActivity.this, LeadsCreationActivity.class);
            startActivity(intent);
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
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (adapter != null) {
                adapter.updateList(new ArrayList<>(leadList));
            }
            toggleEmptyState(leadList.isEmpty());
            return;
        }

        String filterPattern = query.toLowerCase().trim();
        List<Lead> filteredResults = new ArrayList<>();

        // Partial match search across Name, Phone, and Email
        for (Lead lead : leadList) {
            boolean matchesName = lead.getLeadName().toLowerCase().contains(filterPattern);
            boolean matchesPhone = lead.getLeadPhoneNumber().contains(filterPattern);
            boolean matchesEmail = (lead.getLeadEmail() != null &&
                    lead.getLeadEmail().toLowerCase().contains(filterPattern));

            if (matchesName || matchesPhone || matchesEmail) {
                filteredResults.add(lead);
            }
        }

        // Update the RecyclerView Adapter with the filtered results
        if (adapter != null) {
            adapter.updateList(filteredResults);
        }
        toggleEmptyState(filteredResults.isEmpty());
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (emptyStateView != null) {
            emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data whenever we return to this screen (e.g., after adding a lead)
        loadLeadsFromDB();
    }
}
