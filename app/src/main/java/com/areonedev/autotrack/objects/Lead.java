package com.areonedev.autotrack.objects;

import java.util.Date;
import java.util.Objects;

public class Lead {

    private static long id=1;
    private String name;
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
    public Lead(){
        id++;
        this.name = null;
        this.phone = null;
        this.budget = 0;
        this.vehicleInterest = null;
        this.stage = null;
        this.followUpDate = null;
        this.notes = null;
        this.createdAt = null;
        this.score = 0.0; // default score
    }

    public Lead(
                String name,
                String phone,
                double budget,
                String vehicleInterest,
                String stage,
                Date followUpDate,
                String notes,
                Date createdAt) {

        id++;
        this.name = name;
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
        return id;
    }

    public String getLeadName() {
        return name;
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

    public void setLeadName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void setVehicleInterest(String vehicleInterest) {
        this.vehicleInterest = vehicleInterest;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // =========================
    // equals & hashCode
    // Important for PriorityQueue remove()
    // =========================

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Lead lead = (Lead) other;

        // Compare by Name (or ID if you prefer)
        if (this.name != null && lead.name != null) {
            return this.name.equals(lead.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =========================
    // toString (for debugging)
    // =========================

    @Override
    public String toString() {
        return "Lead{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", stage='" + stage + '\'' +
                ", score=" + score +
                '}';
    }
}
