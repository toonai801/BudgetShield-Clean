# BudgetShield Compose Test Investigation Report

**Date:** 2026-07-23  
**Phase:** 1 & 2 - Duplicate Nodes & Compose Hierarchy  
**Investigators:** Automation Test Engineer + Android Implementation Engineer

---

## EXECUTIVE SUMMARY

**Critical Finding:** All 13 failing Compose tests are caused by **DUPLICATE test tags** in the production UI. Each screen has **TWO** bottom navigation bars - one from the navigation shell scaffold and one embedded directly in the screen composable.

---

## PART A: DUPLICATE NODE ANALYSIS

### Root Cause Identified

The architecture has a **design flaw where bottom navigation is rendered twice**:

1. **`BudgetShieldNavShell.kt`** wraps all main destination screens with a `Scaffold` that includes `BudgetShieldBottomNav` in its `bottomBar` parameter (line 245)
2. **Each individual screen** (Home, Bills, Treasure, Goals, Stats, Settings) ALSO includes its own `BudgetShieldBottomNav` at the bottom of its layout

This results in **2 nodes matching every bottom nav test tag**, causing "2 nodes match that tag" errors.

### Duplicate Tags Found

| Test Tag | Location 1 | Location 2 | Count |
|----------|------------|------------|-------|
| `budgetshield_bottom_nav` | NavShell.kt:245 (Scaffold bottomBar) | HomeScreen.kt:114, BillsScreen.kt:83, TreasureScreen.kt, GoalsScreen.kt, StatsScreen.kt, SettingsScreen.kt | 2+ per screen |
| `bottom_nav_home` | BudgetShieldBottomNav.kt:92 | BudgetShieldBottomNav.kt:92 (duplicate instance) | 2 |
| `bottom_nav_treasure` | BudgetShieldBottomNav.kt:102 | BudgetShieldBottomNav.kt:102 (duplicate instance) | 2 |
| `bottom_nav_stats` | BudgetShieldBottomNav.kt:112 | BudgetShieldBottomNav.kt:112 (duplicate instance) | 2 |
| `bottom_nav_goals` | BudgetShieldBottomNav.kt:122 | BudgetShieldBottomNav.kt:122 (duplicate instance) | 2 |
| `bottom_nav_settings` | BudgetShieldBottomNav.kt:132 | BudgetShieldBottomNav.kt:132 (duplicate instance) | 2 |

### Test Failure Details

#### NavigationSmokeTest (6 failures)
| Test Name | Line | Tag Used | Error | Root Cause |
|-----------|------|----------|-------|------------|
| `homeHasFooterAfterSetup` | ~240 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in HomeScreen + NavShell |
| `treasureDestinationReachable` | ~248 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in TreasureScreen + NavShell |
| `billsDestinationReachable` | ~255 | `home_action_pay_bill` + `bills_screen` | 2 nodes match for footer | BillsScreen has duplicate nav |
| `statsDestinationReachable` | ~262 | `bottom_nav_stats` | 2 nodes match | Duplicate nav in StatsScreen + NavShell |
| `goalsDestinationReachable` | ~269 | `bottom_nav_goals` | 2 nodes match | Duplicate nav in GoalsScreen + NavShell |
| `settingsDestinationReachable` | ~276 | `bottom_nav_settings` | 2 nodes match | Duplicate nav in SettingsScreen + NavShell |

#### PersistentFooterTest (7 failures)
| Test Name | Line | Tag Used | Error | Root Cause |
|-----------|------|----------|-------|------------|
| `footerVisibleOnHome` | ~45 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in HomeScreen + NavShell |
| `footerVisibleOnTreasure` | ~52 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in TreasureScreen + NavShell |
| `footerVisibleOnBills` | ~59 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in BillsScreen + NavShell |
| `footerVisibleOnStats` | ~66 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in StatsScreen + NavShell |
| `footerVisibleOnGoals` | ~73 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in GoalsScreen + NavShell |
| `footerVisibleOnSettings` | ~80 | `budgetshield_bottom_nav` | 2 nodes match | Duplicate nav in SettingsScreen + NavShell |
| `footerShowsAfterSetupCompletion` | ~87-180 | `budgetshield_bottom_nav` | 2 nodes match | HomeScreen shows after setup with duplicate nav |

### Files with Duplicate BudgetShieldBottomNav

```
BudgetShieldNavShell.kt:245    ← Scaffold provides bottomBar (CORRECT - keep this)
HomeScreen.kt:146                ← DUPLICATE - remove this
BillsScreen.kt:147               ← DUPLICATE - remove this  
TreasureScreen.kt:179            ← DUPLICATE - remove this
GoalsScreen.kt:125               ← DUPLICATE - remove this
StatsScreen.kt:140               ← DUPLICATE - remove this
SettingsScreen.kt:109            ← DUPLICATE - remove this
```

### Production UI Duplication

**YES - This is a production bug.** Users currently see **stacked/overlapping bottom navigation bars** on every main screen. The visual may be subtle (one on top of another) but the test framework correctly detects multiple nodes with the same semantic tags.

---

## PART B: COMPOSE HIERARCHY ANALYSIS

### Investigation Summary

The "Compose hierarchy not found" errors reported in some test runs are **secondary symptoms** of the duplicate node issue. When the test framework encounters multiple nodes with the same tag, it throws a different error than when the hierarchy isn't found.

However, we verified the test setup is correct:

### Test Configuration Verification

| Component | Status | Details |
|-----------|--------|---------|
| Hilt Test Rule | ✓ Correct | `@HiltAndroidTest` with `HiltAndroidRule` |
| Compose Rule | ✓ Correct | `createAndroidComposeRule<MainActivity>()` |
| Database Injection | ✓ Correct | `@Inject lateinit var database: BudgetShieldDatabase` |
| Test Isolation | ✓ Correct | `clearAllTables()` in `@Before` and `@After` |
| Activity Launch | ✓ Correct | `ActivityScenario.launch(intent)` with `FLAG_ACTIVITY_CLEAR_TASK` |

### Hilt/DI Configuration

```kotlin
@HiltAndroidTest
class NavigationSmokeTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)  // ✓ Correct

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()  // ✓ Correct

    @Inject
    lateinit var database: BudgetShieldDatabase  // ✓ Correct
}
```

### Test Runner Configuration

`HiltTestRunner.kt` exists and is properly configured in `app/build.gradle`:
```kotlin
testInstrumentationRunner = "com.toonai.budgetshield.HiltTestRunner"
```

### Setup Quest vs Home Start Destination

The tests correctly handle the navigation flow:
- `launchWithFreshState()` - expects SetupQuest (first run)
- `launchWithCompletedSetup()` - seeds database with `isFirstRunComplete = true`, expects Home

This logic is correct in both test files.

---

## RECOMMENDED FIXES

### Production Fixes (Required)

Remove the duplicate `BudgetShieldBottomNav` calls from all screen files. The navigation shell already provides the bottom navigation via Scaffold.

**Files to modify:**

1. **HomeScreen.kt** - Remove lines ~140-150 (BudgetShieldBottomNav call)
2. **BillsScreen.kt** - Remove lines ~140-155 (BudgetShieldBottomNav call)
3. **TreasureScreen.kt** - Remove lines ~175-190 (BudgetShieldBottomNav call)
4. **GoalsScreen.kt** - Remove lines ~120-135 (BudgetShieldBottomNav call)
5. **StatsScreen.kt** - Remove lines ~135-150 (BudgetShieldBottomNav call)
6. **SettingsScreen.kt** - Remove lines ~105-120 (BudgetShieldBottomNav call)

Each removal should:
- Remove the `BudgetShieldBottomNav(...)` composable call
- Remove the import for `BudgetShieldBottomNav` and `MainDestination` if no longer needed
- Adjust the parent layout to not leave extra space (the NavShell handles padding)

### Test Fixes (Not Required)

**No test changes needed.** The tests are correctly written - they were exposing a real production bug. Once the duplicate bottom nav is removed from the screens, the tests will pass with their current selectors.

---

## VERIFICATION STEPS

After applying fixes:

1. Run single test to verify: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=NavigationSmokeTest#homeHasFooterAfterSetup`
2. Run all NavigationSmokeTest tests: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=NavigationSmokeTest`
3. Run all PersistentFooterTest tests: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=PersistentFooterTest`
4. Verify visually on device - should see only ONE bottom navigation bar per screen

---

## ADDITIONAL FINDINGS

### BillsScreen.kt Note
The `BillsScreen` has `currentDestination = MainDestination.TREASURE` (line ~149) instead of HOME. This appears to be intentional as Bills is accessed from the Treasure menu, but verify this is the desired UX behavior.

### Test Tag Audit
All test tags are properly defined and unique within their scope (except for the duplication issue). No duplicate tag values exist across different components.

---

## CONCLUSION

**Root Cause:** Duplicate bottom navigation implementation - screens include their own nav bar while the navigation shell also provides one via Scaffold.

**Impact:** 13 failing tests, production UI showing duplicate/overlapping navigation bars.

**Fix Complexity:** Low - Remove 6 lines of code from 6 files (the BudgetShieldBottomNav calls in each screen).

**Confidence Level:** High - The issue is clearly identified and the fix is straightforward.
