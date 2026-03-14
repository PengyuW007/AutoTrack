package com.areonedev.autotrack.objects;

import java.util.Date;
import java.util.Objects;

public class Lead {

    private static long idCounter = 1;
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
        this.leadID = idCounter++; // Assign unique ID and increment counter
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

        this.leadID = idCounter++; // Assign unique ID
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.budget = budget;
        this.vehicleInterest = vehicleInterest;
        this.stage = stage;
        this.followUpDate = followUpDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.score = 0.0; // default score
    }

    // =========================
    // Getters & Setters
    // =========================

    public long getLeadID() {
        return leadID;
    }

    public String getLeadName() {
        return firstName + " " + lastName;
    }

    public String getLeadFirstName() {
        return firstName;
    }

    public String getLeadLastName() {
        return lastName;
    }

    public String getLeadPhoneNumber() {
        return phone;
    }

    public double getLeadBudget() {
        return budget;
    }

    public String getLeadVehicleInterest() {
        return vehicleInterest;
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
        return notes;
    }

    public Date getLeadCreatedAt() {
        return createdAt;
    }

    public void setLeadFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLeadLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setLeadName(String name) {
        if (name != null && name.contains(" ")) {
            int firstSpace = name.indexOf(" ");
            this.firstName = name.substring(0, firstSpace).trim();
            this.lastName = name.substring(firstSpace + 1).trim();
        } else {
            this.firstName = name;
            this.lastName = ""; // Or handle as you prefer
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

        // If IDs match, they are definitely the same
        if (this.leadID == lead.leadID) return true;

        // Logic: Match by First Name, Last Name, and Phone (ignoring case)
        return safeCompare(this.firstName, lead.firstName) &&
                safeCompare(this.lastName, lead.lastName) &&
                Objects.equals(this.phone, lead.phone);
    }

    private boolean safeCompare(String s1, String s2) {
        String str1 = (s1 == null) ? "" : s1.trim().toLowerCase();
        String str2 = (s2 == null) ? "" : s2.trim().toLowerCase();
        return str1.equals(str2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phone);
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
