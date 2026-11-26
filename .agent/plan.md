# Project Plan

Act as a Senior Android Developer. Build a gesture-driven floating formula reference app in Kotlin using Jetpack Compose and Android's WindowManager API so floating topic bubbles can hover over other apps (like calculators, PDF readers, or browsers).

### Tech Stack & Architecture
- **Language & UI:** Kotlin, Jetpack Compose, Material 3 (Dark Mode default).
- **Overlay System:** Android `WindowManager` with `TYPE_APPLICATION_OVERLAY` running inside a Foreground Service.
- **Database:** Room Database for storing topics, formulas, and favorite status (pre-populated with sample Physics & Calculus data).
- **Math Rendering:** Render LaTeX formulas cleanly using a lightweight local KaTeX `WebView` or `JLatexMath` wrapped in a Compose `AndroidView`.
- **Architecture:** MVVM with clean separation between the main configuration app and the `OverlayService`.

### Required Permissions & Android 14+ Setup
- Configure `AndroidManifest.xml` with `SYSTEM_ALERT_WINDOW`, `FOREGROUND_SERVICE`, and `FOREGROUND_SERVICE_SPECIAL_USE`.
- Include a clean onboarding/permission screen in `MainActivity` that checks `Settings.canDrawOverlays(context)` and guides the user to grant "Display over other apps" permission before starting the service.

### Core Gesture & Overlay Mechanics
1. **Edge-Swipe Trigger:** Attach a thin, invisible touch-interceptor view along the right edge of the screen via `WindowManager`. Swiping left from this strip reveals a vertical tray of up to 6 favorited topic bubbles.
2. **Draggable Floating Bubble:** Tapping a topic in the tray spawns a persistent circular floating bubble (`ComposeView` attached to `WindowManager`). Support smooth touch dragging by updating `WindowManager.LayoutParams.x` and `y`, and use a `ValueAnimator` to snap the bubble to the nearest horizontal screen edge on release.
3. **Expandable Formula Card:** Tapping the floating bubble toggles a floating, frosted-glass-style card displaying all LaTeX formulas for that topic, along with a collapsible variable legend (e.g., $F = ma$). Tapping outside or tapping the bubble again collapses it.
4. **Drag-to-Dismiss:** Dragging the bubble toward the bottom center reveals a red "X" target zone; releasing the bubble over it removes the overlay view cleanly.

### Main App Dashboard
- A Compose UI where users can toggle the overlay service on/off, create/edit custom topics and LaTeX formulas, and star up to 6 topics to appear in their quick-swipe tray.

### Implementation Guidelines
- Properly attach a custom `LifecycleOwner` and `SavedStateRegistryOwner` to any `ComposeView` added to `WindowManager` so Compose state and animations work without crashing.
- Provide the complete folder structure, `build.gradle.kts` dependencies, `AndroidManifest.xml`, and full Kotlin code for the `OverlayService`, Room entities/DAO, and main UI screens.

## Project Brief

Here is the project brief for the Floating Formula Reference App, focusing on the core minimum viable product (MVP) requirements.

# Project Brief: Floating Formula Reference App

## Features
1. **Floating Overlay & Edge-Swipe Tray:** A gesture-triggered edge-swipe tray that reveals favorite topics. Tapping a topic spawns a draggable, snap-to-edge floating bubble that hovers persistently over other apps.
2. **Interactive Formula Cards:** Expandable, frosted-glass-style floating cards that display cleanly rendered LaTeX formulas and collapsible variable legends when the topic bubble is tapped.
3. **Dashboard & Configuration:** A main app dashboard where users can toggle the background service, handle necessary permissions, create/edit formulas, and select up to 6 topics for the quick-swipe tray.
4. **Drag-to-Dismiss:** An intuitive removal mechanism where dragging the active bubble towards a bottom-center red "X" zone smoothly dismisses the overlay.

## High-Level Technical Stack
- **Language & UI:** Kotlin, Jetpack Compose, Material 3 (Dark Mode default).
- **Navigation & Adaptive Strategy:** Jetpack Navigation 3 (state-driven) and the Compose Material Adaptive library for all main app layouts.
- **Architecture & Concurrency:** MVVM (Model-View-ViewModel) utilizing Kotlin Coroutines and StateFlow for robust, reactive data handling.
- **System Overlay & Background Work:** Android `WindowManager` API (`TYPE_APPLICATION_OVERLAY`) running inside a Foreground Service. Includes a custom `LifecycleOwner` and `SavedStateRegistryOwner` attached to the `ComposeView` to maintain Compose state and animations within the overlay.
- **Database / Persistence:** Room Database for storing topics, LaTeX formula strings, and favorite statuses (as explicitly required by the design).
- **Math Rendering:** KaTeX running in a lightweight local `WebView` or `JLatexMath` embedded via a Compose `AndroidView`.

*(Note: The UI Design Image section has been omitted as an image generation tool is not available in the current environment.)*

## Implementation Steps
**Total Duration:** 50m 44s

### Task_1_SetupDatabaseAndDashboard: Setup Room database, pre-populate sample data, and build Main Dashboard UI to manage topics, request overlay permissions, and toggle the foreground service.
- **Status:** COMPLETED
- **Updates:** Task 1 is completed.
- **Acceptance Criteria:**
  - Room DB working with Topics/Formulas
  - Main UI shows topics and allows toggling favorites
  - Permission flow for 'Draw over other apps' is implemented
- **Duration:** 8m 20s

### Task_2_ForegroundServiceAndEdgeTray: Implement Foreground Service and WindowManager setup with custom LifecycleOwner. Create the edge-swipe trigger and the sliding tray UI showing favorite topics.
- **Status:** COMPLETED
- **Updates:** Completed Task 2: Implement Foreground Service and WindowManager setup with custom LifecycleOwner. Create the edge-swipe trigger and the sliding tray UI showing favorite topics. Service runs in foreground. Custom LifecycleOwner/SavedStateRegistryOwner implemented for WindowManager. Edge swipe reveals a Compose tray containing favorited topics.
- **Acceptance Criteria:**
  - Service runs in foreground
  - Custom LifecycleOwner/SavedStateRegistryOwner implemented for WindowManager
  - Edge swipe reveals a Compose tray containing favorited topics
- **Duration:** 3m 31s

### Task_3_FloatingBubbleAndDismiss: Implement the draggable floating topic bubble that spawns from the tray, snaps to screen edges, and the drag-to-dismiss functionality with the bottom center 'X' zone.
- **Status:** COMPLETED
- **Updates:** Completed Task 3: Implement the draggable floating topic bubble that spawns from the tray, snaps to screen edges, and the drag-to-dismiss functionality with the bottom center 'X' zone. Tapping tray spawns bubble. Bubble can be dragged and snaps to edge. Dragging to bottom X dismisses the bubble.
- **Acceptance Criteria:**
  - Tapping tray spawns bubble
  - Bubble can be dragged and snaps to edge
  - Dragging to bottom X dismisses the bubble
- **Duration:** 3m 13s

### Task_4_FormulaCardAndLatex: Implement the expandable formula card triggered by tapping the bubble, featuring frosted-glass style and LaTeX rendering using a WebView with local KaTeX (or JLatexMath).
- **Status:** COMPLETED
- **Updates:** Fixed the layout issues with the formula card. Card is properly bounded to the screen and LaTeX is not cropped.
- **Acceptance Criteria:**
  - Tapping bubble toggles the formula card
  - LaTeX formulas render correctly
  - Collapsible variable legend works
- **Duration:** 16m 42s

### Task_5_RunAndVerify: Run and Verify. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Verification successful. No crashes, all features function correctly, and critical UI issues are resolved.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 2m 53s

### Task_6_SettingsAndBugFix: Add Settings screen to Main Dashboard with overlay Start/Stop buttons and a Y-offset slider for the side gesture position. Persist settings and dynamically update the edge trigger in OverlayService. Fix drag-to-dismiss logic to reliably detect the bottom 'X' hit zone.
- **Status:** COMPLETED
- **Updates:** Task 6 completed. 1. Added Settings screen with Start/Stop buttons and Y-offset slider for the side gesture position. 2. Persisted settings with DataStore and dynamically updated the edge trigger in OverlayService. 3. Fixed the drag-to-dismiss logic by properly calculating the hit bounds for the 'X' target zone.
- **Acceptance Criteria:**
  - Settings UI with Start/Stop buttons and Y-offset slider
  - Edge trigger position updates dynamically based on slider
  - Drag-to-dismiss correctly removes bubble
- **Duration:** 4m 18s

### Task_7_RunAndVerify: Run and Verify. Instruct critic_agent to verify application stability, confirm drag-to-dismiss and settings updates function perfectly, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Task 7 completed successfully. Critic agent verified app stability, successful build, and correct functioning of the new Settings UI, Y-offset slider, and dismiss mechanics.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 11m 47s

