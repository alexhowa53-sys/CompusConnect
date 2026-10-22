# CampusConnect — Comprehensive Project Summary & Architecture Report

**Project Name:** CampusConnect  
**Framework:** Android (Kotlin & Jetpack Compose, Material 3)  
**Backend:** Firebase Authentication & Firebase Firestore  
**Architecture:** Single-Activity (`MainActivity`), Centralized State Routing (`MainApp`), Repository Pattern (`CampusRepository`)

---

## 1. Design System & Visual Identity
* **Color Palette**: Warm Cream (`#FAF7F0`) background, Deep Navy (`#132A4C`) primary brand color, Amber/Gold (`#F2A93B`) accents, Sage Green (`#CFE0D6`) for free tags, and Ink (`#1C1C1C`) body text.
* **Typography**: Serif display headings paired with clean sans-serif UI/body text.
* **Ticket Stub Motif**: Event cards designed as physical ticket stubs with left-side date blocks, circular perforation notches, and category-specific date block colors (Amber for Music, Navy for Sports, Dark Green for Career/Community).

---

## 2. Implemented Screens & Features

### A. Core Student Experience
1. **Splash Screen (`SplashScreen.kt`)**: Branded launch screen with the "C." logo and loading indicators.
2. **Home Screen (`HomeScreen.kt`)**: Event feed with scrollable filter chips (*All*, *This week*, *Music*, *Sports*, *Career*, *Free*) and real-time live search filtering.
3. **Search Screen (`SearchScreen.kt`)**: Dedicated explore screen with eyebrow label (*"EXPLORE CAMPUS"*), large serif headline, real-time query filtering, and result counters.
4. **Event Details Screen (`EventDetailsScreen.kt`)**: Immersive header banner displaying custom poster artwork, event metadata, description (*"About this event"*), tags, and dynamic bottom bars (showing price and "Buy Ticket" for paid events, or "Admission: Free" without buttons for free events).
5. **Ticket Confirmation Screen (`TicketConfirmationScreen.kt`)**: Success celebration screen (*"You're on the list!"*) with barcode/QR reference code and quick navigation back home or to tickets.
6. **My Tickets Screen (`TicketsScreen.kt`)**: Segmented toggle between **Purchased** passes (Ready) and **Saved** bookmarked events.
7. **Profile Screen (`ProfileScreen.kt`)**: User session management displaying initials avatar, account type, preferences, and admin dashboard switching.

### B. Authentication & Admin Experience
8. **Login & Auth Screen (`LoginAuthScreen.kt`)**: Redesigned based on professional UI templates ("Welcome back" / "Sign up Account"). Includes password visibility toggles, **OTP Verification** step on sign-up, and interactive **Google & Apple** social sign-in buttons.
9. **Admin Dashboard Screen (`AdminDashboardScreen.kt`)**: Management interface for administrators (*"Good morning, Maya"*, *"STUDENT RADIO · ADMIN"*) featuring metric summary cards (Tickets, Check-ins, Revenue), active event list, and **event deletion**.
10. **Create Event Screen (`CreateEventScreen.kt`)**: Form allowing admins to publish new campus events, configure title, date, location, category chips (wrapped with `FlowRow`), pricing, rich description placeholders, and **local phone gallery poster uploads** or custom image URLs.

---

## 3. Backend & Data Architecture
* **`CampusRepository.kt`**: Bridges the app with Firebase Auth and Firebase Firestore. Handles asynchronous real-time fetching (`fetchEvents`), publishing (`addEvent`), and deletion (`deleteEvent`), backed by robust offline simulation fallback to ensure seamless local testing and previewing.
* **`MainApp.kt`**: Coordinates navigation state and hoisted event lists across all screens using Jetpack Compose `Crossfade` for buttery-smooth page and tab transitions.

---

## 4. Testing & Documentation
* **Unit Tests (`src/test`)**: 
  * `AuthHelperTest.kt`: Validates login rules for admin and student accounts.
  * `EventFilterTest.kt`: Validates real-time search and category/pricing filters (**7 unit tests passing 100%**).
* **UI Instrumentation Tests (`src/androidTest`)**:
  * `AppNavigationTest.kt`: Validates bottom navigation switching.
  * `EventFlowTest.kt`: Validates event card click navigation and ticket booking.
  * `AdminDashboardTest.kt`: Validates admin login, metrics, and event creation navigation.
* **Documentation**:
  * `TESTING.md`: Technical guide on test architecture and execution.
  * `TEST_CASES.md`: Formal test case documentation for academic assessment.
  * `TEST_CASES.docx`: Formatted Microsoft Word document generated for hard/soft copy submission.
