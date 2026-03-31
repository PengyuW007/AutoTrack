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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LeadDetailsActivity extends AppCompatActivity {

    // Layout Containers
    private LinearLayout layoutViewMode, layoutEditMode;

    // View Mode Components
    private TextView tvViewName, tvViewPhone, tvViewEmail, tvViewAddress, tvViewVehicle, tvViewNotes, tvDetDate, tvDetUpdatedDate;

    // Edit Mode Components
    private EditText etFirstName, etLastName, etPhone, etEmail, etMake, etModel, etYear, etTrim, etNotes;
    private Button btnUpdate, btnDelete;
    private ScoringService scoringService;
    private Lead currentLead;
    private AccessLeads accessLeads;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_details);

        scoringService = new ScoringService();

        accessLeads = new AccessLeads();
        currentLead = (Lead) getIntent().getSerializableExtra("SELECTED_LEAD");

        setupToolbar();
        initViews();

        if (currentLead != null) {
            refreshUI();
        }else {
            Toast.makeText(this, "Error: Lead data not found", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no data
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
        tvViewAddress = findViewById(R.id.tvViewAddress);
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
        if (currentLead == null) return;

        // Get the scientific mission
        String mission = scoringService.getScientificMission(currentLead);
        // Display Mission + Notes
        String notesDisplay = "🎯 MISSION: " + mission + "\n\n" + "--- BOARD OF NOTES ---\n" + (currentLead.getLeadNotes() != null ? currentLead.getLeadNotes() : "No notes.");
        tvViewNotes.setText(notesDisplay);

        // --- PART 1: CONTACT INFO ---
        tvViewName.setText(currentLead.getLeadFirstName() + " " + currentLead.getLeadLastName());
        tvViewPhone.setText(currentLead.getLeadPhoneNumber());
        tvViewEmail.setText(currentLead.getLeadEmail());
        tvViewAddress.setText(currentLead.getLeadAddress()+"\n"+currentLead.getLeadCity()+", "+currentLead.getLeadProvince()+", "+currentLead.getLeadPostalCode());
        tvViewNotes.setText(currentLead.getLeadNotes());

        // --- PART 2: VEHICLE INTEREST ---
        Vehicle v = currentLead.getLeadVehicleInterest();
        if (v != null) {
            tvViewVehicle.setText(String.format("%s %s %s %s", v.getYear(), v.getMake(), v.getModel(), v.getTrim()));
        }else{
            tvViewVehicle.setText("Vehicle Interest: No specific model selected");
        }

        // --- PART 3: NOTES BOARD ---
        if (currentLead.getLeadNotes() != null && !currentLead.getLeadNotes().isEmpty()) {
            tvViewNotes.setText(currentLead.getLeadNotes());
        } else {
            tvViewNotes.setText("No notes available for this lead.");
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
        // Ensure we have a valid lead to delete
        if (currentLead == null) {
            Toast.makeText(this, "Error: No lead selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Lead")
                .setMessage("Are you sure you want to delete " + currentLead.getLeadFirstName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Call the business logic layer to delete
                    String result = accessLeads.deleteLead(currentLead);

                    if (result == null) {
                        // Success!
                        Toast.makeText(this, "Lead deleted successfully", Toast.LENGTH_SHORT).show();

                        // Set a result code so the previous activity (Calendar/AllLeads) knows to refresh
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        // Failure: Show the specific error message from the database/business layer
                        Toast.makeText(this, "Delete failed: " + result, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}