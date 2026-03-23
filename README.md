# AutoTrack: Automotive Personal CRM

AutoTrack is a specialized Customer Relationship Management (CRM) application designed for automotive sales professionals. It helps sales consultants track leads, manage vehicle interests, and schedule follow-ups through a clean, date-categorized interface.

## 🚀 Current Status: Phase 1 (Core Infrastructure)
The project has successfully moved from the architectural design phase to a functional data-driven UI. The persistence layer is fully integrated with the presentation layer.

## ✨ Key Features
- **Smart Lead Grouping:** Automatically categorizes leads by creation date (Today, Yesterday, or specific dates).
- **Lead Management:** Track customer contact info, budget, and sales stage (New, Visited, Negotiation, etc.).
- **Vehicle Interest Tracking:** (In Progress) Mapping specific vehicle models to potential buyers.
- **Persistence:** Local SQLite database integration for offline data management.

## 🛠 Tech Stack
- **Language:** Java / Android SDK
- **Architecture:** Layered Architecture (Presentation, Business, Persistence, Objects)
- **Database:** SQLite (via custom Data Access Objects)
- **UI Components:** RecyclerView with Multi-View Types, Material Design 3

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
    - *Note: On first run, the app seeds dummy data (e.g., Pengyu, Irfan, Anna) to demonstrate the grouping logic.*

## 📅 Roadmap
- [x] Database & UI Connection
- [x] Date-based Lead Grouping (Today/Yesterday logic)
- [ ] **Next:** Structural Refactor (Vehicle Object)
- [ ] **Next:** Search functionality (Filter by Name/Phone)
- [ ] **Next:** "Add Lead" Story with Floating Action Button
- [ ] **Future:** Calendar View for Follow-up Reminders

---
*Developed by AreOneDev*