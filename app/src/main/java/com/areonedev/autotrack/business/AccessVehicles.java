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

}
