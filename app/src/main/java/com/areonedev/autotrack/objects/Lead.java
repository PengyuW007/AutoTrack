package com.areonedev.autotrack.objects;

import java.util.Date;
import java.util.Objects;

public class Lead {

    private static long leadCounter = 1;
    private long leadID;
    private String firstName;
    private String lastName;
    private String phone;
    private double budget;
    private String vehicleInterest; // Update this to Vehicle Class when needed
    private String stage;
    private Date followUpDate;
    private double score; //Score of weight at different stages
    private String notes;
    private Date createdAt;

    // =========================
    // Constructor
    // =========================
    public Lead() {
        this.leadID = 0; // Assign unique ID and increment counter
        this.firstName = null;
        this.lastName = null;
        this.phone = null;
        this.budget = 0;
        this.vehicleInterest = null;
        this.stage = "New";
        this.followUpDate = null;
        this.notes = null;
        this.createdAt = new Date();
        this.score = 0.0; // default score
    }

    public Lead(
            String firstName,
            String lastName,
            String phone,
            double budget,
            String vehicleInterest,
            String stage,
            Date followUpDate,
            String notes,
            Date createdAt) {

        // 1. Validation: Check if BOTH names are missing
        boolean isFirstEmpty = (firstName == null || firstName.trim().isEmpty());
        boolean isLastEmpty = (lastName == null || lastName.trim().isEmpty());

        if (isFirstEmpty && isLastEmpty) {
            throw new IllegalArgumentException("Lead must have at least a First Name or a Last Name.");
        }

        this.leadID = 0; // Assign unique ID and increment counter

        // 2. Null-Safety: Assign empty strings instead of null
        this.firstName = isFirstEmpty ? "" : firstName.trim();
        this.lastName = isLastEmpty ? "" : lastName.trim();
        this.phone = (phone == null) ? "" : phone;
        this.budget = budget;
        this.vehicleInterest = (vehicleInterest == null) ? "" : vehicleInterest;
        this.stage = (stage == null) ? "New" : stage;
        this.notes = (notes == null) ? "" : notes;

        this.followUpDate = followUpDate;
        this.createdAt = (createdAt == null) ? new Date() : createdAt;
        this.score = 0.0;
    }

    // =========================
    // Getters & Setters
    // =========================

    public static long getLeadCounter() {
        return leadCounter;
    }

    public long getLeadID() {
        return leadID;
    }

    public String getLeadName() {
        String first = getLeadFirstName();
        String last = getLeadLastName();
        return (first + " " + last).trim();
    }

    public String getLeadFirstName() {
        return firstName == null ? "" : firstName;
    }

    public String getLeadLastName() {
        return lastName == null ? "" : lastName;
    }

    public String getLeadPhoneNumber() {
        return phone == null ? "" : phone;
    }

    public double getLeadBudget() {
        return budget;
    }

    public String getLeadVehicleInterest() {
        return vehicleInterest == null ? "" : vehicleInterest;
    }

    public String getLeadStage() {
        return stage;
    }

    public Date getLeadFollowUpDate() {
        return followUpDate;
    }

    public double getLeadScore() {
        return score;
    }

    public String getLeadNotes() {
        return notes == null ? "" : notes;
    }

    public Date getLeadCreatedAt() {
        return createdAt;
    }

    public void setLeadID(long leadID) {
        this.leadID = leadID;
    }

    public void setLeadFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLeadLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLeadName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.firstName = "null";
            this.lastName = "null";
            return;
        }

        String trimmedName = name.trim();
        int firstSpace = trimmedName.indexOf(" ");

        if (firstSpace != -1) {
            // Split into first and last
            this.firstName = trimmedName.substring(0, firstSpace).trim();
            this.lastName = trimmedName.substring(firstSpace + 1).trim();
        } else {
            // No space found: treat the whole thing as the first name
            this.firstName = trimmedName;
            this.lastName = "";
        }
    }

    public void setLeadPhoneNumber(String phone) {
        this.phone = phone;
    }

    public void setLeadBudget(double budget) {
        this.budget = budget;
    }

    public void setLeadVehicleInterest(String vehicleInterest) {
        this.vehicleInterest = vehicleInterest;
    }

    public void setLeadCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setLeadStage(String stage) {
        this.stage = stage;
    }

    public void setLeadFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }

    public void setLeadScore(double score) {
        this.score = score;
    }

    public void setLeadNotes(String notes) {
        this.notes = notes;
    }

    // =========================
    // equals & hashCode
    // Important for PriorityQueue remove()
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same memory address
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;

        // Logic: Match by ID
        return leadID == lead.leadID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leadID);
    }

    // =========================
    // toString (for debugging)
    // =========================

    @Override
    public String toString() {
        return "Lead{" +
                "id='" + leadID + '\'' +
                ", name='" + getLeadName() + '\'' +
                ", stage='" + stage + '\'' +
                ", score=" + score +
                '}';
    }
}
