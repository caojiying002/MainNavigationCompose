# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is an Android application built with **Jetpack Compose** using a **tab-based navigation architecture**. The app features a main screen with bottom navigation tabs (首页/Home, 信息/Message, 暗巷/Alley, 商家/Merchant, 我的/Profile) and detailed screens for each section.

## Architecture
- **Navigation**: Uses Jetpack Navigation Compose with a global `LocalNavController` for navigation across the app
- **Dependency Injection**: Hilt for dependency injection with `@HiltAndroidApp` and `@AndroidEntryPoint`
- **UI Framework**: 100% Jetpack Compose with Material 3 design system
- **State Management**: ViewModels with Hilt integration (`hiltViewModel()`)
- **Screen Structure**: 
  - `MainScreen.kt` - Container with bottom navigation using `CustomBottomBar`
  - Individual tab screens under `ui/screen/` directories (home, message, alley, merchant, profile)
  - Detail screens for navigation from main tabs

## Key Files
- `navigation/AppNavigation.kt` - Defines routes and navigation structure using sealed class `Screen`
- `ui/screen/main/MainScreen.kt` - Main container with tab switching and lazy loading
- `ui/components/CustomBottomBar.kt` - Custom animated bottom navigation bar
- `App.kt` - Application class with `@HiltAndroidApp`

## Commands

### Build Commands
```bash
./gradlew build                    # Build and test the project
./gradlew assembleDebug           # Build debug APK
./gradlew assembleRelease         # Build release APK
./gradlew installDebug            # Install debug build to connected device
```

### Testing Commands
```bash
./gradlew test                    # Run all unit tests
./gradlew testDebugUnitTest       # Run debug unit tests
./gradlew connectedAndroidTest    # Run instrumented tests on connected devices
./gradlew connectedCheck          # Run all device checks
```

### Code Quality
```bash
./gradlew lint                    # Run lint checks
./gradlew lintDebug              # Run lint for debug variant
./gradlew lintFix                # Apply safe lint suggestions
./gradlew check                   # Run all checks (lint + tests)
```

### Development
```bash
./gradlew clean                   # Clean build artifacts
```

## Navigation System
The app uses a centralized navigation approach:
- `Screen` sealed class defines all routes with parameter support
- `LocalNavController` provides global navigation access
- Main tabs use lazy loading - screens are only created when first visited
- Detail screens are separate composables in the main navigation graph

## State Management
- Each screen has its own ViewModel injected via `hiltViewModel()`
- `isFirstTimeVisible` parameter controls initialization logic for tab screens
- Navigation state is managed through `rememberSaveable` for tab selection

## UI Conventions
- Material 3 design system
- Custom animated components (see `CustomBottomBar` for reference)
- Edge-to-edge display with proper padding handling
- Consistent color scheme defined in theme files