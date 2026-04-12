package com.areonedev.autotrack.objects;

import java.util.Date;

public class Task extends Event {
    public boolean isCompleted; // True if date is today or in the past

    public Task(String title, Date date) {
        super(title, date);
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}