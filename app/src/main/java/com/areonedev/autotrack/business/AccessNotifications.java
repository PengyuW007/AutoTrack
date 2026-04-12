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

    public String getNotifications(List<Notification>notifications){
        notifications.clear();
        return dataAccess.getNotificationSequential(notifications);
    }

    public Notification getSequential(){
        String result = null;

        if (notification == null || notifications.isEmpty()) {
            // the following line was added as a result of a failing test in AccessCoursesTest!
            notifications = new ArrayList<>();
            dataAccess.getNotificationSequential(notifications);
            currNotification = 0; // Reset counter for a fresh list
        }
        if (currNotification < notifications.size()) {
            notification = notifications.get(currNotification);
            currNotification++;
        } else {
            //now hit the end of the list, so reset the counter
            notification = null;
            notifications = null;
        }
        return notification;
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