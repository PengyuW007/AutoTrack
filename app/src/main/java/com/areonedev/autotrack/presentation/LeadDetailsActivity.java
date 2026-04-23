package com.areonedev.autotrack.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;
import com.areonedev.autotrack.objects.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LeadDetailsActivity extends AppCompatActivity {

    // Layout Containers
    private LinearLayout layoutViewMode, layoutEditMode;
    private RecyclerView rvTimeline;

    // View Mode Components
    private TextView tvViewName, tvViewPhone, tvViewEmail, tvViewAddress, tvViewVehicle, tvViewNotes, tvDetDate, tvDetUpdatedDate;
    private ImageView ivPhone, ivEmail, ivSms;

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

        if (currentLead == null) {
            long leadId = getIntent().getLongExtra("LEAD_ID", -1);
            if (leadId != -1) {
                // Fetch the lead from your database using the ID
                currentLead = accessLeads.getRandom(leadId);
            }
        }

        setupToolbar();
        initViews();
        setupContactActions();

        if (currentLead != null) {
            refreshUI();
        } else {
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
        //tvViewPhone = findViewById(R.id.tvViewPhone);
        //tvViewEmail = findViewById(R.id.tvViewEmail);
        tvViewAddress = findViewById(R.id.tvViewAddress);
        tvViewVehicle = findViewById(R.id.tvViewVehicle);
        tvViewNotes = findViewById(R.id.tvViewNotes);
        tvDetDate = findViewById(R.id.tvDetDate);
        tvDetUpdatedDate = findViewById(R.id.tvDetUpdatedDate);

        ivPhone = findViewById(R.id.ivPhoneIcon);
        ivEmail = findViewById(R.id.ivEmailIcon);
        ivSms = findViewById(R.id.ivSmsIcon);

        rvTimeline = findViewById(R.id.rvLeadTimeline);

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

    private void setupContactActions() {
        // 1. Phone Popup + Dial
        ivPhone.setOnClickListener(v -> {
            if (currentLead == null || currentLead.getLeadPhoneNumber() == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Call Lead")
                    .setMessage("Call " + currentLead.getLeadFirstName() + " at " + currentLead.getLeadPhoneNumber() + "?")
                    .setPositiveButton("Call", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + currentLead.getLeadPhoneNumber()));
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 2. Email Board
        ivEmail.setOnClickListener(v -> {
            if (currentLead == null || currentLead.getLeadEmail() == null) return;

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + currentLead.getLeadEmail()));
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        // 3. SMS Chat Window
        ivSms.setOnClickListener(v -> {
            if (currentLead == null || currentLead.getLeadPhoneNumber() == null) return;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("sms:" + currentLead.getLeadPhoneNumber()));
            startActivity(intent);
        });
    }

    private void refreshUI() {
        if (currentLead == null) return;

        // 1. Handle the Mission + Notes Board
        String mission = scoringService.getScientificMission(currentLead, new Date());
        String displayMission = (mission != null) ? mission : "No urgent task today.";

        // Combine Mission and Notes into one display for the "Board of Notes"
        String notesDisplay = "🎯 MISSION: " + displayMission + "\n\n" +
                "--- BOARD OF NOTES ---\n" +
                (currentLead.getLeadNotes() != null ? currentLead.getLeadNotes() : "No notes.");
        tvViewNotes.setText(notesDisplay);

        // 2. Setup the Timeline (The Task Ledger)
        // This calls the helper method you already have below
        setupTimeline();

        // 3. Populate Contact Info
        tvViewName.setText(currentLead.getLeadFirstName() + " " + currentLead.getLeadLastName());
        //tvViewPhone.setText(currentLead.getLeadPhoneNumber());
        //tvViewEmail.setText(currentLead.getLeadEmail());

        String address = currentLead.getLeadAddress() + "\n" +
                currentLead.getLeadCity() + ", " +
                currentLead.getLeadProvince() + ", " +
                currentLead.getLeadPostalCode();
        tvViewAddress.setText(address);

        // 4. Populate Vehicle Interest
        Vehicle v = currentLead.getLeadVehicleInterest();
        if (v != null) {
            tvViewVehicle.setText(String.format("%s %s %s %s", v.getYear(), v.getMake(), v.getModel(), v.getTrim()));
        } else {
            tvViewVehicle.setText("No specific model selected");
        }

        // 5. Set Dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        if (currentLead.getLeadFollowUpDate() != null) {
            tvDetUpdatedDate.setText("Last Updated: " + sdf.format(currentLead.getLeadFollowUpDate()));
        }
        if (currentLead.getLeadCreatedAt() != null) {
            tvDetDate.setText("Created: " + sdf.format(currentLead.getLeadCreatedAt()));
        }

        // 6. Populate Edit Mode Fields (Hidden until Edit is clicked)
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

    private void setupTimeline() {
        // 1. Safety Check: Ensure the view exists in the XML
        if (rvTimeline == null) {
            Log.e("LeadDetails", "CRITICAL: rvLeadTimeline not found in XML.");
            return;
        }

        // 2. Safety Check: Ensure we have a lead and a creation date
        if (currentLead == null || currentLead.getLeadCreatedAt() == null) {
            Log.e("LeadDetails", "Lead data or CreatedAt date is null. Cannot generate timeline.");
            return;
        }

        // 3. Set Layout Manager ONLY if it hasn't been set yet
        if (rvTimeline.getLayoutManager() == null) {
            rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        }

        // 4. Generate the 1-year scientific plan
        List<Task> timeline = scoringService.getFullTimeline(currentLead);

        // 5. Bind to Adapter
        if (timeline != null && !timeline.isEmpty()) {
            TimelineAdapter adapter = new TimelineAdapter(timeline);
            rvTimeline.setNestedScrollingEnabled(true);
            rvTimeline.setAdapter(adapter);
        } else {
            Log.w("LeadDetails", "Timeline generated was empty.");
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