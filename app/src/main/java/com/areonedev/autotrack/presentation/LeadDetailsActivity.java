package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LeadDetailsActivity extends AppCompatActivity {

    // Layout Containers
    private LinearLayout layoutViewMode, layoutEditMode;

    // View Mode Components
    private TextView tvViewName, tvViewPhone, tvViewEmail, tvViewVehicle, tvViewNotes, tvDetDate, tvDetUpdatedDate;

    // Edit Mode Components
    private EditText etFirstName, etLastName, etPhone, etEmail, etMake, etModel, etYear, etTrim, etNotes;
    private Button btnUpdate, btnDelete;

    private Lead currentLead;
    private AccessLeads accessLeads;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_details);

        accessLeads = new AccessLeads();
        currentLead = (Lead) getIntent().getSerializableExtra("SELECTED_LEAD");

        setupToolbar();
        initViews();

        if (currentLead != null) {
            refreshUI();
        }

        btnUpdate.setOnClickListener(v -> handleUpdate());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupToolbar() {
        // 1. Find the toolbar from your XML
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        // 2. Set it as the SupportActionBar
        setSupportActionBar(toolbar);

        // 3. Now configure the back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lead Details");
        }
    }

    private void initViews() {
        // Containers
        layoutViewMode = findViewById(R.id.layoutViewMode);
        layoutEditMode = findViewById(R.id.layoutEditMode);

        // View Mode TextViews
        tvViewName = findViewById(R.id.tvViewName);
        tvViewPhone = findViewById(R.id.tvViewPhone);
        tvViewEmail = findViewById(R.id.tvViewEmail);
        tvViewVehicle = findViewById(R.id.tvViewVehicle);
        tvViewNotes = findViewById(R.id.tvViewNotes);
        tvDetDate = findViewById(R.id.tvDetDate);
        tvDetUpdatedDate = findViewById(R.id.tvDetUpdatedDate);

        // Edit Mode EditTexts
        etFirstName = findViewById(R.id.etDetFirstName);
        etLastName = findViewById(R.id.etDetLastName);
        etPhone = findViewById(R.id.etDetPhone);
        etEmail = findViewById(R.id.etDetEmail);
        etMake = findViewById(R.id.etDetMake);
        etModel = findViewById(R.id.etDetModel);
        etYear = findViewById(R.id.etDetYear);
        etTrim = findViewById(R.id.etDetTrim);
        etNotes = findViewById(R.id.etDetNotes);

        // Buttons
        btnUpdate = findViewById(R.id.btnUpdateLead);
        btnDelete = findViewById(R.id.btnDeleteLead);
    }

    private void refreshUI() {
        // 1. Populate View Mode (Cards)
        tvViewName.setText(currentLead.getLeadFirstName() + " " + currentLead.getLeadLastName());
        tvViewPhone.setText(currentLead.getLeadPhoneNumber());
        tvViewEmail.setText(currentLead.getLeadEmail());
        tvViewNotes.setText(currentLead.getLeadNotes());

        Vehicle v = currentLead.getLeadVehicleInterest();
        if (v != null) {
            tvViewVehicle.setText(String.format("%s %s %s %s", v.getYear(), v.getMake(), v.getModel(), v.getTrim()));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        tvDetUpdatedDate.setText("Last Updated: " + sdf.format(currentLead.getLeadFollowUpDate()));
        tvDetDate.setText("Created: " + sdf.format(currentLead.getLeadCreatedAt()));

        // 2. Populate Edit Mode (Form)
        etFirstName.setText(currentLead.getLeadFirstName());
        etLastName.setText(currentLead.getLeadLastName());
        etPhone.setText(currentLead.getLeadPhoneNumber());
        etEmail.setText(currentLead.getLeadEmail());
        etNotes.setText(currentLead.getLeadNotes());
        if (v != null) {
            etMake.setText(v.getMake());
            etModel.setText(v.getModel());
            etYear.setText(v.getYear());
            etTrim.setText(v.getTrim());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lead_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            toggleEditMode();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            layoutViewMode.setVisibility(View.GONE);
            layoutEditMode.setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Lead");
        } else {
            layoutViewMode.setVisibility(View.VISIBLE);
            layoutEditMode.setVisibility(View.GONE);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Lead Details");
            refreshUI(); // Reset fields if they cancelled
        }
    }

    private void handleUpdate() {
        // Update the object
        currentLead.setLeadFirstName(etFirstName.getText().toString());
        currentLead.setLeadLastName(etLastName.getText().toString());
        currentLead.setLeadPhoneNumber(etPhone.getText().toString());
        currentLead.setLeadEmail(etEmail.getText().toString());
        currentLead.setLeadNotes(etNotes.getText().toString());
        currentLead.setLeadFollowUpDate(new Date());

        Vehicle updatedVehicle = new Vehicle(
                etYear.getText().toString(),
                etMake.getText().toString(),
                etModel.getText().toString(),
                etTrim.getText().toString()
        );
        currentLead.setLeadVehicleInterest(updatedVehicle);

        String result = accessLeads.updateLead(currentLead);
        if (result == null) {
            Toast.makeText(this, "Lead updated", Toast.LENGTH_SHORT).show();
            toggleEditMode(); // Switch back to view mode
            refreshUI();      // Show new data in cards
        } else {
            Toast.makeText(this, "Error: " + result, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Lead")
                .setMessage("Are you sure you want to delete this lead?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String result = accessLeads.deleteLead(currentLead);
                    if (result == null) {
                        finish(); // Return to list
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}