package com.areonedev.autotrack.business;

import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Vehicle;
import com.areonedev.autotrack.persistence.DataAccess;

public class AccessVehicles {
    private DataAccess dataAccess;
    private List<Vehicle> vehicles;
    private Vehicle vehicle;
    private int currVehicle;

    public AccessVehicles() {
        dataAccess = Services.getDataAccess(Main.dbName);
        vehicles = new ArrayList<>();
        vehicle = null;
        currVehicle = 0;
    }

    public String getVehicles(List<Vehicle> vehicles) {
        vehicles.clear();
        return dataAccess.getVehicleSequential(vehicles);
    }

    public Vehicle getSequential() {
        String result = null;

        if (vehicles == null || vehicles.isEmpty()) {
            vehicles = new ArrayList<>();
            dataAccess.getVehicleSequential(vehicles);
            currVehicle = 0;
        }

        if (currVehicle < vehicles.size()) {
            vehicle = vehicles.get(currVehicle);
            currVehicle++;
        } else {
            vehicle = null;
            vehicles = null;
            currVehicle = 0;
        }

        return vehicle;
    }

    public Vehicle getRandom(long id) {
        if (id <= 0) {
            vehicle = null;
        }

        Vehicle temp = new Vehicle();
        temp.setVehicleID(id);
        vehicles = dataAccess.getVehicleRandom(temp);

        currVehicle = 0;
        if (currVehicle < vehicles.size()) {
            vehicle = vehicles.get(currVehicle);
            currVehicle++;
        } else {
            vehicle = null;
            vehicles = null;
        }
        return vehicle;
    }

    public String insertVehicle(Vehicle currVehicle) {
        return dataAccess.insertVehicle(currVehicle);
    }


    public String updateVehicle(Vehicle currVehicle) {
        return dataAccess.updateVehicle(currVehicle);
    }

    public String deleteVehicle(Vehicle currVehicle) {
        return dataAccess.deleteVehicle(currVehicle);
    }

}
