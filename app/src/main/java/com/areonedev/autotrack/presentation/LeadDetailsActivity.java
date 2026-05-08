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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.business.AccessTasks;
import com.areonedev.autotrack.business.ScoringService;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;
import com.areonedev.autotrack.objects.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class LeadDetailsActivity extends AppCompatActivity {

    // Layout Containers
    private LinearLayout layoutViewMode, layoutEditMode;
    private RecyclerView rvTimeline;

    // View Mode Components
    private TextView tvViewName, tvViewPhone, tvViewEmail, tvViewAddress, tvViewVehicle, tvViewNotes, tvDetDate, tvDetUpdatedDate;
    private ImageView ivPhone, ivEmail, ivSms;
    private ImageButton btnAddTask;

    // Edit Mode Components
    private EditText etFirstName, etLastName, etPhone, etEmail, etMake, etModel, etYear, etTrim, etNotes;
    private Button btnUpdate, btnDelete;
    private ScoringService scoringService;
    private Lead currentLead;
    private AccessLeads accessLeads;
    private AccessTasks accessTasks;
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

        accessTasks = new AccessTasks();

        setupToolbar();
        initViews();
        setupContactActions();
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
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
        btnAddTask = findViewById(R.id.btnAddTask);

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
        //String mission = scoringService.getScientificMission(currentLead, new Date());
        //String displayMission = (mission != null) ? mission : "No urgent task today.";

        // Combine Mission and Notes into one display for the "Board of Notes"
//        String notesDisplay = "🎯 MISSION: " + displayMission + "\n\n" + "--- BOARD OF NOTES ---\n" + (currentLead.getLeadNotes() != null ? currentLead.getLeadNotes() : "No notes.");
        //tvViewNotes.setText();

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
            // Handle clicks on the timeline tasks
            adapter.setOnTaskStatusChangedListener(task -> {
                // 1. Update the task in the database (IsCompleted 0 -> 1)
                accessTasks.updateTask(task);

                // 2. Refresh the whole UI
                // This updates the Engagement Score and the Mission Board
                // because a completed task changes the lead's status.
                refreshUI();
            });

            adapter.setOnTaskClickListener(task -> {
                showTaskOptionsDialog(task);
            });

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

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Schedule New Task");

        // These keywords are recognized by your ScoringService for the +25 bonus
        String[] taskTypes = {"Appointment", "Test Drive", "Follow-up", "Other"};

        builder.setItems(taskTypes, (dialog, which) -> {
            String selectedType = taskTypes[which];
            promptForTaskDetails(selectedType);
        });
        builder.show();
    }

    private void promptForTaskDetails(String type) {
        // 1. Initialize a Calendar to store the user's selection
        final Calendar calendar = java.util.Calendar.getInstance();

        // 2. First, pick the Date
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

            // 3. Second, pick the Time
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // 4. Finally, ask for optional details
                showOptionalDetailsDialog(type, calendar.getTime());

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showOptionalDetailsDialog(String type, Date selectedDate) {
        EditText etInput = new EditText(this);
        etInput.setHint("Details (Optional)");

        // Format the date for the dialog title so the user sees what they picked
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

        new AlertDialog.Builder(this)
                .setTitle("Details for " + type)
                .setMessage("Scheduled for: " + sdf.format(selectedDate))
                .setView(etInput)
                .setPositiveButton("Confirm & Save", (dialog, which) -> {
                    String note = etInput.getText().toString();
                    saveManualTask(type, note, selectedDate);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveManualTask(String type, String note,Date taskDate) {
        if (currentLead == null) return;
        // 1. Create the Task object
        String finalDescription = type + (note.trim().isEmpty() ? "" : ": " + note);
        Task newTask = new Task(currentLead, finalDescription, taskDate);
        String result = accessTasks.insertTask(newTask);

        if (result == null) {
            Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
            // 4. Refresh UI so the new task appears on the timeline immediately
            refreshUI();
        } else {
            Toast.makeText(this, "Error saving task: " + result, Toast.LENGTH_SHORT).show();
        }
    }

    private void showTaskOptionsDialog(Task task) {
        if (task.getEventID() <= 0) {
            String result = accessTasks.insertTask(task);
            if (result != null) {
                Toast.makeText(this, "Failed to access tasks", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String[] options = {"Edit Task", "Delete Task", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Manage Task")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditTaskDialog(task);
                    } else if (which == 1) {
                        showDeleteTaskConfirmation(task);
                    }
                })
                .show();
    }

    private void showEditTaskDialog(Task task) {
        // Updated list to include the 4 main types + Other
        String[] taskTypes = {"Appointment", "Test Drive", "Follow-up", "Other"};

        new AlertDialog.Builder(this)
                .setTitle("Update Task Type")
                .setItems(taskTypes, (dialog, which) -> {
                    String selectedType = taskTypes[which];
                    // Proceed to Date/Time selection specific to Editing
                    promptForEditDetails(task, selectedType);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptForEditDetails(Task task, String newType) {
        final Calendar calendar = Calendar.getInstance();
        // Initialize with the task's CURRENT date/time
        calendar.setTime(task.getDate());

        // 1. Pick the New Date
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // 2. Pick the New Time
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // 3. Final Step: Edit the Note and Save
                showEditFinalConfirmation(task, newType, calendar.getTime());

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showEditFinalConfirmation(Task task, String type, Date newDate) {
        EditText etInput = new EditText(this);

        // Logic to extract the existing note from the "Type: Note" format
        String currentTitle = task.getTitle();
        String existingNote = currentTitle.contains(":") ? currentTitle.split(":", 2)[1].trim() : "";
        etInput.setText(existingNote);
        etInput.setHint("Update details (Optional)");

        // UI Padding for the EditText
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        etInput.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle("Finalize Changes")
                .setView(etInput)
                .setPositiveButton("Save Changes", (dialog, which) -> {
                    String note = etInput.getText().toString();

                    // Reconstruct title to maintain "Type: Note" format for ScoringService
                    String finalTitle = type + (note.trim().isEmpty() ? "" : ": " + note);

                    // Update the object fields
                    task.setTitle(finalTitle);
                    task.setDate(newDate);

                    // Persist changes to SQLite
                    String result = accessTasks.updateTask(task);
                    if (result == null) {
                        Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                        refreshUI(); // Refresh timeline and engagement score
                    } else {
                        Toast.makeText(this, "Update failed: " + result, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteTaskConfirmation(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to remove this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String result = accessTasks.deleteTask(task);
                    if (result == null) {
                        Toast.makeText(this, "Task removed", Toast.LENGTH_SHORT).show();
                        refreshUI();
                    } else {
                        Toast.makeText(this, "Delete failed: " + result, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}