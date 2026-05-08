package com.areonedev.autotrack.objects;

import java.util.Date;

public class Task extends Event {
    private boolean isCompleted;

    // Constructor for new tasks
    public Task(Lead lead, String title, Date date) {
        super(lead, title, date);
        this.isCompleted = false;
    }

    // Constructor for existing tasks loaded from the Database
    public Task(long id, Lead lead, String title, Date date) {
        super(lead, title, date);
        this.setEventID(id);
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}