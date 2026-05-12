package com.areonedev.autotrack.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeadsActivity extends AppCompatActivity {
    private static final String TAG = "LeadsActivity";

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;
    private SearchView searchView;
    private View emptyStateView;
    private FloatingActionButton fab;
    private ImageButton btnFilterToggle;
    private Button btnApply, btnReset;
    private LinearLayout filterPanel;
    private Spinner spinnerStatusFilter, spinnerStageFilter, spinnerDivisionFilter;
    private AutoCompleteTextView actvYear, actvMake, actvModel;
    private AccessLeads accessLeads;
    private LeadAdapter adapter;
    private boolean isFilterPanelExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_leads);

        accessLeads = new AccessLeads();

        initViews();
        setupListeners();

        setupVehicleDropdowns();
        setupDropdownBehaviors();

        applyFilters();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewLeads);
        bottomNav = findViewById(R.id.bottom_navigation);
        fab = findViewById(R.id.fab_add_lead);
        searchView = findViewById(R.id.searchView);
        btnFilterToggle = findViewById(R.id.btnFilterToggle);
        filterPanel = findViewById(R.id.filterPanel);
        spinnerStatusFilter = findViewById(R.id.spinnerStatusFilter);
        spinnerStageFilter = findViewById(R.id.spinnerStageFilter);
        spinnerDivisionFilter = findViewById(R.id.spinnerDivisionFilter);
        actvYear = findViewById(R.id.actvYearFilter);
        actvMake = findViewById(R.id.actvMakeFilter);
        actvModel = findViewById(R.id.actvModelFilter);
        btnApply = findViewById(R.id.btnApplyFilters);
        btnReset = findViewById(R.id.btnResetFilters);
        emptyStateView = findViewById(R.id.empty_state_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNav.setSelectedItemId(R.id.nav_leads);


    }

    private void setupListeners() {
        // 1. Toggle Filter Panel
        btnFilterToggle.setOnClickListener(v -> {
            isFilterPanelExpanded = !isFilterPanelExpanded;
            filterPanel.setVisibility(isFilterPanelExpanded ? View.VISIBLE : View.GONE);
            if (isFilterPanelExpanded) {
                btnFilterToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                btnFilterToggle.setImageResource(R.drawable.ic_filter_funnel); // Switch back to funnel
            }
        });

        // 2. Apply Button (Triggers the database/list update)
        btnApply.setOnClickListener(v -> {
            applyFilters();
            filterPanel.setVisibility(View.GONE);
            isFilterPanelExpanded = false;
            btnFilterToggle.setImageResource(R.drawable.ic_filter_funnel);
        });

        // 3. Reset Button
        btnReset.setOnClickListener(v -> {
            spinnerStatusFilter.setSelection(0);
            spinnerStageFilter.setSelection(0);
            spinnerDivisionFilter.setSelection(0);
            actvYear.setText("Year", false);
            actvMake.setText("Make", false);
            actvModel.setText("Model", false);

            // Reset the dropdown lists to default
            setupVehicleDropdowns();
            searchView.setQuery("", false);
            applyFilters(); // Refresh to show all
        });

        // 4. Search View (Keep real-time for quick name/phone lookup)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });

        // 5. FAB & Navigation
        fab.setOnClickListener(v -> startActivity(new Intent(this, LeadsCreationActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_leads) return true;

            Intent intent;
            if (id == R.id.nav_calendar) intent = new Intent(this, CalendarActivity.class);
            else if (id == R.id.nav_notifications)
                intent = new Intent(this, NotificationsActivity.class);
            else return false;

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        });
    }

    /**
     * Combined Method: Handles Search, Filters, Database Query, and UI Updates
     */
    private void applyFilters() {
        // 1. Get current values from all UI components
        String query = (searchView != null) ? searchView.getQuery().toString().trim() : "";

        // Use a helper to get Spinner text safely
        String status = (spinnerStatusFilter.getSelectedItem() != null) ?
                spinnerStatusFilter.getSelectedItem().toString() : "All Status";
        String stage = (spinnerStageFilter.getSelectedItem() != null) ?
                spinnerStageFilter.getSelectedItem().toString() : "All Stages";
        String division = (spinnerDivisionFilter.getSelectedItem() != null) ?
                spinnerDivisionFilter.getSelectedItem().toString() : "All Divisions";

        // Get specific vehicle selections
        String year = actvYear.getText().toString().trim();
        String make = actvMake.getText().toString().trim();
        String model = actvModel.getText().toString().trim();

        // 2. Fetch filtered results from Business Layer (Option B)
        // Ensure your AccessLeads.getLeadsFiltered signature matches these 7 parameters
        List<Lead> filteredList = accessLeads.getLeadsFiltered(query, status, stage, division, year, make, model);
        if (filteredList == null) filteredList = new ArrayList<>();

        // 3. Update or Initialize Adapter
        if (adapter == null) {
            adapter = new LeadAdapter(new ArrayList<>(filteredList), lead -> {
                Intent intent = new Intent(LeadsActivity.this, LeadDetailsActivity.class);
                intent.putExtra("SELECTED_LEAD", lead);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(filteredList);
        }

        // 4. Toggle Empty State
        boolean isEmpty = filteredList.isEmpty();
        if (emptyStateView != null) {
            emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void setupVehicleDropdowns() {
        // 1. Initial Load: Get all unique years from the DB
        List<String> years = accessLeads.getUniqueVehicleYears();
        years.add(0, "Year"); // Add hint
        updateAdapter(actvYear, years);

        updateAdapter(actvMake, new ArrayList<>(Collections.singletonList("Make")));
        updateAdapter(actvModel, new ArrayList<>(Collections.singletonList("Model")));

        // 2. Year Selection -> Filter Makes
        actvYear.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYear = (String) parent.getItemAtPosition(position);

            // Reset children
            actvMake.setText("Make");
            actvModel.setText("Model");

            if (selectedYear.equals("Year")) {
                updateAdapter(actvMake, new ArrayList<>(Collections.singletonList("Make")));
            } else {
                List<String> makes = accessLeads.getMakesByYear(selectedYear);
                makes.add(0, "Make");
                updateAdapter(actvMake, makes);
            }
        });

        // 3. Make Selection -> Filter Models
        actvMake.setOnItemClickListener((parent, view, position, id) -> {
            String selectedYear = actvYear.getText().toString();
            String selectedMake = (String) parent.getItemAtPosition(position);

            actvModel.setText("Model");

            if (selectedMake.equals("Make")) {
                updateAdapter(actvModel, new ArrayList<>(Collections.singletonList("Model")));
            } else {
                List<String> models = accessLeads.getModelsByYearAndMake(selectedYear, selectedMake);
                models.add(0, "Model");
                updateAdapter(actvModel, models);
            }
        });
    }

    private void setupDropdownBehaviors() {
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus && v instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) v).showDropDown();
            }
        };

        actvYear.setOnFocusChangeListener(focusListener);
        actvMake.setOnFocusChangeListener(focusListener);
        actvModel.setOnFocusChangeListener(focusListener);

        actvYear.setOnClickListener(v -> actvYear.showDropDown());
        actvMake.setOnClickListener(v -> actvMake.showDropDown());
        actvModel.setOnClickListener(v -> actvModel.showDropDown());
    }

    // Helper to refresh adapters (Same as your LeadDetailsActivity)
    private void updateAdapter(AutoCompleteTextView view, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, data);
        view.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters(); // Refresh list when returning to activity
    }
}