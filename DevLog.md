# Developer Log — AutoTrack
---
**Date:** March 13, 2024  
**Module:** Persistence Layer (Stub Database Testing)

---

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

---

## Issues

- Minor logic bugs were discovered during early testing of insert and update operations.
- Adjusted `Lead.equals()` implementation to ensure correct object comparison during list operations.
- Ensured correct handling of ID matching for update and delete operations.

All identified issues have now been resolved.

---

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

---

## Issues

- `Log.d()` output is not visible in unit tests → switched to `System.out.println`.
- Java version compatibility issue: `getFirst()` / `getLast()` require Java 21.
- Decided to use a **system-generated ID** instead of a phone number as the primary key.

---

## Next

- Finalize `Lead.equals()` and `hashCode()` implementation.
- Standardize JUnit assertions across tests.
- Enable real database testing (`DataAccessObject`).
- Implement and test `deleteLead`.
- Update `AccessLeads` to use `getRandom(long id)`.
- Connect the persistence layer to the UI lead list view.