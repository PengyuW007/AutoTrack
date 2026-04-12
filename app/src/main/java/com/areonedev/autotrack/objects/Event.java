package com.areonedev.autotrack.objects;

import java.util.Date;

public abstract class Event {
    protected String title;
    protected Date date;
    protected Lead lead;
    protected long eventID;

    public Event(Lead lead, String title, Date date) {
        this.title = title;
        this.date = date;
        this.lead = lead;
        this.eventID = -1;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public long getEventID() {
        return eventID;
    }
}