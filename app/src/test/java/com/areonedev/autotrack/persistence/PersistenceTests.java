package com.areonedev.autotrack.persistence;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//public class PersistenceTests {
//    public static TestSuite suite;
//
//    public static Test suite()
//    {
//        suite = new TestSuite("Persistence tests");
//        suite.addTestSuite(DataAccessTest.class);
//        return suite;
//    }
//}

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataAccessTest.class,
})
public class PersistenceTests {
    // This class remains empty.
    // The annotations above tell JUnit to run the classes listed.
}
