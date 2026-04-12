package com.areonedev.autotrack.business;

import com.areonedev.autotrack.application.Main;
import com.areonedev.autotrack.application.Services;
import com.areonedev.autotrack.objects.Notification;
import com.areonedev.autotrack.persistence.DataAccess;

import java.util.ArrayList;
import java.util.List;

public class AccessNotifications {
    private DataAccess dataAccess;
    private List<Notification>notifications;
    private Notification notification;
    private int currNotification;

    public AccessNotifications() {
        this.dataAccess = Services.getDataAccess(Main.dbName);
        this.notifications = new ArrayList<>();
        this.notification = null;
        this.currNotification = 0;
    }

    public List<Notification> getAllNotifications() {
        return dataAccess.getAllNotifications();
    }

    public String insertNotification(Notification currNotification) {
        return dataAccess.insertNotification(currNotification);
    }

    public String updateNotification(Notification currNotification){
        return dataAccess.updateNotification(currNotification);
    }

    public String deleteNotification(Notification currNotification){
        return dataAccess.deleteNotification(currNotification);
    }

}