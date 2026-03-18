# Developer Log — AutoTrack
**Date:** March 15-17, 2024

**Module:** Integration Testing & Suite Consolidation

## Itinerary

- Successfully completed the integration tests for `BusinessPersistenceSeamTest`, verifying the connection between `AccessLeads` and `DataAccessStub`.
- Implemented `getLeadByName_Phone(String, String)` in the Business Layer to allow searching via business keys (Name/Phone) rather than just the Primary Key.
- Refined the **Lead Object Lifecycle**:
  - Confirmed that a `Lead` is instantiated with **ID = 0** (Transient state).
  - Confirmed that the **Persistence Layer** is responsible for assigning the real ID upon successful insertion.
- Integrated the `BusinessPersistenceSeamTest` into the global `RunUnitTests.java` suite to ensure continuous integration of the business logic.
- Utilized the **static instance counter** in the `Lead` class to verify that "Criteria" objects used for searching are being instantiated correctly without bloating the database state.

## Issues

- **The "Dead Loop" Search Bug:** Discovered that searching with a new `Lead` object (ID 0) failed in the Stub because the Stub relied on `indexOf()` (which compares IDs).
- **Resolution:** Refactored `getLeadByName_Phone` to act as a bridge—it creates a transient criteria object, retrieves the persistent record from the Stub, and extracts the database-assigned ID from the result.
- **Null Safety:** Fixed a potential `IndexOutOfBoundsException` in the search method by ensuring the results list is validated before accessing the first element.
- **ID Assignment Paradox:** Resolved the conflict between `Lead.equals()` (ID-based) and search requirements by ensuring the Business Layer handles the extraction of IDs from returned persistence objects.

## Next

- **Real Database Implementation:** Transition from `DataAccessStub` to the SQLite `DataAccessObject`. This is the priority to ensure that the auto-increment logic and Primary Key constraints discovered during integration testing function correctly in a persistent storage environment.
- **Database Integration Testing:** Run the existing `BusinessPersistenceSeamTest` suite against the real database implementation to verify the "Seam" remains intact.
- **Android UI Development:** Once the data layer is verified as persistent, begin building the Lead Entry and Lead List Activities using the `AccessLeads` controller.
- **Espresso UI Testing:** Implement automated UI tests to verify the end-to-end flow from User Input -> Business Logic -> SQLite Storage.
- ---
**Date:** March 14, 2024

**Module:** Domain Model (Object-Level Testing)

## Itinerary

- Completed the `LeadTest` unit test suite to verify the core data model.
- Fully tested the `Lead` class logic and state management.
- Verified correct functionality for the following components:
  - `Lead(String, String, ...)` (Full Constructor validation)
  - `setLeadName(String)` (Name splitting and null-safety logic)
  - `getLeadName()` (Formatted string retrieval)
  - `equals(Object)` and `hashCode()` (Identity and duplicate detection)
  - `leadID` (Auto-increment and immutability)

- Confirmed that the constructor enforces the "at least one name" validation rule.
- Verified that null inputs are handled gracefully without throwing NullPointerExceptions.
- Confirmed that `equals()` correctly identifies duplicates based on name and phone.
- Verified that the static ID counter increments correctly across multiple instances.

## Issues

- Discovered that `getLeadName()` returned a leading space when the first name was null.
- Fixed by implementing `.trim()` in the name concatenation logic.
- Found that `equals()` failed when comparing a Lead to a null object; added null-check guards.

All identified issues have now been resolved.

## Next

- Begin **integration testing between the Persistence Layer and the Business Layer**.
- Verify that `AccessLeads` controller correctly handles data from `DataAccessStub`.
- Prepare test cases for end-to-end lead management workflows in the UI.

---
**Date:** March 13, 2024  
**Module:** Persistence Layer (Stub Database Testing)

## Itinerary

- Completed the `DataAccessTest` unit test suite for the Stub database implementation.
- Fully tested all persistence methods in `DataAccessStub`.
- Verified correct functionality for the following methods:

  - `String getLeadSequential(List<Lead> leadResult)`
  - `ArrayList<Lead> getLeadRandom(Lead lead)`
  - `String insertLead(Lead lead)`
  - `String updateLead(Lead lead)`
  - `String deleteLead(Lead lead)`

- Confirmed that all CRUD operations behave correctly in the Stub database environment.
- Verified that list population, searching, insertion, updating, and deletion behave as expected.
- Fixed all issues discovered during testing.
- Confirmed stable dataset behavior across multiple test cases.

## Issues

- Minor logic bugs were discovered during early testing of insert and update operations.
- Adjusted `Lead.equals()` implementation to ensure correct object comparison during list operations.
- Ensured correct handling of ID matching for update and delete operations.

All identified issues have now been resolved.

## Next

- Begin **object-level testing** for the `Lead` class.
- Verify correctness of:
  - Constructors
  - Getter methods
  - `equals()` and `hashCode()`
  - Object state integrity
- Ensure that `Lead` behaves correctly as the fundamental data entity before integrating further layers.
- Prepare for the next phase: **integration between Persistence Layer and Business Layer**.

---
**Date:** March 10, 2024  
**Module:** Persistence Layer

## Itinerary

- Set up the unit testing environment (`DataAccessTest`) with Stub and Real database switching.
- Tested `getLeadSequential` to verify initial dataset retrieval (3 leads).
- Implemented `testRandom` to verify lead search functionality.
- Fixed search failure by implementing `Lead.equals()` so `ArrayList.indexOf()` works correctly.
- Tested insertion logic:
    - Verified **unique insert** increases dataset size (3 → 4).
    - Verified **duplicate/invalid insert** does not modify the dataset.
- Implemented and tested `updateLead`.
    - Confirmed updates correctly replace records using the primary key.
- Refactored the `Lead` object:
    - Switched ID type to `long`.
    - Implemented auto-increment ID generation.

## Issues

- `Log.d()` output is not visible in unit tests → switched to `System.out.println`.
- Java version compatibility issue: `getFirst()` / `getLast()` require Java 21.
- Decided to use a **system-generated ID** instead of a phone number as the primary key.

## Next

- Finalize `Lead.equals()` and `hashCode()` implementation.
- Standardize JUnit assertions across tests.
- Enable real database testing (`DataAccessObject`).
- Implement and test `deleteLead`.
- Update `AccessLeads` to use `getRandom(long id)`.
- Connect the persistence layer to the UI lead list view.