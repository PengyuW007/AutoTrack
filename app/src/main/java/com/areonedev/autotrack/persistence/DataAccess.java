package com.areonedev.autotrack.persistence;

import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.objects.Lead;
import com.areonedev.autotrack.objects.Notification;

public interface DataAccess {
    void open(String string);

    void close();

    /*** Lead ***/
    String getLeadSequential(List<Lead> leadResult);

    ArrayList<Lead> getLeadRandom(Lead lead);

    String insertLead(Lead lead);

    String updateLead(Lead lead);

    String deleteLead(Lead lead);

    /*** Notification ***/
    String getNotificationSequential(List<Notification>notificationResult);
    ArrayList<Notification>getNotificationRandom(Notification notification);

    String insertNotification(Notification notification);
    String updateNotification(Notification notification);
    String deleteNotification(Notification notification);

    List<Notification> getAllNotifications();

}
