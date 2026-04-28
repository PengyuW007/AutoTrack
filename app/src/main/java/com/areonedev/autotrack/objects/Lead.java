package com.areonedev.autotrack.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.List;

public class Lead implements Serializable {
    // It is good practice to add a serialVersionUID
    private static final long serialVersionUID = 1L;
    private long leadID;
    private String firstName;
    private String lastName;
    private String phone;
    private String leadEmail;
    private String leadDivision;
    private String leadAddress;
    private String leadCity;
    private String leadProvince;
    private String leadCountry;
    private String leadPostalCode;

    private double budget;
    private Vehicle vehicleInterest;
    private Vehicle tradeInVehicle;
    private String stage;
    private Date followUpDate;
    private double score;
    private String notes;
    private Date createdAt;
    private Date lastInteractionDate;
    private String lastInteractionBy; // "LEAD" or "SALES"
    private List<Task> tasks;


    // =========================
    // Constructors
    // =========================

    public Lead() {
        this.leadID = 0;
        this.firstName = "";
        this.lastName = "";
        this.phone = "";
        this.leadEmail = "";
        this.leadDivision = "";
        this.leadAddress = "";
        this.leadCity = "";
        this.leadProvince = "ON";
        this.leadCountry = "Canada";
        this.leadPostalCode = "";
        this.budget = 0;
        this.vehicleInterest = null;
        this.tradeInVehicle = null;
        this.stage = "NEW";
        this.followUpDate = new Date();
        this.notes = "";
        this.createdAt = new Date();
        this.lastInteractionDate = null; // No interaction yet
        this.lastInteractionBy = "";
        this.score = 0.0;
        this.tasks = new ArrayList<>();
    }

    public Lead(
            String firstName,
            String lastName,
            String phone,
            String email,
            String division,
            String address,
            String city,
            String province,
            String country,
            String postalCode,
            double budget,
            Vehicle vehicleInterest,
            Vehicle tradeInVehicle,
            //String stage,
            //Date followUpDate,
            String notes
            //Date createdAt
            ) {

        // Validation: Check if BOTH names are missing
        boolean isFirstEmpty = (firstName == null || firstName.trim().isEmpty());
        boolean isLastEmpty = (lastName == null || lastName.trim().isEmpty());

        if (isFirstEmpty && isLastEmpty) {
            throw new IllegalArgumentException("Lead must have at least a First Name or a Last Name.");
        }

        this.leadID = 0;
        this.firstName = isFirstEmpty ? "" : firstName.trim();
        this.lastName = isLastEmpty ? "" : lastName.trim();
        this.phone = (phone == null) ? "" : phone;
        this.leadEmail = (email == null) ? "" : email;
        this.leadDivision = (division == null) ? "" : division;
        this.leadAddress = (address == null) ? "" : address;
        this.leadCity = (city == null) ? "" : city;
        this.leadProvince = (province == null) ? "ON" : province;
        this.leadCountry = (country == null) ? "Canada" : country;
        this.leadPostalCode = (postalCode == null) ? "" : postalCode;

        this.budget = budget;
        this.vehicleInterest = vehicleInterest;
        this.tradeInVehicle = tradeInVehicle;
        this.stage = (stage == null) ? "NEW" : stage;
        this.notes = (notes == null) ? "" : notes;
        this.followUpDate = (followUpDate == null) ? new Date() : followUpDate;
        this.createdAt = (createdAt == null) ? new Date() : createdAt;
        this.score = 0.0;

        this.tasks = new ArrayList<>();
    }

    // =========================
    // Getters & Setters
    // =========================

    public long getLeadID() { return leadID; }
    public void setLeadID(long leadID) { this.leadID = leadID; }

    public String getLeadFirstName() { return firstName; }
    public void setLeadFirstName(String firstName) { this.firstName = firstName; }

    public String getLeadLastName() { return lastName; }
    public void setLeadLastName(String lastName) { this.lastName = lastName; }

    public String getLeadName() {
        return (firstName + " " + lastName).trim();
    }
    public void setLeadName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            this.firstName = "";
            this.lastName = "";
            return;
        }

        String trimmedName = fullName.trim();
        int firstSpaceIndex = trimmedName.indexOf(" ");

        if (firstSpaceIndex == -1) {
            // Only one name provided (e.g., "Cher")
            this.firstName = trimmedName;
            this.lastName = "";
        } else {
            // Split: First word is firstName, everything else is lastName
            // Example: "John Quincy Adams" -> First: "John", Last: "Quincy Adams"
            this.firstName = trimmedName.substring(0, firstSpaceIndex).trim();
            this.lastName = trimmedName.substring(firstSpaceIndex + 1).trim();
        }
    }
    public String getLeadPhoneNumber() { return phone; }
    public void setLeadPhoneNumber(String phone) { this.phone = phone; }

    public String getLeadEmail() { return leadEmail; }
    public void setLeadEmail(String leadEmail) { this.leadEmail = leadEmail; }

    public String getLeadDivision() { return leadDivision; }
    public void setLeadDivision(String leadDivision) { this.leadDivision = leadDivision; }

    public String getLeadAddress() { return leadAddress; }
    public void setLeadAddress(String leadAddress) { this.leadAddress = leadAddress; }

    public String getLeadCity() { return leadCity; }
    public void setLeadCity(String leadCity) { this.leadCity = leadCity; }

    public String getLeadProvince() { return leadProvince; }
    public void setLeadProvince(String leadProvince) { this.leadProvince = leadProvince; }

    public String getLeadCountry() { return leadCountry; }
    public void setLeadCountry(String leadCountry) { this.leadCountry = leadCountry; }

    public String getLeadPostalCode() { return leadPostalCode; }
    public void setLeadPostalCode(String leadPostalCode) { this.leadPostalCode = leadPostalCode; }

    public double getLeadBudget() { return budget; }
    public void setLeadBudget(double budget) { this.budget = budget; }

    public Vehicle getLeadVehicleInterest() { return vehicleInterest; }
    public void setLeadVehicleInterest(Vehicle vehicleInterest) { this.vehicleInterest = vehicleInterest; }

    public Vehicle getTradeInVehicle() { return tradeInVehicle; }
    public void setTradeInVehicle(Vehicle tradeInVehicle) { this.tradeInVehicle = tradeInVehicle; }

    public String getLeadStage() { return stage; }
    public void setLeadStage(String stage) { this.stage = stage; }

    public Date getLeadFollowUpDate() { return followUpDate; }
    public void setLeadFollowUpDate(Date followUpDate) { this.followUpDate = followUpDate; }

    public double getLeadScore() { return score; }
    public void setLeadScore(double score) { this.score = score; }

    public String getLeadNotes() { return notes; }
    public void setLeadNotes(String notes) { this.notes = notes; }

    public Date getLeadCreatedAt() { return createdAt; }
    public void setLeadCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastInteractionDate() { return lastInteractionDate; }
    public void setLastInteractionDate(Date lastInteractionDate) { this.lastInteractionDate = lastInteractionDate; }

    public String getLastInteractionBy() { return lastInteractionBy; }
    public void setLastInteractionBy(String lastInteractionBy) { this.lastInteractionBy = lastInteractionBy; }

    public void addLeadTask(Task task) {
        tasks.add(task);
    }

    public void removeLeadTask(Task task) {
        if(task!=null){
            tasks.remove(task);
        }
    }

    public List<Task> getLeadTasks() {
        return tasks;
    }

    // =========================
    // Helper Methods
    // =========================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lead lead = (Lead) o;
        return leadID == lead.leadID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leadID);
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + leadID +
                ", name='" + getLeadName() + '\'' +
                ", interest='" + (vehicleInterest != null ? vehicleInterest.getFullDescription() : "None") + '\'' +
                ", tradeIn='" + (tradeInVehicle != null ? tradeInVehicle.getFullDescription() : "None") + '\'' +
                ", score=" + score +
                '}';
    }
}