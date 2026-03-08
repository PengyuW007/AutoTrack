package com.areonedev.autotrack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Import your actual test classes here
import com.areonedev.autotrack.integration.IntegrationTests;
import com.areonedev.autotrack.persistence.DataAccessTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        IntegrationTests.class,
        DataAccessTest.class
})
public class RunIntegrationTests {
    // This class remains empty.
    // The annotations above tell JUnit to run the classes listed.
}