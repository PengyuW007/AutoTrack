package com.areonedev.autotrack.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.areonedev.autotrack.R;
import com.areonedev.autotrack.business.AccessLeads;
import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Vehicle;

import java.util.Date;

public class LeadsCreationActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etPhone, etEmail, etMake, etModel, etYear,etTrim;
    private Button btnSave;
    private AccessLeads accessLeads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_creation);

        accessLeads = new AccessLeads();
        initViews();

        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etMake = findViewById(R.id.etMake);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        btnSave = findViewById(R.id.btnSaveLead);
    }

    private void validateAndSave() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Basic Validation
        if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. DUPLICATE CHECK (The "Situation" you mentioned)
        // We check if a lead with this Name + Phone already exists
        Lead existingLead = accessLeads.getLeadByName_Phone(first + " " + last, phone);

        if (existingLead != null) {
            // Case 2: Duplicate found
            Toast.makeText(this, "Error: A lead with this phone number already exists!", Toast.LENGTH_LONG).show();
        } else {
            // Case 1: Positive case - Create new lead
            saveNewLead(first, last, phone, email);
        }
    }

    private void saveNewLead(String first, String last, String phone, String email) {
        Date now = new Date();
        Vehicle interest = new Vehicle(
                etMake.getText().toString(),
                etModel.getText().toString(),
                etYear.getText().toString(),
                etTrim.getText().toString()
        );

        // Construct the 17-parameter Lead
        Lead newLead = new Lead(
                first, last, phone, email, "Sales",
                "", "", "", "", "", // Address fields empty for now
                0.0, interest, null, "NEW", now, "Added via App", now
        );

        String result = accessLeads.insertLead(newLead);

        if (result == null) {
            Toast.makeText(this, "Lead created successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Return to LeadsActivity
        } else {
            Toast.makeText(this, "Database Error: " + result, Toast.LENGTH_SHORT).show();
        }
    }
}