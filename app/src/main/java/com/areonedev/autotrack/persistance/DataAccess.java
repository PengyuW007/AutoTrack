package com.areonedev.autotrack.persistance;

import java.util.ArrayList;
import java.util.List;

import com.areonedev.autotrack.objects.Lead;

public interface DataAccess {
    void open(String string);

    void close();
}
