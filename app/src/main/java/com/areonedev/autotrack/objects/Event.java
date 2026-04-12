package com.areonedev.autotrack.objects;

import java.util.Date;

public abstract class Event {
    protected String title;
    protected Date date;

    public Event(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() { return title; }
    public Date getDate() { return date; }
}