package com.areonedev.autotrack;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

import com.areonedev.autotrack.acceptance.SampleAcceptanceTests;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SampleAcceptanceTests.class})
public class RunAcceptanceTests
{
    public RunAcceptanceTests()
    {
        System.out.println("Acceptance Tests passed!");
    }
}