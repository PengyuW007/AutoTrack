package com.areonedev.autotrack.objects;

import java.util.Date;
import java.util.Objects;

public class Lead {

    private String id;
    private String name;
    private String phone;
    private double budget;
    private String vehicleInterest; // Update this to Vehicle Class when needed
    private String stage;
    private Date followUpDate;
    private double score;
    private String notes;
    private Date createdAt;

    // =========================
    // Constructor
    // =========================
    public Lead(String id,
                String name,
                String phone,
                double budget,
                String vehicleInterest,
                String stage,
                Date followUpDate,
                String notes,
                Date createdAt) {

        this.id = id;
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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public double getBudget() {
        return budget;
    }

    public String getVehicleInterest() {
        return vehicleInterest;
    }

    public String getStage() {
        return stage;
    }

    public Date getFollowUpDate() {
        return followUpDate;
    }

    public double getScore() {
        return score;
    }

    public String getNotes() {
        return notes;
    }

    public Date getCreatedAt() {
        return createdAt;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lead)) return false;
        Lead lead = (Lead) o;
        return Objects.equals(id, lead.id);
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
