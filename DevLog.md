# Developer Log — AutoTrack
---
**Date:** March 15, 2026

**Module:** Integration Testing (Business–Persistence Seam)

## Itinerary

- Continued integration testing for the **Business–Persistence seam**.
- Worked on `BusinessPersistenceSeamTest` and investigated failures related to the `getRandom` method in `AccessLeads`.
- Began debugging the `testGetRandom` unit tests in `DataAccessTest`.
- Reviewed the identity logic of the `Lead` class and confirmed that **LeadID must remain the primary identifier** for the `equals()` and `hashCode()` contracts.
- Determined that the `setID()` method needs to be implemented to support the correct lifecycle of a `Lead` object.
- Clarified the object lifecycle:
  - A `Lead` object is initially created with **ID = 0** (transient state).
  - After insertion through the **Persistence Layer**, the object receives its actual database ID using `setID()`.
- Added a **static instance counter** to the `Lead` class to monitor how many objects are created during testing, helping identify unnecessary object creation.

## Issues

- The `testGetRandom` tests are currently **still in progress** due to inconsistencies between **object identity logic** and **search behavior in the Stub database**.
- Searches using manually instantiated `Lead` objects fail when the Stub relies strictly on **ID-based equality**.
- Additional adjustments are required to properly integrate the `setID()` method with the persistence workflow.

## Next

- Implement and integrate the `setID()` method within the `Lead` class.
- Continue debugging and complete the `testGetRandom` test cases.
- Refine the Stub search logic to ensure compatibility with object identity rules.
- Finalize the **Business–Persistence seam integration testing**.
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