# CampusConnect — Testing Documentation

This document outlines the testing architecture, test suite breakdown, and execution instructions for the **CampusConnect** Android application.

---

## 1. Testing Strategy & Architecture

CampusConnect follows a rigorous, multi-layered testing strategy designed to ensure high reliability across business logic, state management, and Jetpack Compose user interfaces:

* **Unit Tests (`src/test`)**: Fast running, isolated tests using **JUnit 4** to verify pure business logic, such as authentication rules, credential validation, and event search/category filtering.
* **Compose UI Instrumentation Tests (`src/androidTest`)**: Integration and UI tests leveraging **Jetpack Compose UI Test (`createComposeRule`)** and **Espresso** to verify user interaction flows, bottom navigation switching, ticket booking journeys, and admin management workflows on device/emulator.

---

## 2. Test Suite Breakdown

### A. Unit Tests (`src/test/java/com/example/compusconnect/`)

#### 1. `AuthHelperTest.kt`
* **Purpose**: Validates authentication rules and role assignment.
* **Key Scenarios Tested**:
  * Admin login success with valid admin credentials (`admin@compusconnect.com` / `admin123`) returning `UserRole.ADMIN`.
  * Student login success with arbitrary valid credentials returning `UserRole.STUDENT`.
  * Invalid/blank login failure returning `null`.

#### 2. `EventFilterTest.kt`
* **Purpose**: Validates real-time search filtering and category constraints.
* **Key Scenarios Tested**:
  * Keyword search matching event titles, locations, and descriptions.
  * Filtering events by category (`MUSIC`, `SPORTS`, `CAREER`, `COMMUNITY`).
  * Filtering events by pricing (`Free` vs. `Paid`).

---

### B. Compose UI Instrumentation Tests (`src/androidTest/java/com/example/compusconnect/`)

#### 1. `AppNavigationTest.kt`
* **Purpose**: Validates core bottom navigation bar tab switching.
* **Key Scenarios Tested**:
  * Tapping between *Home*, *Search*, *Tickets*, and *Profile* tabs correctly updates the active screen state.

#### 2. `EventFlowTest.kt`
* **Purpose**: Validates end-to-end event discovery and ticketing flow.
* **Key Scenarios Tested**:
  * Clicking an event card on the Home or Search screen opens the **Event Details** screen with correct poster artwork and information.
  * Tapping "Buy Ticket" / completing the checkout flow opens the **Ticket Confirmation** ("You're on the list!") screen.

#### 3. `AdminDashboardTest.kt`
* **Purpose**: Validates administrator workflows.
* **Key Scenarios Tested**:
  * Admin Dashboard rendering metrics (Tickets, Check-ins, Revenue).
  * Clicking "+ Create a new event" navigates to the **Create Event** form.
  * Deleting an event from the admin event list updates the global state.

---

## 3. How to Run Tests

### In Android Studio
1. Open the project in Android Studio.
2. **Run Unit Tests**: 
   * Navigate to `app/src/test`.
   * Right-click the test directory or specific test file and select **Run**.
3. **Run UI Instrumentation Tests**:
   * Connect an Android emulator or physical device.
   * Navigate to `app/src/androidTest`.
   * Right-click and select **Run**.

### Via Gradle (Command Line)
* **Execute Unit Tests**:
  ```bash
  ./gradlew test
  ```
* **Execute Instrumentation UI Tests** (requires connected device/emulator):
  ```bash
  ./gradlew connectedAndroidTest
  ```

---

## 4. Jetpack Compose Testing Best Practices

* **State Hoisting**: Screens and components hoist state (`events`, `userSession`, `currentRoute`) up to `MainApp.kt`, making them easily testable with mock data and callbacks.
* **Semantics & Test Tags**: Compose UI tests locate elements using semantic matchers (`hasText`, `hasClickAction`, `hasContentDescription`) matching our accessibility and UI specifications.
