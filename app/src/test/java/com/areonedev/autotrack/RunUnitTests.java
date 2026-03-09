package com.areonedev.autotrack;

import junit.framework.Test;
import junit.framework.TestSuite;

//import com.areonedev.autotrack.business.BusinessTests;
//import com.areonedev.autotrack.objects.ObjectTests;
import com.areonedev.autotrack.persistence.PersistenceTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PersistenceTests.class
        // Add other test classes here as you create them, e.g., ObjectTests.class
})
public class RunUnitTests {
    // This class remains empty. The annotations above do all the work.
}

/* JUnit 3 Version ****
public class RunUnitTests {
    public static TestSuite suite;


     //* This method is called by the JUnit runner. It must be public static Test suite() to be recognized.
    public static Test suite()
    {
        suite = new TestSuite("Unit tests");

        // Add the PersistenceTests suite which contains DataAccessTest
        suite.addTest(PersistenceTests.suite());
        // suite.addTest(ObjectTests.suite());
        // suite.addTest(BusinessTests.suite());

        return suite;
    }
}
*/