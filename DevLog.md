Developer Log — AutoTrack

---
**Date:** May 11, 2026

**Module:** Dynamic Filtering & Vehicle-Aware Search System

## Itinerary

- Successfully migrated filtering logic from `LeadsActivity` into `AccessLeads.java`, ensuring the presentation layer remains thin and aligned with proper multi-tier architecture principles.

- Developed the `getLeadsFiltered()` pipeline to support simultaneous filtering across:
  - Full-text search (`Name` and `Phone`)
  - Categorical filters (`Status`, `Stage`, `Division`)
  - Vehicle-based filtering (`Year`, `Make`, `Model`)

- Implemented:
  - `getUniqueVehicleYears()`
  - `getMakesByYear()`
  - `getModelsByYearAndMake()`

  allowing filter dropdown options to load dynamically from the SQLite database instead of relying on static XML arrays.

- Designed and implemented an expandable vertical **Filter Board** in `LeadsActivity`, improving usability and responsiveness on mobile layouts.

- Integrated cascading dropdown logic:
  ```
  Year → Make → Model
  ```
  preventing invalid vehicle combinations during filtering.

- Updated the `SearchView` listener so `applyFilters()` executes on every keystroke, creating a real-time filtering experience.

## Issues

- Identified an icon reset issue after applying filters. When the **Apply Filters** button is clicked, the filter icon incorrectly changes back to the wrench icon instead of remaining as the funnel icon.

- Identified a lead sorting issue where leads are displayed in the wrong order. Newly created or most recently created leads should appear at the top, while older leads should appear below them.

- Identified a vehicle persistence issue in `LeadDetailsActivity` Edit Mode. When vehicle interest information is changed and **Save Changes** is clicked, the lead card switches back to the old vehicle information instead of saving the updated selection.

- Identified a dropdown population issue in the Filter Panel. Under the **Interested Vehicle** section, the `Year`, `Make`, and `Model` dropdowns do not display vehicle options, even though they should load available vehicle data from the database and allow user selection.

- Identified a cascading dropdown issue in `LeadDetailsActivity` Edit Mode. The `Year` dropdown displays correctly, but the dependent `Make` and `Model` dropdowns do not populate based on the selected year.

## Next

- Fix the filter icon toggle logic so the funnel icon remains visible after filters are applied.

- Update the DAO query or lead retrieval logic to display leads in descending creation order, with the newest leads appearing first.

- Debug and repair vehicle interest persistence in `LeadDetailsActivity`, ensuring updated vehicle selections are correctly saved through `updateLead()` and reflected in View Mode.

- Verify the vehicle dropdown data source in the Filter Panel, especially the database query methods used to populate `Year`, `Make`, and `Model`.

- Manually trigger cascading dropdown initialization in Edit Mode so existing Year selections correctly load their related Make and Model options.
---

**Date:** May 9-10, 2026

**Module:** Smart UI & Data Integrity

## Itinerary

- **Database Integration:** Updated the DAO to seed the vehicle table directly from CSV data without requiring a complex constructor-based setup.

- **Smart Vehicle Selection UI:** Replaced four static `EditText` fields with four intelligent, cascading `AutoCompleteTextView` components for vehicle selection.

- **Data Integrity:** Ensured the normal **View Mode** card in `LeadDetailsActivity` dynamically refreshes immediately after users save vehicle-related changes.

## Issues

- **Static Input Limitation:** The previous vehicle input fields relied on free-text entry, which increased the risk of inconsistent vehicle data.

- **View Mode Synchronization:** The Lead Details display initially did not update immediately after edits were saved.

## Next

- **Calendar Activity Refinement:** Refactor the Calendar task pipeline so only tasks due on the selected date are displayed.

- **Priority Sorting:** Sort daily tasks so high-priority follow-ups and high-engagement leads appear at the top of the agenda.
---
**Date:** May 8, 2026

**Module:** Task Details Refinement & Vehicle Data Integration

## Itinerary

- **Lead Details UI/UX Refinement:**
  - Relocated the **Created At** and **Last Updated** metadata from the Notes section to a pinned footer at the bottom of `LeadDetailsActivity`.
  - Refactored the layout using `CoordinatorLayout` combined with `NestedScrollView` to maintain visibility of modification timestamps even when large task lists are displayed.
  - Improved overall visual hierarchy and readability within the Lead Details workflow.

- **Task Management Bug Fixes:**
  - Resolved an issue where tasks appeared to delete or update successfully in the UI but were not actually modified in the database.
  - Identified the root cause within `getTaskRandom`, where the `TaskID` was not properly mapped back onto the `Task` object during retrieval.
  - Updated the DAO retrieval pipeline to correctly assign the `EventID`, ensuring SQL statements using:
    ```sql
    WHERE TaskID = ?
    ```
    target the correct database row.

- **Vehicle Persistence Layer Integration:**
  - Expanded the `DataAccess` interface and `DataAccessObject` implementation to support full **Vehicle CRUD operations**.
  - Added the `Cars` table schema within the DAO `open()` initialization process, supporting:
    - Year
    - Make
    - Model
    - Trim
  - Implemented a transaction-based CSV parser to import vehicle inventory data from:
    ```
    assets/db/CARS.csv
    ```
    into the local SQLite database for standardized vehicle selection.

## Issues

- **Access Layer Incomplete:**  
  The persistence layer for Vehicles is fully operational; however, the `AccessVehicles` business layer remains incomplete, preventing UI modules from interacting with the vehicle dataset.

- **Calendar Filtering Logic:**  
  Identified a flaw in `CalendarActivity` where tasks are not being filtered strictly by their assigned dates, causing unrelated tasks to appear within the daily agenda.

## Next

- **Complete AccessVehicles Layer:**  
  Finalize the business logic methods required to bridge the Vehicle DAO with UI components.

- **Calendar Activity Refactor:**
  - Implement strict **date-specific task filtering** so daily agendas display only tasks assigned to the selected date.
  - Develop an advanced sorting pipeline based on:
    - **Completion Status:** Completed tasks move to the bottom.
    - **Priority:** Active tasks sorted by urgency score.

- **Vehicle UI Integration:**  
  Begin integrating the Vehicle database into lead creation and detail workflows using searchable dropdown components and inventory-linked selection logic.

---
**Date:** May 5, 2026

**Module:** Task Persistence & Lifecycle Management

## Itinerary

- **Task Database Architecture:**
  - Integrated the `Tasks` table into the `DataAccessObject (DAO)` layer.
  - Established a **LeadID foreign key relationship**, ensuring each task is linked to a specific lead.
  - Configured the `isCompleted` field as an integer (0/1) to persist task completion state across app restarts.

- **Intelligent Persistence Logic (ScoringService):**
  - Implemented a **hydration pattern** in `getFullTimeline`, prioritizing database records before generating missing milestones.
  - Added **idempotent insertion logic** to prevent duplicate system-generated tasks (e.g., "Day 1 Gratitude").
  - Enabled **auto-hardening**, where system-generated milestones are immediately inserted into the database upon creation.

- **UI-to-Database Synchronization:**
  - Implemented `OnTaskStatusChangedListener` to connect `TimelineAdapter` with the DAO layer.
  - Enabled **real-time persistence**, where toggling a task in the UI triggers an immediate database `UPDATE`.
  - Developed the `saveManualTask` workflow, allowing users to create custom tasks (e.g., Appointments, Test Drives) that are instantly stored and reflected in scoring.

- **Engagement Score Stabilization:**
  - Refactored the scoring engine to read directly from the `Tasks` table instead of relying on in-memory calculations.
  - Ensured the engagement score is now **fully consistent with persisted task data**.

## Issues

- **Volatile Engagement Score:**
  - Root cause: Score calculation was previously dependent on transient in-memory task states.
  - Resolution: Updated scoring logic to query persisted task data, ensuring accuracy and consistency across sessions.

## Next

- **Task CRUD Expansion:** Implement **Update/Delete functionality** via long-press gestures in the timeline to allow users to manage tasks more effectively.

- **Lifecycle Management Enhancements:** Introduce safeguards for handling accidental task creation and improve edit workflows.

- **Vehicle Inventory Integration (Future Phase):** Begin integrating the **Car Inventory Database** to associate specific vehicles with leads, enabling more context-aware and dynamic task generation.

---
**Date:** April 17 – April 23, 2026

**Module:** Core Logic Refinement & UI/UX Optimization
## Itinerary

- **Task Logic & Cadence Audit:** Performed a full calibration of the **1-year Scientific Follow-up algorithm**, resolving edge cases where tasks were triggered on incorrect day offsets.

- **Cadence Integrity Enforcement:** Ensured that the **Scientific Mission timeline** strictly follows the intended engagement schedule:
  - 48-hour follow-up
  - Day 3
  - Day 7
  - Day 14
  - Monthly milestones  
    providing a consistent roadmap for structured sales engagement.

- **Calendar UI/UX Polishing:**
  - Added a distinct visual state for the system date (**Today**) within `WeekAdapter`, clearly separating it from the user’s selected date.
  - Implemented `jumpToToday()` in `CalendarActivity`, allowing users to instantly return to the current week after navigating across schedules.

- **Intelligent Notification Tagging:**
  - Enhanced `LeadInteractionReceiver` to classify interaction types from system intents:
    - **Missed Calls** → Red-coded alerts
    - **Inbound SMS/Messages** → Green-coded alerts
    - **System Reminders** → Info-coded alerts
  - Embedded `leadId` metadata inside notifications to enable **direct deep-link navigation** to Lead Detail cards.

- **Performance Optimization:**
  - Disabled default Android window transition animations via the application theme, creating a **near-zero-latency navigation experience** between Dashboard, Calendar, and Lead Details modules.

- **Branding Consistency:**
  - Replaced all default launcher icons with the finalized **AutoTrack logo**.
  - Deployed adaptive icons across all required `mipmap` densities (`hdpi` through `xxhdpi`) to ensure consistent rendering across devices.

## Issues

- **Follow-up Logic Drift:** Fixed an error where the **48-hour response window** was calculated from the last system-generated task instead of the last **human interaction event**.

- **Calendar Navigation Friction:** Reduced user disorientation by introducing persistent visual indicators for the current system date.

- **Asset Scaling Artifact:** Corrected logo blurriness on high-density displays by switching to **vector-backed adaptive launcher icons**.

## Next

- **Lead Details Refinement (Pending):** Continue refinement of the `LeadDetailsActivity` display logic and interaction workflows, including improved metadata rendering and timeline integration consistency.

- **Vehicle Database:** Integrate the vehicle database into the system, which is for the vehicle search system and interested cars for leads.

---
**Date:** April 9-13, 2026

**Module:** Notification Activity Completed & Object Architecture Refinement

**Status:** Core Features Completed

## Itinerary

- **Notification Activity Completion:** Finalized the `NotificationActivity` and `NotificationAdapter`. The system now successfully displays a chronological log of all lead interactions (Calls, SMS, Emails).

- **Object Model Refactoring:**
  - Refactored `Notification` and `Task` classes to inherit from a unified `Event` base class.
  - Updated constructors to require a `Lead` object, ensuring strict relational integrity across the app.
  - Implemented "Lead Hydration" logic in the DAO to resolve `LeadID` foreign keys into full `Lead` objects during data retrieval.

- **Vehicle Data Integration:** Successfully imported the **Vehicle CSV dataset** into the SQLite database. Transitioned from a free-text "Vehicle Interest" string to a structured relational model, laying the groundwork for searchable inventory selection.

- **Navigation Flow Stabilization:**
  - Resolved the "Activity Freezing" issue by implementing `FLAG_ACTIVITY_REORDER_TO_FRONT` across the Bottom Navigation Bar.
  - Fixed navigation loops between `LeadsActivity`, `CalendarActivity`, and `NotificationsActivity` by standardizing the `OnItemSelectedListener` logic.

- **Automated Interaction Tracking:** Integrated `LeadInteractionReceiver` with the database. Incoming communications now trigger a background lookup to identify the lead by phone/email and automatically generate a linked notification.

## Issues

- **Database Schema Mismatch (Resolved):** Fixed a crash caused by a missing `LeadID` column in the `Notifications` table. Implemented a table recreation script to align the SQLite schema with the refactored `Notification` object.

- **Navigation Deadlock (Resolved):** Fixed a bug where `MainActivity` (Calendar) would force-redirect to `LeadsActivity`, preventing users from accessing the calendar view.

- **Icon Rendering (Resolved):** Identified that legacy system PNG icons were appearing faded/muddy. Replaced them with modern Vector Drawables and applied `app:tint` for high-contrast visibility.

## Next

- **Task Logic Audit:** Re-evaluate and fix the remaining inconsistencies in the 1-year scientific follow-up cadence to ensure tasks trigger on the correct days.

- **Lead Detail Refinement:** Replace the manual "Vehicle Interest" text input with a searchable database lookup linked to the newly imported Vehicle CSV data.

- **Calendar Polishing:**
  - Implement visual highlighting for "Today" in the week strip.
  - Add a "Jump to Today" shortcut to quickly reset the view after scrolling.

- **Notification Tagging:** Enhance the `LeadInteractionReceiver` to intelligently tag notifications with specific interaction types (e.g., "Missed Call," "Inbound SMS") based on system intent data.

- **Animation Removal:** Disable default Android window transition animations to create a faster, "instant-load" user experience.

- **Brand Identity:** Redesign and replace the default Android launcher icon with the finalized AutoTrack logo across all mipmap densities.

---
**Date:** April 8, 2026

**Module:** Lead Details & Data Assets Integration

## Itinerary

- **Task Object Refactoring:** Migrated the internal `ScientificTask` class to a standalone `Task.java` object within the `com.areonedev.autotrack.objects` package, improving modularity and enabling reuse across timeline and agenda components.

- **Timeline Display Logic:** Finalized the **Today-on-Top sorting logic**. The timeline now filters future tasks and displays only **today’s mission** together with historical tasks in **reverse chronological order**.

- **Interactive Task Ledger:** Implemented click interaction for timeline items. Users can now toggle task completion status directly within the Lead Detail board, with immediate visual feedback.

- **Isolated Scroll Window:** Configured the `RecyclerView` to scroll independently within a fixed-height window (**250dp**). This prevents layout shifting and keeps the main Lead Details page visually stable.

- **Inventory Data Integration:** Added structured vehicle dataset files to the **assets** folder (Make, Year, Model, Category), establishing the foundation for upcoming **vehicle selection** and **trade-in estimation** features.

## Issues

- **Task Leakage (Resolved):** Fixed a scheduling logic issue where leads appeared on the Agenda during non-milestone days (e.g., Day 9 and Day 11). The system now strictly follows the intended cadence (Day 1, Day 3, etc.).

- **Scroll Conflict (Resolved):** Removed the global `NestedScrollView` so the internal `RecyclerView` can properly capture touch gestures and scroll independently.

- **XML Syntax Error (Resolved):** Corrected
  `android:text="Task & Activities"`
  to
  `android:text="Task &amp; Activities"`
  to resolve a resource compilation failure.

## Next

- **Direct Contact Actions:** Finalize Intent logic for phone and email icons to support **one-tap calling** and **one-tap emailing** from the Lead Details board.

- **UI Polish:** Add a **Lead Created** milestone at the bottom of the timeline to serve as the historical anchor for engagement tracking.
---
**Date:** April 7, 2026

**Module:** Lead Details UI & Interactive Task Ledger

## Itinerary

- **Task Ledger Integration:** Finalized `LeadDetailsActivity` by integrating the **1-year Scientific Follow-up timeline (Task Ledger)**, enabling structured long-term engagement tracking within the lead profile.

- **Independent Scroll Window:** Configured the `RecyclerView` to support **internal scrolling within the Lead Card**, allowing users to browse the full 1-year history inside a fixed-height window without expanding the entire activity layout.

- **Dynamic Mission Header:** Integrated `ScoringService` into the **Board of Notes** section to display the current high-priority engagement mission (e.g., *Day 3: New Ideas*) as a persistent contextual header.

- **UI Polish:** Add **color-coded timeline status indicators**:
  - **Red:** Overdue or urgent tasks
  - **Gray:** Completed tasks  
    to improve glanceability and interaction efficiency.

## Issues

- **Resource Corruption (Resolved):** Fixed a native crash (`DeadObjectException`) and `libprotobuf` error caused by an invalid UTF-8 character in the `ic_radio_button_unchecked.xml` path data. Corrected the XML encoding to restore Binder stability.

- **Binder Transaction Failure (Resolved):** Resolved an *Invalid Argument* error during `setAdapter()` initialization by ensuring the item layout height was set to `wrap_content` and properly configuring nested scrolling behavior.

- **Layout Inflation Error (Resolved):** Fixed a mismatch between Java view IDs (`ivTimelineStatus`) and XML layout IDs in `TimelineAdapter`, which previously caused a `NullPointerException` during view binding.

## Next

- **Task Completion Logic:** Implement a checkbox listener in `TimelineAdapter` to allow users to mark scientific tasks as **completed**, updating the lead engagement score in real time.

- **Chronological Reversal:** Implemented **latest-first sorting logic**, ensuring that the most recent or upcoming milestones appear at the top of the timeline for immediate visibility.

- **Vehicle Interest Expansion:** Extend the vehicle information display to include a **Trade-In value estimator** and a direct link to the inventory database.

- **Interactive Contact Intents:** Added **click-to-action functionality** for contact fields:
  - **Phone:** Tapping the phone number launches the system dialer using `ACTION_DIAL`.
  - **Email:** Tapping the email address opens the default mail client using `ACTION_SENDTO`, with metadata automatically populated.
---
**Date:** March 31, 2026

**Module:** Business Logic & Scientific Follow-up Algorithm

## Itinerary

- **Centralized Mission Engine:** Refactored the "Scientific Follow-up" logic out of the UI layer and into `ScoringService.java`. This ensures a "Single Source of Truth" where the business layer dictates the mission based on lead data.
- **8-Stage Follow-up Algorithm:** Successfully implemented the time-based cadence:
  - **Short-term:** Day 1 (Gratitude), Day 3 (New Ideas).
  - **Mid-term:** Day 8 (Market Update), Day 15 (Resources), Day 30 (Checking In).
  - **Long-term:** 3 Months (Seasonal), 6 Months (Relationship), 1 Year (Anniversary).
- **Priority Escalation:** Integrated the **48-Hour Reply Rule**. If a lead has a high engagement score but hasn't been contacted in >48 hours, the mission automatically escalates to **🚨 URGENT**.
- **Hybrid Logic:** Merged Lead Stages with the Timeline. Active "Negotiation" or "Test Drive" stages now override generic time-based reminders to keep the sales rep focused on closing.
- **Empty State UI:** Integrated a dynamic "No Missions Scheduled" background in the Calendar Activity that toggles automatically when a date has no tasks.

## Issues

- **NullPointerException (Resolved):** Fixed a crash in `LeadDetailsActivity` caused by an uninitialized `ScoringService`. Centralizing the logic in the service layer resolved the dependency conflict.
- **Date Library Conflict:** Resolved a "Private Access" error by standardizing all calculations to `java.util.Date` and `java.util.concurrent.TimeUnit`, removing conflicting Google Protobuf imports.

## Next

- **Lead Detail Integration:** Update the `LeadDetailsActivity` CardView to display the "Current Mission" alongside existing lead data.
- **Notification System:** Begin development of the `NotificationActivity` and background services to alert sales reps when a "48-Hour Urgent" mission or a "Day 1 Gratitude" task is triggered.

---
**Date:** March 29-30, 2026

**Module:** Calendar & Unified Agenda System Finalized

## Itinerary

- **Unified Daily Agenda:** Merged the previous **Priority** and **General** task windows into a single Daily Agenda view. Integrated `PriorityManager` so all leads are automatically sorted by score (**highest priority first**).

- **7-Day Swippable Navigation:** Implemented `shiftWeek(int days)` logic, enabling users to move forward or backward by exactly **7 days** using header arrow controls.

- **Interactive Week Strip:**
  - **Clickable Days:** Enabled users to tap any day in the 7-day strip to instantly refresh the agenda for the selected date.
  - **Visual Highlighting:** Implemented dual highlighting:
    - **Today:** Light blue circular indicator
    - **Selected Date:** Solid primary blue circular indicator

- **Task Interaction Enhancements:**
  - **Click-to-Detail Navigation:** Restored the Intent bridge in `TaskAdapter`, allowing each agenda task to open `LeadDetailsActivity`.
  - **Dynamic Styling:** Implemented contextual styling:
    - **Gray-out:** CLOSED leads
    - **Red-alert styling:** High-priority leads (`Score > 75`)

- **Type Safety & Refactoring:** Standardized all date handling to `java.util.Date` across `WeekAdapter` and `CalendarActivity`, resolving lambda type-inference issues and constructor mismatch errors.

## Issues

- **RecyclerView Color Leakage ("Red-Leak" Bug):** Fixed a recycling issue where high-priority styling persisted across reused rows. Added explicit color resets in the `onBindViewHolder` `else` block.

- **Layout Constraint Limitation:** Addressed the absence of a native `simple_list_item_3` layout by combining **Lead Name** and **Score** into a single formatted display string.

## Next

- **Lead Details Display Fix:** Verify backend unpacking logic to ensure the `LEAD_OBJECT` passed from the Calendar module is correctly rendered in `layoutViewMode` within `activity_lead_details.xml`.

- **Database Sanitization:** Perform a full reset of the SQLite `Leads` table to ensure all records conform to the `dd/MM/yyyy` scheduling format required by Calendar queries.
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