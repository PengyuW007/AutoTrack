package com.areonedev.autotrack.objects;

import java.util.Date;

public class Notification extends Event {

    // Constructor for new notifications (e.g., created by the Receiver)
    public Notification(Lead lead, String title, Date date) {
        super(lead, title, date);
    }

    // Constructor for existing notifications loaded from the Database
    public Notification(long id, Lead lead, String title, Date date) {
        super(lead, title, date);
        this.setEventID(id); // Using the setter from Event
    }
}