package com.areonedev.autotrack.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataAccessDatabaseTest.class,
        BusinessPersistenceSeamTest.class,
})
public class IntegrationTests {
    // This class remains empty.
    // The annotations above tell JUnit to run the classes listed.
}