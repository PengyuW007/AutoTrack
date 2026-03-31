# AutoTrack: Automotive Personal CRM

AutoTrack is a specialized Customer Relationship Management (CRM) application designed for automotive sales professionals. Unlike traditional static CRMs, AutoTrack acts as a **Scientific Sales Co-pilot**, utilizing data-driven algorithms to dictate the "Mission" for every lead based on engagement and timing.
## ✨ Key Features
- **Scientific Follow-up Algorithm:** A centralized business logic engine that assigns specific "Missions" (e.g., Day 1 Gratitude, Day 8 Market Update, 1-Year Anniversary) based on the lead's position in the sales funnel.
- **Intelligent Lead Scoring:** Utilizes a priority-based algorithm to calculate a lead's importance (0-100+) based on budget, interest level, and engagement history.
- **48-Hour Safety Net:** An automated escalation trigger that flags leads who have interacted but haven't been addressed within the critical 48-hour window.
- **Unified Daily Agenda:** A date-categorized interface that merges scheduled tasks with AI-driven follow-up suggestions.
- **Persistence:** Local SQLite database integration ensuring data is available offline and persists across sessions.
- ## 🛠 Tech Stack
- **Language:** Java / Android SDK
- **Architecture:** Layered Architecture (Presentation, Business, Persistence, Objects)
- **Database:** SQLite (via custom Data Access Objects)
- **UI Components:** RecyclerView with Multi-View Types, Material Design 3

## 📖 Core Modules & Big Stories
### 📋 Leads Activity (The "Daily Dashboard")
**Purpose:** This is the primary entry point for the sales consultant.
- **The Story:** Upon opening the app, the user sees a prioritized list of leads. Unlike a standard list, AutoTrack runs a scoring algorithm to highlight which leads require immediate attention. This allows the consultant to identify "High Priority" leads from today and follow up with "Yesterday's" inquiries based on their calculated value, ensuring no high-potential customer is missed.

### 📅 Calendar & Tasks
**Purpose:** To manage time-sensitive commitments and priority follow-ups.
- **The Story:** This module allows the user to visualize their schedule. By binding `FollowUpDates` and `Priority Scores` to a calendar view, the consultant can plan their day around test drives and calls, focusing their energy on the leads the system has flagged as most likely to convert.

### 🔔 Notifications & Events
**Purpose:** Real-time awareness of sales milestones and priority shifts.
- **The Story:** A centralized hub that alerts the user to upcoming tasks. Rather than simple chronological notifications, it provides a "Priority Strip" that alerts the sales rep when a lead's status changes or when a high-scoring lead is due for contact.
## 📂 Project Structure
app/src/main/java/com/ areonedev/ autotrack/  

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