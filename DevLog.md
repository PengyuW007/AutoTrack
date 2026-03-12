# Developer Log — AutoTrack

**Date:** March 10, 2024  
**Module:** Persistence Layer

---

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