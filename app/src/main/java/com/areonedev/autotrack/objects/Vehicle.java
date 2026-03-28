package com.areonedev.autotrack.objects;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String make;        // e.g., Volkswagen
    private String model;       // e.g., Atlas
    private String year;        // e.g., 2024
    private String trim;        // e.g., Highline R-Line (Important for pricing)
    private String transmission; // e.g., Automatic / Manual
    private String color;       // e.g., Black
    private double price;          // The MSRP or listed price of this specific unit
    private boolean inStock; // To indicate if the unit is currently on the lot
    private String vin;         // Optional: Vehicle Identification Number (for specific inventory tracking)

    // 1. Full Constructor: Includes all parameters
    public Vehicle(String make, String model, String year, String trim, double price,String color, boolean inStock, String vin,String transmission) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.trim = trim;
        this.transmission = (transmission == null) ? "Automatic" : transmission;;
        this.price = price;
        this.color=color;
        this.inStock=inStock;
        this.vin=vin;
    }

    // 2. Partial Constructor: Only basic info, defaults the rest
    public Vehicle(String make, String model, String year,String trim){
        this(make, model, year, trim, 0.0, null, false, "N/A", "Automatic");
    }

    // 3. Empty Constructor: Sets everything to null/default
    public Vehicle() {
        this.make = null;
        this.model = null;
        this.year = null;
        this.trim = null;
        this.price = 0;
        this.color = null;
        this.inStock = false;
        this.vin = "N/A";
        this.transmission = "Automatic";
    }

    // Getters and Setters...
    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    public String getTrim() {
        return trim;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getVin() {
        return vin;
    }

    public double getPrice() {
        return price;
    }
    public String getColor() { return color; }
    public boolean isInStock() { return inStock; }
    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setColor(String color) { this.color = color; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    // A helper method for your LeadAdapter to display a clean string
    public String getFullDescription() {
        if (make == null && model == null) return "No Vehicle Details";

        String displayYear = (year != null) ? year : "";
        String displayMake = (make != null) ? make : "";
        String displayModel = (model != null) ? model : "";
        String displayTrim = (trim != null) ? " (" + trim + ")" : "";

        return (displayYear + " " + displayMake + " " + displayModel + displayTrim).trim();
    }
}