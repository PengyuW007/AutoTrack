Developer Log: Persistence Layer Testing
Date: March 11, 2024 Module: AutoTrack.app.persistence Status: In Progress (Unit Testing Phase)
✅ Completed Tasks
    1. Unit Test Environment Setup: Configured DataAccessTest to toggle between Stub and Real database implementations using setUp() injection.
    2. Sequential Retrieval Test: Verified that getLeadSequential correctly populates a list and returns the expected initial dataset (3 leads).
    3. Search Logic (Random Access):
        Implemented testRandom to verify search functionality.
        Identified and resolved the "Expected 0, Actual 1" failure by determining that Lead.equals() must be overridden to support ArrayList.indexOf() in the Stub.
    4. Insertion Logic:
        Verified "Unique Insert" (size increases from 3 to 4).
        Verified "Duplicate/Invalid Handling" (ensuring the database size remains stable when bad data is provided).
    5. Update Logic:
        Completed testUpdateLead. Verified that modifying an object and passing it to updateLead correctly replaces the record in the Stub based on the Primary Key (ID).
    6. Object Architecture:
        Refactored Lead.java to use a long data type for IDs.
        Implemented a static auto-incrementing ID counter in the Lead constructor.
⚠️ Issues Identified / Resolved
    Log Visibility: Discovered Log.d does not output to the standard Unit Test console. Switched to System.out.println for developer visibility during test execution.
    Java Version Compatibility: Noted that getFirst() and getLast() require Java 21. Prepared fallback to .get(0) and .get(size - 1) for broader compatibility.
    Primary Key Strategy: Confirmed that System-Generated IDs are superior to Phone Numbers for data integrity (immutability).
📅 Next Steps & Schedule
    | Phase | Task | Description | Target Date | | :--- | :--- | :--- | :--- | | 
    Phase 1: Cleanup | Lead Object Finalization | Ensure Lead.java has a robust equals() and hashCode() based on leadID. | Today | | 
    Phase 1: Cleanup | Refactor Assertions | Standardize Assert.assertEquals (JUnit 4) vs assertEquals (JUnit 3) across the test file. | Today | | 
    Phase 2: Persistence | Real DB Testing | Uncomment real() in setUp() and verify that DataAccessObject (SQLite/HSQLDB) passes the same tests as the Stub. | Tomorrow | | 
    Phase 2: Persistence | Delete Functionality | Implement and test deleteLead(Lead lead) in both the Stub and the Test suite. | Tomorrow | | 
    Phase 3: Business | AccessLeads Integration | Update the AccessLeads business controller to use the new long getRandom(long id) method. | Thursday | | 
    Phase 4: UI | Lead List View | Connect the verified DataAccess layer to the Lead List Activity to display real data. | Friday |
