# Developer Log — AutoTrack
---
**Date:** March 27-28, 2026

**Module:** Lead Management CRUD & UI Standardization Finalized

## Itinerary

- **UI Standardization:** Synchronized the visual identity of `LeadsCreationActivity` and `LeadDetailsActivity` by implementing a consistent `androidx.appcompat.widget.Toolbar` with functional **Up navigation** and white-titled headers.

- **Dual-Mode Detail View:** Engineered a hybrid layout in `LeadDetailsActivity` that toggles between a high-readability **View Mode** (using `CardView` for structured data presentation) and an **Edit Mode** (using `EditText` for inline modifications) via a Toolbar menu action.

- **CRUD Logic Integration:**
  - **Create:** Connected the **Save Lead** workflow to the `AccessLeads` business controller with integrated duplicate checking.
  - **Read:** Implemented serialized object passing to display all **17 lead parameters** in the detail view.
  - **Update:** Enabled real-time modification of lead and vehicle data, including automatic `leadUpdatedAt` timestamp refreshing.
  - **Delete:** Integrated a destructive action workflow with `AlertDialog` confirmation to ensure safe deletion and data integrity.

- **Data Normalization:** Refactored the `formatPhoneNumber` utility to strip non-numeric characters and enforce a standardized **xxx-xxx-xxxx** format across all persistence operations.

## Issues

- **Toolbar Type Mismatch:** Resolved a critical error caused by importing `android.widget.Toolbar` instead of the AndroidX variant, which prevented the **Edit (wrench) icon** from inflating correctly.

- **Serialization Errors:** Fixed a `Cannot resolve method putExtra` compilation error by ensuring both `Lead` and `Vehicle` domain objects implement `java.io.Serializable`.

- **UX Visibility Constraint:** Adjusted UI logic so the **Delete** button appears only in **Edit Mode**, reducing the risk of accidental deletions during normal viewing.

- **Metadata Reordering:** Updated the layout hierarchy to display **Last Updated** above the **Created** timestamp, improving visibility of recent activity for users.

## Next

- **List Synchronization:** Implement a result-callback or lifecycle-based refresh in `LeadsActivity` so the main list immediately reflects updates or deletions made in the detail view.

- **Input Masking:** Add a `TextWatcher` to phone input fields to provide real-time formatting (automatic dash insertion) during typing.

- **Address Expansion:** Extend the UI to support additional address parameters (**City, Province, Postal Code**) currently defined in the 17-parameter `Lead` constructor.

---
**Date:** March 25, 2026

**Module:** Refactoring Object classes
## Itinerary

- **Lead Class Refactoring:** Successfully expanded the `Lead` data model from a basic profile to a comprehensive **17-parameter constructor**. This enhancement enables the system to capture critical CRM details, including full mailing addresses, department-specific routing, and high-precision timestamps (`followUpDate` and `createdAt`).

- **Vehicle Class Creation:** Designed and implemented a dedicated `Vehicle` class to replace the previous `String`-based vehicle interest field. This structured representation (`Year`, `Make`, `Model`, `Trim`) supports improved inventory filtering and enables a more professional UI presentation.

- **Persistence Layer Synchronization:** Updated `DataAccessObject` (SQLite) to support the new object structures, ensuring that complex `Vehicle` objects are correctly serialized and deserialized.

- **Test Suite Overhaul:** Refactored all existing test suites, including `LeadTest`, `DataAccessTest`, and `BusinessPersistenceSeamTest`, to align with the updated constructor signatures and object relationships.

## Issues

- **Global Breaking Changes:** The transition to a **17-parameter constructor** caused immediate compilation errors across the entire project. Every instantiation of a `Lead` object in the business logic and test modules required manual updates.

- **Date Precision Conflict:** Identified a bug where *"Today"* leads appeared as *"Yesterday"*. This issue was traced to the `dd/MM/yyyy` formatter removing time precision, causing midnight-aligned timestamps to shift days due to timezone offsets.

- **Integration Test Regression:** In `BusinessPersistenceSeamTest.java`, the `testGetSequential` method failed because it still asserted legacy dummy names (e.g., *"Darren"*, *"Darryl"*) that no longer matched the updated database initialization sequence.

- **Vehicle Nullability:** Encountered `NullPointerException` issues in the UI layer when a lead was created without a vehicle selection. Implemented null-safety checks in `Lead` getters to return **"No Vehicle Interest"** as a fallback value.

## Next
- **UI Refinement:** Finalize `LeadAdapter` to support the new `Vehicle` object structure for single-row display formatting, using `SpannableStringBuilder` to programmatically bold the **Model** name.

- **Search Logic Update:** Refactor filtering logic in `LeadsActivity` to ensure **"Search Result"** headers are correctly inserted when users filter by name or phone.
  
- **Insert New Leads:** Implement the "Add Lead" workflow by connecting the Floating Action Button (`+`) on the `LeadsActivity` page to a new input form.
- **Database Integration:** Develop the logic to capture user input from the UI and persist new `Lead` and `Vehicle` objects into the SQLite database via the `AccessLeads` business controller.
---
**Date:** March 22, 2026

**Module:** UI Design & CRM Storyboard Implementation
## Itinerary
- **UI Integration:** Connected the SQLite database to the `LeadsActivity` and implemented the `LeadAdapter` with multi-view support.
- **Date Categorization:** Developed logic to group leads by creation date, injecting "Today", "Yesterday", and "dd MMM yyyy" headers into the RecyclerView.
- **Lead Card Enhancement:** Updated the `single_lead.xml` layout to include a right-aligned Status badge and a dedicated field for Vehicle Interest.
- **Data Seeding:** Expanded `addDummyLeads` in the DAO to include new test records (Pengyu, Irfan, Anna, Vignejan) with specific timestamps to verify relative date grouping.

## Issues
- **RecyclerView Sync (Resolved):** Fixed an `IndexOutOfBoundsException` caused by `getItemCount()` returning the raw lead count instead of the total count including injected date headers.
- **Timestamp Grouping (Resolved):** Leads from the same day were generating duplicate headers due to unique time-of-day values. Resolved by truncating the `CreatedAt` string to the first 10 characters (`yyyy-MM-dd`).
- **Format Mismatch (Resolved):** The DAO was using `dd/MM/yyyy` while the Adapter required `yyyy-MM-dd` for comparison logic. Standardized all date formats to ISO `yyyy-MM-dd`.
- **Data Stale-mate (Resolved):** New dummy leads were not appearing because the database already existed with a non-zero count. Performed a full app cache clear to trigger fresh data seeding.

## Next
- **Search Functionality:** Implement a `SearchView` in `LeadsActivity` to filter the list by name or phone number, ensuring date headers refresh dynamically.
- **Add Lead Story:** Connect the Floating Action Button (FAB) to a new activity to allow manual lead insertion into the database.
- **Object Refactoring:** Transition `VehicleInterest` from a `String` to a dedicated `Vehicle` object class to support more granular data (Year, Model, Trim).
- **System Testing:** Introduce Espresso tests to verify that clicking a lead in the list opens the correct detail view.

---
**Date:** March 20-21, 2026

**Module:** Integration Testing (Persistence)
## Itinerary
- Successfully completed the full testing pyramid (Unit, Integration, and Business-Persistence Seam). The data layer is now verified as stable and deterministic.
- Resolved non-deterministic test failures in `BusinessPersistenceSeamTest`.
- Implemented a database reset strategy to ensure test isolation.
## Issues
- **State Leakage:** `testGetSequential` was failing with an unexpected 4th lead.
  - *Cause:* SQLite persistence on the Android Emulator was preserving data from `testInsertLeadSeam` across test boundaries.
  - *Resolution:* Added `context.deleteDatabase(dbName)` to the `@Before` setup. This forces the `DataAccessObject` to recreate the schema and re-populate default data for every test case.
## Next
- **UI Phase 1:** Design the `LeadListActivity` using a `RecyclerView`.
- **UI Phase 2:** Implement the `AddLeadActivity` and bind the "Save" button to `AccessLeads.insertLead()`.
- **System Testing:** Introduce Espresso tests once the first UI components are functional.
---
**Date:** March 19, 2026

**Module:** Android Integration Testing (Test Suite Setup on emulator with real DB )

## Itinerary
- Configured `IntegrationTests.java` as a JUnit Suite to run all persistence-related instrumented tests in a single pass.
- Included `DataAccessDatabaseTest` and `BusinessPersistenceSeamTest` in the suite to validate both the DAO and the Business-Persistence Seam.
- Verified that the test environment correctly targets the Android Emulator for SQLite operations.

## Issues
- **Test Execution Environment:** Confirmed that instrumented tests (located in `androidTest`) cannot run on a local JVM and require an active Android Runtime (ART).
  - *Resolution:* Established the workflow of launching the AVD (Android Virtual Device) prior to executing the test suite.
- **Suite Synchronization:** Updated the `@SuiteClasses` annotation to ensure all relevant integration tests are included in the build.

## Next
- **Database Inspection:** Use the "App Inspection" tool in Android Studio to view the live SQLite tables on the emulator while the tests are running.
---
**Date:** March 18, 2026

**Module:** Real Database Implementation (SQLite) & Android Test Environment

## Itinerary
- **Transition to Real Persistence:** Initiated the development of `DataAccessObject.java` to replace the `DataAccessStub` with a real SQLite implementation.
- **Schema Design:** Defined the SQL schema for the `Leads` table, ensuring alignment with the `Lead` object. Implemented `LeadID` as an `INTEGER PRIMARY KEY AUTOINCREMENT` to ensure the database handles identity generation.
- **Android Test Setup:** Created the `androidTest` directory structure and initialized `SampleAcceptanceTests.java`.
- **Dependency Management:** Updated `build.gradle.kts` to include `androidx.test.rules` and `androidx.test.ext.junit`, resolving configuration errors with the test runner.
- **Modernizing Test Rules:** Migrated from the deprecated `ActivityTestRule` to the modern `ActivityScenarioRule` for better Activity lifecycle management during UI tests.

## Issues
- **ID Assignment Logic:** Identified that the `LeadID` must only be assigned to the `Lead` object *after* a successful `db.insert()`.
  - *Resolution:* Refactored `insertLead` to capture the generated `rowId` and update the object in-memory, ensuring the Business Layer receives the persistent ID.
- **The "Dead Loop" Search Bug:** Recognized that searching for a lead with ID 0 (new lead) would fail in a strict ID-based SQL query.
  - *Resolution:* Refactored `getLeadRandom` to perform a string-based search (FirstName, LastName, Phone) when the provided ID is 0.
- **Build Configuration Errors:** Encountered "Cannot resolve symbol" for `ActivityTestRule` and `AndroidJUnit4`.
  - *Resolution:* Cleaned up duplicate imports and added the missing `androidTestImplementation` libraries to the Kotlin DSL build file.

## Next
- **DAO Refinement:** Complete the `parseCursor` and `getLeadContentValues` methods to ensure all `Lead` fields (Budget, Dates, Notes) are correctly mapped to SQL types.
- **Integration Re-Validation:** Swap the `DataAccessStub` for the `DataAccessObject` in the `BusinessPersistenceSeamTest` suite to verify the SQL logic.
- **UI-to-DB Binding:** Connect the `HomeActivity` to the `AccessLeads` controller to verify that data entered in the UI is successfully persisted to the SQLite file.
- **Persistence Verification:** Write an Espresso test to verify that a Lead created in one session survives an app restart.
---
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