package com.areonedev.autotrack.persistence;

import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.objects.Lead;

public interface DataAccess {
    void open(String string);

    void close();

    String getLeadSequential(List<Lead> leadResult);

    ArrayList<Lead> getLeadRandom(Lead lead);

    String insertLead(Lead lead);

    String updateLead(Lead lead);

    String deleteLead(Lead lead);

}
