package com.areonedev.autotrack.objects;
import junit.framework.Test;
import junit.framework.TestSuite;
public class ObjectTests {
    public static TestSuite suite;

    public static Test suite()
    {
        suite = new TestSuite("Object tests");
        suite.addTestSuite(LeadTest.class);
        return suite;
    }
}
