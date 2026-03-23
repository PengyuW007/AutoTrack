# AutoTrack: Automotive Personal CRM

AutoTrack is a specialized Customer Relationship Management (CRM) application designed for automotive sales professionals. It helps sales consultants track leads, manage vehicle interests, and schedule follow-ups through a clean, date-categorized interface.

## ✨ Key Features
- **Smart Lead Grouping:** Automatically categorizes leads by creation date (Today, Yesterday, or specific dates) for better daily workflow.
- **Lead Management:** Track customer contact info, budget, and sales stage (New, Visited, Negotiation, etc.).
- **Vehicle Interest Tracking:** Mapping specific vehicle models and trims to potential buyers.
- **Persistence:** Local SQLite database integration ensuring data is available offline and persists across sessions.

## 🛠 Tech Stack
- **Language:** Java / Android SDK
- **Architecture:** Layered Architecture (Presentation, Business, Persistence, Objects)
- **Database:** SQLite (via custom Data Access Objects)
- **UI Components:** RecyclerView with Multi-View Types, Material Design 3

## 📖 Core Modules & User Stories

### 📋 Leads Activity (The "Daily Dashboard")
**Purpose:** This is the primary entry point for the sales consultant.
- **The Story:** Upon opening the app, the user sees a prioritized list of leads grouped by their creation date. This allows the consultant to immediately identify "Hot Leads" from today and follow up with "Yesterday's" inquiries, ensuring no potential customer is forgotten in the sales funnel.

### 📅 Calendar & Tasks
**Purpose:** To manage time-sensitive commitments.
- **The Story:** This module allows the user to visualize their schedule. By binding `FollowUpDates` to a calendar view, the consultant can plan their day around test drives, showroom visits, and scheduled calls, transitioning from a reactive to a proactive sales approach.

### 🔔 Notifications & Events
**Purpose:** Real-time awareness of sales milestones.
- **The Story:** A centralized hub that alerts the user to upcoming tasks and system events. It provides a quick-scan "Event Strip" that displays titles and timeframes, allowing the consultant to stay informed without leaving the main workflow.

## 📂 Project Structure
text app/src/main/java/com/ areonedev/ autotrack/  

├── business/ # Logic for accessing and filtering leads 

├── objects/ # Data models (Lead, Vehicle) 

├── persistence/ # SQLite Database and DAO implementation 

└── presentation/ # Activities and Adapters (UI)

## ⚙️ Installation & Setup
1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Gradle files.
4. Run the app on an emulator (API 34+ recommended).
    - *Note: On first run, the app seeds dummy data to demonstrate the grouping logic.*

## 📅 Roadmap
- [x] Database & UI Connection
- [x] Date-based Lead Grouping (Today/Yesterday logic)
- [ ] Structural Refactor (Vehicle Object implementation)
- [ ] Search functionality (Filter by Name/Phone)
- [ ] "Add Lead" Story with Floating Action Button
- [ ] Calendar View for Follow-up Reminders

---
*Developed by AreOneDev*