package com.areonedev.autotrack.business;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccessLeadsTest.class,
        PriorityManagerTest.class,
        ScoringServiceTest.class,
        // Add other test classes here as you create them, e.g., ObjectTests.class
})
public class BusinessTests {
}