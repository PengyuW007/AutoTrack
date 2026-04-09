package com.areonedev.autotrack.objects;

import java.util.Date;

public class Task {
    public String title;
    public Date date;
    public boolean isCompleted; // True if date is today or in the past

    public Task(String title, Date date, boolean isCompleted) {
        this.title = title;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}