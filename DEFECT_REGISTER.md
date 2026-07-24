# BudgetShield Consolidated Defect Register

## Register Metadata
- **Created:** 2026-07-23
- **Audits Merged:** Architect, Design, QA, Data
- **Total Defects:** 71
- **Blocking Release:** 9

---

## Legend
- **ID Format:** {AGENT}-{###} (e.g., QA-001, ARCH-042)
- **Severity:** CRITICAL | HIGH | MEDIUM | LOW
- **Status:** OPEN | IN_PROGRESS | FIXED | VERIFIED | CLOSED

---

## Critical Defects (Blocking Release)

### QA-001: Setup Quest Next Button Non-Functional
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Fresh install app
2. Launch Setup Quest Chapter 1
3. Enter valid amount in Cash on Hand field (e.g., "1500")
4. Tap "Next" button

**Expected:** Navigate to Chapter 2  
**Actual:** No response, remains on Chapter 1

**Root Cause Analysis:**
- UI dump shows `clickable="false"` despite enabled state
- State flow emission not triggering recomposition
- Navigation callback may not be invoked
- Potential validation gate blocking

**Required Fix:**
- Inspect SetupQuestScreen.kt navigation footer (line ~520)
- Verify SetupQuestViewModel.goToNextChapter() execution
- Check state emission and recomposition
- Verify navigation callback chain

**Regression Test:**
- Fresh install → Enter Chapter 1 data → Tap Next → Chapter 2 appears

**Verification Status:** PENDING

**Evidence:** `/qa-evidence/screenshot_ch1_input.png`, `/qa-evidence/final_dump.xml` showing `clickable="false"`

---

### ARCH-001: Chapter 2 Date Field Non-Responsive
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Reach Setup Quest Chapter 2 (after QA-001 fix)
2. Tap directly on the pay date input field (not calendar icon)

**Expected:** Date picker opens  
**Actual:** Field shows readOnly state with no click handler

**Root Cause Analysis:**
- TextField has `readOnly = true` (line ~330-350)
- No `clickable` modifier attached to field
- User must tap calendar icon only

**Required Fix:**
```kotlin
TextField(
    value = chapter2State.payDate,
    onValueChange = { },
    readOnly = true,
    modifier = Modifier
        .fillMaxWidth()
        .clickable { showDatePicker = true },  // ADD
    // ...
)
```

**Regression Test:**
- Navigate to Chapter 2 → Tap date field → Date picker opens

**Verification Status:** PENDING

---

### ARCH-002: Chapter 3 Wrong Keyboard Type
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Setup Quest Chapter 3
2. Tap on date input field

**Expected:** Numeric keyboard (for MM/DD/YYYY entry)  
**Actual:** Full alphanumeric keyboard appears

**Root Cause Analysis:**
- TextField missing `keyboardOptions` specification
- Defaults to `KeyboardType.Text`
- Date field expects numeric input

**Required Fix:**
```kotlin
TextField(
    value = dueDate,
    onValueChange = { dueDate = it },
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    ),
    // ...
)
```

**Regression Test:**
- Navigate to Chapter 3 → Tap date field → Numeric keyboard appears

**Verification Status:** PENDING

**Related:** Also affects BillEntryScreen.kt due date field (line ~160-180)

---

### DES-001: HomeScreen Missing Bottom Navigation
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Complete setup or reach Home screen
2. Observe bottom of screen

**Expected:** Bottom navigation bar with Home/Bills/Goals/Settings tabs  
**Actual:** No bottom navigation present

**Root Cause Analysis:**
- Screen composable lacks bottom navigation implementation
- Design contract requires bottom navigation on main screens
- Navigation 3 integration may be incomplete

**Required Fix:**
- Add BottomNavigation composable to HomeScreen.kt
- Connect to Navigation 3 routes
- Follow design contract styling (BackgroundDark, CyanAccent for selected)

**Regression Test:**
- Launch Home → Verify bottom nav visible with 4 tabs

**Verification Status:** PENDING

**Affected Screens:** HomeScreen.kt, BillsScreen.kt, GoalsScreen.kt, SettingsScreen.kt, StatsScreen.kt, TransactionDetailsScreen.kt, TreasureScreen.kt

---

### DES-005: BillsScreen Missing Bottom Navigation
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Bills screen
2. Observe bottom of screen

**Expected:** Bottom navigation bar present  
**Actual:** No bottom navigation

**Root Cause Analysis:**
- Same systemic issue as DES-001

**Required Fix:**
- Add BottomNavigation composable to BillsScreen.kt

**Regression Test:**
- Navigate to Bills → Verify bottom nav visible

**Verification Status:** PENDING

---

### DES-013: GoalsScreen Missing Bottom Navigation
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Goals screen
2. Observe bottom of screen

**Expected:** Bottom navigation bar present  
**Actual:** No bottom navigation

**Required Fix:**
- Add BottomNavigation composable to GoalsScreen.kt

**Regression Test:**
- Navigate to Goals → Verify bottom nav visible

**Verification Status:** PENDING

---

### DES-020: SettingsScreen Missing Bottom Navigation
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Settings screen
2. Observe bottom of screen

**Expected:** Bottom navigation bar present  
**Actual:** No bottom navigation

**Required Fix:**
- Add BottomNavigation composable to SettingsScreen.kt

**Regression Test:**
- Navigate to Settings → Verify bottom nav visible

**Verification Status:** PENDING

---

### DES-025: StatsScreen Missing Bottom Navigation
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Stats screen
2. Observe bottom of screen

**Expected:** Bottom navigation bar present  
**Actual:** No bottom navigation

**Required Fix:**
- Add BottomNavigation composable to StatsScreen.kt

**Regression Test:**
- Navigate to Stats → Verify bottom nav visible

**Verification Status:** PENDING

---

### DES-031: TreasureScreen No ViewModel Binding
**Severity:** CRITICAL  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Treasure screen
2. Observe XP, Streak, and Achievements sections

**Expected:** Real data from XpRepository and SavingsGoalRepository  
**Actual:** All sections show "No XP records", "No streak records", static content

**Root Cause Analysis:**
- Screen created as UI showcase but never wired to data layer
- Missing ViewModel to fetch XP, streaks, achievements
- TreasureScreen.kt accepts only navigation callbacks

**Required Fix:**
- Create or use ShieldProgressionViewModel
- Accept ViewModel parameter: `TreasureScreen(viewModel: ShieldProgressionViewModel)`
- Collect Flows from XpRepository and SavingsGoalRepository
- Replace static content with collected data

**Regression Test:**
- Navigate to Treasure → Verify real XP/streak/achievement data displays

**Verification Status:** PENDING

---

## High Defects

### CT-001: Setup Flow Persistence Test Failure
**Severity:** HIGH  
**Owner:** QA/Testing Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Run `SetupQuestFlowTest.setupPersistsAcrossProcessDeath`
2. Test seeds data via `setupDraftDao.getDraftSync()`

**Expected:** Draft persists and is retrievable  
**Actual:** Returns null in test environment even after data seeded

**Root Cause Analysis:**
- Test synchronization issue
- Blocking DAO methods exist purely for test
- May be related to timing/executor service usage

**Required Fix:**
- Verify SetupQuestViewModel process death handling
- Check test coroutine context
- Consider migrating to coroutine test helpers

**Regression Test:**
- Run connected test → Verify draft persists

**Verification Status:** PENDING

**Related:** TD-001 (Technical Debt)

---

### CT-002: Chapter Navigation Timing Test Failure
**Severity:** HIGH  
**Owner:** QA/Testing Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Run `NavigationSmokeTest.endToEndSetupQuestCompletes`
2. Test advances from Chapter 2 to Chapter 3

**Expected:** Assertion on Chapter 3 state passes  
**Actual:** Test advances before validation completes, assertion fails

**Root Cause Analysis:**
- Timing issue in test execution
- State update not synchronized
- Likely related to QA-001 navigation bug

**Required Fix:**
- Fix QA-001 first
- Add proper synchronization in test
- Verify ViewModel state updates before assertions

**Regression Test:**
- Run connected test → Complete quest → Chapter 3 appears

**Verification Status:** PENDING

---

### CT-003: Footer Visibility Timing Test Failure
**Severity:** HIGH  
**Owner:** QA/Testing Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Run `PersistentFooterTest.footerHiddenDuringSetupAppearsAfter`

**Expected:** Footer visible after setup completion  
**Actual:** Footer visibility assertion fails after setup completion

**Root Cause Analysis:**
- Setup completion state not properly detected
- Footer visibility tied to completion flag

**Required Fix:**
- Verify completion flag persistence
- Check footer visibility logic in MainActivity/NavShell

**Regression Test:**
- Complete setup → Verify footer appears

**Verification Status:** PENDING

---

### ARCH-003: SetupQuestViewModel Empty Navigation Callback
**Severity:** HIGH  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Complete Chapter 2
2. Observe navigation behavior

**Expected:** Navigate to Chapter 3  
**Actual:** State updates internally but navigation doesn't trigger

**Root Cause Analysis:**
- `completeChapter2(navigateToNext: () -> Unit)` accepts lambda but never calls it
- Lambda parameter passed but not invoked

**Required Fix:**
- Add `navigateToNext()` invocation after `saveDraft()`
- Verify callback chain to Navigation 3

**Regression Test:**
- Complete Chapter 2 → Verify navigation to Chapter 3

**Verification Status:** PENDING

---

### ARCH-004: BillEntryScreen Category Selection Non-Functional
**Severity:** HIGH  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Bill Entry screen
2. Select category from dropdown
3. Save bill
4. Re-open bill

**Expected:** Selected category persists and displays  
**Actual:** Category may not update state or persist correctly

**Root Cause Analysis:**
- Categories hardcoded as list
- Selection may not properly update ViewModel
- DATA-002: Bill entity lacks category field

**Required Fix:**
- Implement proper category selection UI (see DES-002.3)
- Wire selection to ViewModel state
- Requires DATA-002 fix (add category field)

**Regression Test:**
- Create bill → Select category → Save → Verify category persisted

**Verification Status:** PENDING

**Dependencies:** DATA-002

---

### DES-002: HomeScreen Hero Card Missing Border Stroke
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Home screen with positive balance
2. Observe hero card (Safe Now card)

**Expected:** 1dp stroke at 30% opacity cyan when positive  
**Actual:** No border stroke on CardHero

**Required Fix:**
```kotlin
Card(
    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f)),
    // ...
)
```

**Regression Test:**
- View Home screen → Verify hero card has cyan border

**Verification Status:** PENDING

---

### DES-003: HomeScreen "Safe to Spend" Typography Wrong
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Home screen
2. Observe "Safe to Spend" label

**Expected:** 16sp Bold (cyan if positive, red if shortage)  
**Actual:** Uses hardcoded font values

**Required Fix:**
- Apply MaterialTheme.typography tokens
- Color based on shortage state

**Regression Test:**
- Verify typography follows design contract

**Verification Status:** PENDING

---

### DES-004: HomeScreen Amount Text Hardcoded
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Home screen
2. Observe Safe Now amount

**Expected:** Uses `MaterialTheme.typography.displayLarge`  
**Actual:** Uses hardcoded `42.sp, ExtraBold`

**Required Fix:**
- Use typography tokens from design system

**Regression Test:**
- Verify amount uses displayLarge token

**Verification Status:** PENDING

---

### DES-006: BillsScreen Bill List Items Wrong Styling
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Bills screen
2. Observe bill list items

**Expected:** Themed cards with PanelDark background  
**Actual:** Default list styling without card treatment

**Required Fix:**
- Apply Card with PanelDark background
- Add proper border radius (16dp)

**Regression Test:**
- View Bills → Verify themed cards

**Verification Status:** PENDING

---

### DES-007: BudgetMenuScreen Touch Targets Below 48dp
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to BudgetMenu screen
2. Attempt to tap menu items

**Expected:** Minimum 48dp touch targets  
**Actual:** Menu items may be smaller than 48dp

**Required Fix:**
- Add minimumTouchTargetSize() or ensure 48dp height

**Regression Test:**
- Verify all menu items meet minimum touch target

**Verification Status:** PENDING

**Accessibility Impact:** Affects users with motor impairments

---

### DES-008: BudgetMenuScreen Missing PanelDark Background
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to BudgetMenu screen
2. Observe menu card

**Expected:** Uses `PanelDark` background  
**Actual:** Default Card colors

**Required Fix:**
```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = PanelDark
    ),
    // ...
)
```

**Regression Test:**
- Verify menu card uses PanelDark

**Verification Status:** PENDING

---

### DES-009: IncomeEntryScreen Income Type Selector Not Themed
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Income Entry screen
2. Observe income type toggle

**Expected:** Custom themed selector  
**Actual:** Default Material toggle buttons

**Required Fix:**
- Apply themed selector with CyanAccent selected state

**Regression Test:**
- Verify themed income type selector

**Verification Status:** PENDING

---

### DES-010: SavingsEntryScreen Goal Linkage Selector Not Themed
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Savings Entry screen
2. Open goal dropdown

**Expected:** Themed dropdown selector  
**Actual:** Default dropdown styling

**Required Fix:**
- Apply themed selector with PanelDark background

**Regression Test:**
- Verify themed goal selector

**Verification Status:** PENDING

---

### DES-011: SettingsScreen Danger Zone Not Grouped
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Settings screen
2. Scroll to delete actions

**Expected:** Delete actions visually grouped with warning styling  
**Actual:** Delete actions scattered, not distinct

**Required Fix:**
- Group danger zone actions
- Use DangerDot color for destructive actions
- Add warning styling container

**Regression Test:**
- Verify danger zone visually distinct

**Verification Status:** PENDING

---

### DES-012: TransactionDetailsScreen Category Name Hardcoded
**Severity:** HIGH  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Transaction Details
2. View transaction header

**Expected:** Shows actual transaction description/category  
**Actual:** Always shows "Rent Payment" regardless of actual transaction

**Required Fix:**
- Accept transactionId or transaction object parameter
- Query TransactionRepository for details
- Display actual category/description

**Regression Test:**
- View transaction → Verify correct category/name shown

**Verification Status:** PENDING

---

### DES-015: StatsScreen "View All Transactions" Wrong Style
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Stats screen
2. Observe "View All" button

**Expected:** Themed link style  
**Actual:** Uses `TextButton` instead of themed link

**Required Fix:**
- Apply themed link style with CyanAccent
- Or use proper outlined button

**Regression Test:**
- Verify button follows design contract

**Verification Status:** PENDING

---

### DES-016: TreasureScreen Canvas Icons Instead of Emoji
**Severity:** HIGH  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Treasure screen
2. Observe section icons

**Expected:** Emoji iconography per design contract (🛡️, 🔥, etc.)  
**Actual:** Custom Canvas-drawn icons

**Required Fix:**
- Replace Canvas icons with emoji per contract
- Or add accessibility labels to Canvas icons

**Regression Test:**
- Verify icons match design contract

**Verification Status:** PENDING

**Accessibility Impact:** Canvas icons lack screen reader support

---

### DATA-001: IncomeRepository.hasActiveSchedules() Always Returns True
**Severity:** HIGH  
**Owner:** Data Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Call `IncomeRepository.hasActiveSchedules()` with no schedules

**Expected:** Returns false when no schedules exist  
**Actual:** Hardcoded to return `true`

**Root Cause Analysis:**
```kotlin
suspend fun hasActiveSchedules(): Boolean {
    return true  // <-- Always returns true!
}
```

**Required Fix:**
```kotlin
suspend fun hasActiveSchedules(): Boolean {
    return incomeScheduleDao.getActiveScheduleCount() > 0
}
```

Add to IncomeScheduleDao:
```kotlin
@Query("SELECT COUNT(*) FROM income_schedules WHERE isActive = 1")
suspend fun getActiveScheduleCount(): Int
```

**Regression Test:**
- Test with no schedules → Verify returns false
- Test with schedules → Verify returns true

**Verification Status:** PENDING

---

## Medium Defects

### ARCH-005: Multiple Screens - Placeholder Click Handlers
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to StatsScreen or TransactionDetailsScreen
2. Tap "View All" button

**Expected:** Navigate to full transaction list  
**Actual:** No response (empty `onClick = {}`)

**Affected Screens:**
- StatsScreen.kt - "View All" button
- TransactionDetailsScreen.kt - "View All" button
- Multiple other buttons with empty handlers

**Required Fix:**
- Implement navigation callbacks
- Wire to Navigation 3 routes

**Regression Test:**
- Tap "View All" → Navigate to transaction list

**Verification Status:** PENDING

---

### ARCH-006: BillProtectedScreen Hardcoded Success Data
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Protect any bill
2. Complete protection flow

**Expected:** Shows actual bill name and amount  
**Actual:** Always shows "Rent Payment $950.00"

**Required Fix:**
- Accept `billId: Long` or `bill: Bill` parameter
- Query BillRepository for details
- Display actual bill name and amount

**Regression Test:**
- Protect different bill → Verify correct data shown

**Verification Status:** PENDING

---

### ARCH-007: ShieldProgressionScreen Static XP History
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Shield Progression screen
2. View XP History section

**Expected:** Real XP entries from database  
**Actual:** Hardcoded demo entries

**Required Fix:**
- Accept ViewModel parameter
- Display real XP entries from XpRepository

**Regression Test:**
- Earn XP → Verify appears in history

**Verification Status:** PENDING

---

### ARCH-008: Inconsistent ViewModel Factory Patterns
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
Code review of ViewModel patterns

**Expected:** Consistent Hilt injection  
**Actual:** Mix of Hilt and manual factories

| ViewModel | Pattern | Issue |
|-----------|---------|-------|
| HomeViewModel | Hilt constructor | ✅ Correct |
| SetupQuestViewModel | Manual Factory | ⚠️ Legacy pattern |
| BillsViewModel | Manual Factory | ⚠️ Legacy pattern |
| GoalsViewModel | Manual Factory | ⚠️ Legacy pattern |
| SavingsEntryViewModel | Manual Factory | ⚠️ Legacy pattern |

**Required Fix:**
- Migrate manual Factory ViewModels to Hilt constructor injection
- Use `hiltViewModel()` in screens
- Remove Factory classes

**Regression Test:**
- All screens load correctly after migration

**Verification Status:** PENDING

**Priority:** Phase 4 (Technical Debt)

---

### DES-014: HomeScreen Action Buttons Background
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Home screen
2. Observe action buttons (💰💳💎)

**Expected:** 15% opacity CyanAccent background  
**Actual:** May not follow Action Button token spec

**Required Fix:**
- Apply proper sizing and background

**Regression Test:**
- Verify action button styling

**Verification Status:** PENDING

---

### DES-017: BillEntryScreen Form Title Not Prominent
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Bill Entry screen

**Expected:** Clear visual hierarchy for title  
**Actual:** Title uses `headlineMedium` but lacks prominence

**Required Fix:**
- Apply proper header styling with icon background

**Verification Status:** PENDING

---

### DES-018: BillEntryScreen Text Fields Lack Focus Styling
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Bill Entry screen
2. Tap on text field

**Expected:** CyanAccent border when focused  
**Actual:** Default Material3 focus styling

**Required Fix:**
```kotlin
OutlinedTextField(
    colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CyanAccent,
        // ...
    ),
    // ...
)
```

**Verification Status:** PENDING

---

### DES-019: BillEntryScreen Due Date Lacks Calendar Icon
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Bill Entry screen
2. Observe due date field

**Expected:** Calendar icon affordance for date picker  
**Actual:** No clear visual indicator

**Required Fix:**
- Add trailingIcon with calendar icon

**Verification Status:** PENDING

---

### DES-021: BillEntryScreen Save Button Not Themed
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Bill Entry screen
2. Observe Save button

**Expected:** Themed button with CyanAccent15 background  
**Actual:** Default Material3 Button

**Required Fix:**
- Apply themed button styling

**Verification Status:** PENDING

---

### DES-022: BillPaymentScreen No Back Navigation
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Bill Payment screen

**Expected:** Visible back button or navigation affordance  
**Actual:** No back navigation

**Required Fix:**
- Add back button to top bar
- Or implement swipe-back gesture

**Verification Status:** PENDING

---

### DES-023: BillPaymentScreen Payment Amount No Currency Formatting
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Bill Payment
2. Enter amount

**Expected:** Proper currency mask/formatting  
**Actual:** Plain number input

**Required Fix:**
- Apply MoneyParser formatting to input
- Show formatted amount while typing

**Verification Status:** PENDING

---

### DES-024: BillProtectedScreen Protected Status Card Wrong Background
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Complete bill protection

**Expected:** CardHeroBackground (`#0A1F2C`)  
**Actual:** Uses `PanelDark`

**Required Fix:**
- Use CardHeroBackground token

**Verification Status:** PENDING

---

### DES-026: BillProtectedScreen Shield Icon No Background Circle
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View protected bill screen

**Expected:** 40dp circle with 20% opacity background  
**Actual:** Plain emoji without background

**Required Fix:**
- Add Box with background circle

**Verification Status:** PENDING

---

### DES-027: BillProtectedScreen Tier Indicators Not Distinct
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View protection tiers

**Expected:** Proper color coding (CyanAccent vs GoldAccent)  
**Actual:** Tiers lack visual distinction

**Required Fix:**
- Apply color coding to tier badges

**Verification Status:** PENDING

---

### DES-028: BillsScreen Protected Indicator Too Small
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Bills list with protected bills

**Expected:** Shield icon 20sp  
**Actual:** Shield icon 16sp

**Required Fix:**
- Increase icon size to 20sp

**Verification Status:** PENDING

---

### DES-029: BillsScreen FAB Not Following Action Button Spec
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Bills screen

**Expected:** 56dp circular FAB with 15% opacity  
**Actual:** Standard FAB styling

**Required Fix:**
- Apply themed FAB styling

**Verification Status:** PENDING

---

### DES-030: BudgetMenuScreen Icon Backgrounds Missing
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View BudgetMenu screen

**Expected:** 20% opacity background circles on icons  
**Actual:** Icons without backgrounds

**Required Fix:**
- Add Box with 20% opacity background

**Verification Status:** PENDING

---

### DES-032: BudgetMenuScreen Chevron Indicators Missing
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View BudgetMenu screen

**Expected:** Chevron/arrow indicating tappable  
**Actual:** No visual indicator

**Required Fix:**
- Add trailing chevron icons

**Verification Status:** PENDING

---

### DES-033: BudgetMenuScreen Selected State Not Visible
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View BudgetMenu screen
2. Select an item

**Expected:** Visual distinction for selected item  
**Actual:** No selected state

**Required Fix:**
- Add selected state styling

**Verification Status:** PENDING

---

### DES-034: GoalsScreen Progress Bars Lack Gradient
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Goals screen

**Expected:** Gradient styling on progress bars  
**Actual:** Solid color

**Required Fix:**
- Apply gradient to LinearProgressIndicator

**Verification Status:** PENDING

---

### DES-035: GoalsScreen Goal Category Icons Inconsistent
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Goals screen

**Expected:** Correct emoji per category mapping  
**Actual:** Some goals use wrong icons

**Required Fix:**
- Verify category-to-emoji mapping

**Verification Status:** PENDING

---

### DES-036: IncomeEntryScreen Payday Date Picker Not Obvious
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Income Entry screen

**Expected:** Calendar icon affordance  
**Actual:** No clear date picker indicator

**Required Fix:**
- Add calendar trailing icon

**Verification Status:** PENDING

---

### DES-037: IncomeEntryScreen Frequency Selector Uses Default Dropdown
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Income Entry
2. Open frequency selector

**Expected:** Custom themed selector  
**Actual:** Default dropdown

**Required Fix:**
- Apply themed selector

**Verification Status:** PENDING

---

### DES-038: SavingsEntryScreen Amount Field No Currency Mask
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Savings Entry
2. Enter amount

**Expected:** Currency formatting while typing  
**Actual:** Plain number input

**Required Fix:**
- Apply MoneyParser formatting

**Verification Status:** PENDING

---

### DES-039: SavingsEntryScreen Quick Add Buttons Missing
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open Savings Entry

**Expected:** Quick-select buttons for $50, $100, $500  
**Actual:** No quick add buttons

**Required Fix:**
- Add quick amount buttons

**Verification Status:** PENDING

---

### DES-040: SettingsScreen Settings Groups Lack Separation
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Settings

**Expected:** Section headers with PanelDark background  
**Actual:** Categories without clear separation

**Required Fix:**
- Add section headers
- Apply PanelDark backgrounds

**Verification Status:** PENDING

---

### DES-041: SettingsScreen Toggle Switches Not Themed
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Navigate to Settings
2. Observe toggle switches

**Expected:** CyanAccent themed switches  
**Actual:** Default Material3 colors

**Required Fix:**
```kotlin
Switch(
    colors = SwitchDefaults.colors(
        checkedThumbColor = CyanAccent,
        // ...
    ),
    // ...
)
```

**Verification Status:** PENDING

---

### DES-042: SetupQuestScreen Chapter Cards Use Default Material Styling
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Setup Quest

**Expected:** PanelDark with proper radius  
**Actual:** Default Material cards

**Required Fix:**
- Apply themed card styling

**Verification Status:** PENDING

---

### DES-043: SetupQuestScreen Progress Indicator Not Themed
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Setup Quest

**Expected:** Themed LinearProgressIndicator  
**Actual:** Default progress indicator

**Required Fix:**
```kotlin
LinearProgressIndicator(
    color = CyanAccent,
    trackColor = PanelDark,
    // ...
)
```

**Verification Status:** PENDING

---

### DES-044: SetupQuestScreen Form Fields Lack Error Placement
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Submit invalid form in Setup Quest

**Expected:** Errors appear near relevant controls  
**Actual:** Errors may not follow design contract

**Required Fix:**
- Position errors per design contract

**Verification Status:** PENDING

---

### DES-045: SetupQuestScreen Bill Card Uses Elevation
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View bill card in Setup Quest

**Expected:** Flat design (no elevation)  
**Actual:** `cardElevation(defaultElevation = 2.dp)`

**Required Fix:**
- Remove elevation, use flat design

**Verification Status:** PENDING

---

### DES-046: SetupQuestScreen Dialogs Not Themed
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Open icon picker or date picker in Setup Quest

**Expected:** Themed dialogs with PanelDark  
**Actual:** Default Material dialogs

**Required Fix:**
- Apply themed dialog styling

**Verification Status:** PENDING

---

### DES-047: SetupQuestScreen "Activate My Shield" Button Lacks Prominence
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Reach final Setup Quest chapter

**Expected:** GoldAccent to indicate completion  
**Actual:** Standard button styling

**Required Fix:**
- Apply GoldAccent to CTA

**Verification Status:** PENDING

---

### DES-048: ShieldProgressionScreen XP History Lacks Interactive Affordance
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Shield Progression screen

**Expected:** Indication if items are tappable  
**Actual:** No interactive affordance

**Required Fix:**
- Add visual indicators for tappable items

**Verification Status:** PENDING

---

### DES-049: ShieldProgressionScreen Level Progress Not Visible
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Shield Progression screen

**Expected:** Current level and progress to next level  
**Actual:** Not displayed

**Required Fix:**
- Add level progress UI
- Calculate from XP repository data

**Verification Status:** PENDING

---

### DES-050: StatsScreen Legend Items Spacing Inconsistent
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Stats screen

**Expected:** 16dp spacing  
**Actual:** Inconsistent spacing

**Required Fix:**
- Apply 16dp spacing to legend

**Verification Status:** PENDING

---

### DES-051: StatsScreen Category Progress Bars Not Using PanelBorder
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Stats screen

**Expected:** PanelBorder for background track  
**Actual:** Different color

**Required Fix:**
- Apply PanelBorder token

**Verification Status:** PENDING

---

### DES-052: TransactionDetailsScreen Transaction Header Icon Hardcoded
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Transaction Details

**Expected:** Dynamic category icon  
**Actual:** Always shows "🏠"

**Required Fix:**
- Use transaction.category to determine icon
- Map categories to appropriate icons

**Verification Status:** PENDING

---

### DES-053: TransactionDetailsScreen Delete Button Styling Inconsistent
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Transaction Details

**Expected:** Themed destructive button  
**Actual:** Card with border instead of themed button

**Required Fix:**
- Apply themed destructive button

**Verification Status:** PENDING

---

### DES-054: TransactionDetailsScreen XP Indicator Shows for All
**Severity:** MEDIUM  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View various transaction types

**Expected:** XP only for bill payments  
**Actual:** Shows XP for all transactions

**Required Fix:**
- Conditionally show XP indicator
- Check transaction type before showing XP

**Verification Status:** PENDING

---

### DES-055: TransactionDetailsScreen Error Banner Wrong Color
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Trigger error in Transaction Details

**Expected:** PanelDark background  
**Actual:** Alpha overlay only

**Required Fix:**
- Use PanelDark for error container

**Verification Status:** PENDING

---

### DES-056: TreasureScreen Canvas Icons Accessibility Issue
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. Enable screen reader
2. Navigate Treasure screen

**Expected:** Icons have content descriptions  
**Actual:** Canvas icons lack accessibility support

**Required Fix:**
- Add contentDescription to Canvas
- Or switch to emoji with descriptions

**Verification Status:** PENDING

**Accessibility Impact:** Screen readers cannot describe Canvas icons

---

### DES-057: TreasureScreen Close Button Uses Wrong Icon
**Severity:** MEDIUM  
**Owner:** UI Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Treasure screen

**Expected:** Back arrow emoji "←" per contract  
**Actual:** Uses "<" text character

**Required Fix:**
- Replace with "←" emoji

**Verification Status:** PENDING

---

### DATA-003: HomeViewModel Streak Calculation Placeholder
**Severity:** MEDIUM  
**Owner:** Data Engineer  
**Status:** OPEN

**Reproduction Steps:**
1. View Home screen streak display

**Expected:** Real streak calculation  
**Actual:** Always returns 0

**Root Cause Analysis:**
```kotlin
private fun calculateStreak(bills: List<Bill>): Int {
    return 0 // TODO: implement proper streak calculation
}
```

**Required Fix:**
```kotlin
private fun calculateStreak(bills: List<Bill>): Int {
    return savingsGoalRepository.getCurrentStreak()
    // Or implement actual calculation
}
```

**Regression Test:**
- Complete consecutive days → Verify streak increases

**Verification Status:** PENDING

---

## Low Defects

### TD-001: Blocking DAO Methods Exist Purely for Tests
**Severity:** LOW  
**Owner:** Data Engineer  
**Status:** OPEN (Technical Debt)

**Description:**
`*Blocking()` methods in DAOs exist purely for test synchronization.

**Required Fix:**
- Migrate to coroutine test helpers
- Use `runTest {}` with proper dispatchers
- Remove blocking methods

**Priority:** Phase 4 (Technical Debt)

---

### TD-002: SetupQuestViewModel Uses ExecutorService
**Severity:** LOW  
**Owner:** Android Implementation Engineer  
**Status:** OPEN (Technical Debt)

**Description:**
Thread pool executor in ViewModel for draft persistence.

**Required Fix:**
- Use coroutines with proper scopes
- Replace ExecutorService with viewModelScope

**Priority:** Phase 4 (Technical Debt)

---

### DATA-004: BudgetRepository.getCategoryById() Returns Null
**Severity:** LOW  
**Owner:** Data Engineer  
**Status:** OPEN

**Description:**
```kotlin
suspend fun getCategoryById(categoryId: Long): BudgetCategory? {
    // Not directly available in the DAO
    return null
}
```

**Required Fix:**
Add to BudgetCategoryDao:
```kotlin
@Query("SELECT * FROM budget_categories WHERE id = :categoryId")
suspend fun getCategoryById(categoryId: Long): BudgetCategory?
```

**Impact:** Limited - appears unused in current codebase.

---

### DATA-005: Duplicate Migration Definitions
**Severity:** LOW  
**Owner:** Data Engineer  
**Status:** OPEN

**Description:**
Migrations defined in both `DatabaseMigrations.kt` and `BudgetShieldDatabase.kt`.

**Required Fix:**
- Consolidate migrations in one location
- Remove duplicate external file

---

### DATA-006: UserSettingsDao Duplicate Methods
**Severity:** LOW  
**Owner:** Data Engineer  
**Status:** OPEN

**Description:**
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertSettings(settings: UserSettings)

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insert(settings: UserSettings)  // Duplicate!
```

**Required Fix:**
- Remove duplicate methods
- Keep descriptive names

---

### DATA-007: SavingsGoalRepository Timezone Uses System Default
**Severity:** LOW  
**Owner:** Data Engineer  
**Status:** OPEN

**Description:**
Streak reset uses system default timezone:
```kotlin
val yesterday = LocalDate.now().minusDays(1).toString()
```

**Required Fix:**
- Use UserSettings timezone
- Or allow configurable timezone

---

### DES-058: HomeScreen Month Selector Border Radius
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `RoundedCornerShape(16.dp)` but contract specifies specific Month Selector Card styling.

**Required Fix:**
- Verify against design contract

---

### DES-059: HomeScreen Activity Items Wrong Background
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Activity items use `PanelDark` but contract specifies color-specific 20% opacity.

**Required Fix:**
- Apply proper icon backgrounds

---

### DES-060: BillEntryScreen Screen Padding Inconsistent
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `16.dp` horizontal padding instead of contract-specified `20.dp`.

**Required Fix:**
- Standardize to 20dp

---

### DES-061: BillEntryScreen Cancel Action Not Distinct
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Cancel uses `TextButton` but lacks visual distinction for destructive/cancel action.

---

### DES-062: BillPaymentScreen Payment Method Not Themed
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Payment method options use default radio styling.

---

### DES-063: BillProtectedScreen Title Lacks Header Styling
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Title doesn't follow HeaderSection pattern with icon background.

---

### DES-064: BillsScreen Due Date Formatting Inconsistent
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Date format varies from contract MM/DD spec.

---

### DES-065: BillsScreen Empty State Lacks Illustration
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Empty state uses plain text instead of themed empty state with icon.

---

### DES-066: GoalsScreen Goal Cards Border Radius Wrong
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `12.dp` instead of contract-specified `16.dp`.

---

### DES-067: GoalsScreen Amount Formatting Inconsistent
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Currency display doesn't consistently show cents.

---

### DES-068: IncomeEntryScreen Header Lacks Icon
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Missing header icon pattern (💰 in circle).

---

### DES-069: IncomeEntryScreen Save Button Positioning
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Save button should be at bottom with proper padding per contract.

---

### DES-070: IncomeEntryScreen Padding Inconsistent
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `16.dp` instead of `20.dp` horizontal padding.

---

### DES-071: SavingsEntryScreen Header Lacks Icon
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Missing header icon with circle background pattern.

---

### DES-072: SavingsEntryScreen Transaction Note Too Prominent
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Optional field looks same priority as required fields.

---

### DES-073: SettingsScreen App Version Text Wrong Color
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `TextPrimary` instead of `TextMuted`.

---

### DES-074: SetupQuestScreen Navigation Footer Uses Shadow
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Footer uses `shadowElevation = 8.dp` instead of top border.

---

### DES-075: ShieldProgressionScreen No Back Navigation
**Severity:** LOW  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Description:**
Screen lacks back button in header.

---

### DES-076: StatsScreen Stat Cards Hardcoded Color
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `Color(0xFF0D1B26)` instead of theme token.

---

### DES-077: TransactionDetailsScreen Recent Transactions Hardcoded Limit
**Severity:** LOW  
**Owner:** Android Implementation Engineer  
**Status:** OPEN

**Description:**
Takes 5 items instead of using pagination.

---

### DES-078: TreasureScreen Empty State Backgrounds Hardcoded
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
Uses `Color(0xFF0A1620)` instead of `CardHeroBackground`.

---

### DES-079: TreasureScreen Expand Sections No Haptic Feedback
**Severity:** LOW  
**Owner:** UI Engineer  
**Status:** OPEN

**Description:**
No visual indication of interactive expansion.

---

## Contradictions Resolved

### Contradiction 1: Bottom Navigation Implementation Pattern

**Finding A (Design Audit):**
Bottom navigation missing on Home, Bills, Goals, Settings, Stats, Treasure, TransactionDetails screens. Marked as CRITICAL.

**Finding B (Architect Audit):**
Architecture has Navigation 3 with NavShell pattern implemented. Routes defined for all destinations.

**Resolution:**
- No true contradiction. Navigation infrastructure exists but individual screens don't implement bottom navigation.
- Root Cause: Screens were developed without bottom navigation composable despite infrastructure being ready.
- Fix: Add BottomNavigation composable to each screen, wire to existing Navigation 3 routes.

---

### Contradiction 2: Chapter 2 Date Field Behavior

**Finding A (QA Audit):**
Could not test Chapter 2 due to QA-001 blocking bug.

**Finding B (Architect Audit):**
Chapter 2 date field has `readOnly=true` with no click handler - confirmed non-functional.

**Resolution:**
- No contradiction - both findings align.
- QA could not reach Chapter 2, but Architect audit found the issue via code review.
- Both confirm critical navigation/setup flow issues.

---

### Contradiction 3: Bill Category Field

**Finding A (Data Audit):**
Bill entity lacks category field - MEDIUM severity (DATA-002).

**Finding B (Design Audit):**
Category selector not themed in BillEntryScreen - HIGH severity (DES-002.3).

**Resolution:**
- Related issues, not contradictions.
- Data audit identified missing field (functional).
- Design audit identified UI issues (theming).
- Resolution: Both must be fixed together - add category field (DATA-002) then theme the selector (DES-002.3).
- Combined into ARCH-004 with dependencies noted.

---

### Contradiction 4: Test Count Reporting

**Finding A (Known Bugs - Historical):**
Conflicting test counts (12, 14, 20) reported - marked as FIXED.

**Finding B (Current State):**
20/23 connected tests pass (3 failing: CT-001, CT-002, CT-003).

**Resolution:**
- Historical issue has been resolved.
- Current test count is consistent: 23 connected tests total.
- Previous reporting inconsistency was fixed per TI-005.

---

## Implementation Queue

### Phase 1: P0 - Blocking Release (Critical)

**Dependencies:** None

| # | Defect | Description | Owner |
|---|--------|-------------|-------|
| 1 | QA-001 | Setup Quest Next button broken | Android Engineer |
| 2 | ARCH-001 | Chapter 2 date field non-responsive | Android Engineer |
| 3 | ARCH-002 | Chapter 3 wrong keyboard | Android Engineer |
| 4 | DES-001 | HomeScreen bottom navigation | Android Engineer |
| 5 | DES-005 | BillsScreen bottom navigation | Android Engineer |
| 6 | DES-013 | GoalsScreen bottom navigation | Android Engineer |
| 7 | DES-020 | SettingsScreen bottom navigation | Android Engineer |
| 8 | DES-025 | StatsScreen bottom navigation | Android Engineer |
| 9 | DES-031 | TreasureScreen ViewModel binding | Android Engineer |

**Exit Criteria:**
- All P0 defects closed
- 23/23 connected tests pass
- Fresh install → Complete Setup Quest → Use all main screens

---

### Phase 2: P1 - High Priority

**Dependencies:** QA-001 (to unblock testing)

| # | Defect | Description | Owner |
|---|--------|-------------|-------|
| 10 | CT-001 | Setup flow persistence test | QA Engineer |
| 11 | CT-002 | Chapter navigation timing test | QA Engineer |
| 12 | CT-003 | Footer visibility timing test | QA Engineer |
| 13 | ARCH-003 | SetupQuestViewModel navigation callback | Android Engineer |
| 14 | ARCH-004 | Category selection non-functional | Android Engineer |
| 15 | ARCH-006 | BillProtectedScreen hardcoded data | Android Engineer |
| 16 | DES-002 | HomeScreen hero card border | UI Engineer |
| 17 | DES-003 | HomeScreen Safe to Spend typography | UI Engineer |
| 18 | DES-004 | HomeScreen amount text hardcoded | UI Engineer |
| 19 | DES-006 | BillsScreen bill list styling | UI Engineer |
| 20 | DES-007 | BudgetMenuScreen touch targets | UI Engineer |
| 21 | DES-008 | BudgetMenuScreen PanelDark | UI Engineer |
| 22 | DES-009 | IncomeEntryScreen income type selector | UI Engineer |
| 23 | DES-010 | SavingsEntryScreen goal selector | UI Engineer |
| 24 | DES-011 | SettingsScreen danger zone grouping | UI Engineer |
| 25 | DES-012 | TransactionDetailsScreen hardcoded text | Android Engineer |
| 26 | DES-015 | StatsScreen View All button style | UI Engineer |
| 27 | DES-016 | TreasureScreen Canvas vs emoji | UI Engineer |
| 28 | DATA-001 | IncomeRepository.hasActiveSchedules() | Data Engineer |

**Exit Criteria:**
- All P1 defects closed
- No hardcoded content in user-facing screens
- All themed per design contract

---

### Phase 3: P2 - Medium Priority

**Dependencies:** Phase 1 complete

| # | Defect | Description | Owner |
|---|--------|-------------|-------|
| 29 | ARCH-005 | Placeholder click handlers | Android Engineer |
| 30 | ARCH-007 | ShieldProgressionScreen static XP | Android Engineer |
| 31 | DES-014 | HomeScreen action buttons | UI Engineer |
| 32 | DES-017 | DES-079 | [All Medium design defects] | UI Engineer |
| 33 | DATA-003 | HomeViewModel streak calculation | Data Engineer |

**Exit Criteria:**
- All P2 defects closed
- All screens polished per design contract
- XP/streak features fully functional

---

### Phase 4: P3 - Low Priority (Technical Debt)

**Dependencies:** Phase 2 complete

| # | Defect | Description | Owner |
|---|--------|-------------|-------|
| 34 | ARCH-008 | ViewModel factory standardization | Android Engineer |
| 35 | TD-001 | Blocking DAO methods for tests | Data Engineer |
| 36 | TD-002 | SetupQuestViewModel ExecutorService | Android Engineer |
| 37 | DATA-004 | BudgetRepository.getCategoryById() | Data Engineer |
| 38 | DATA-005 | Duplicate migration definitions | Data Engineer |
| 39 | DATA-006 | DAO duplicate methods | Data Engineer |
| 40 | DATA-007 | Timezone from UserSettings | Data Engineer |
| 41 | DES-058 | DES-079 | [All Low design defects] | UI Engineer |

**Exit Criteria:**
- All P3 defects closed
- Code quality standards met
- No technical debt remaining

---

## Historical Issues (Closed)

The following issues from KNOWN_BUGS.md have been resolved and are preserved for record:

### Test Integrity Issues (RESOLVED)
| ID | Description | Resolution |
|----|-------------|------------|
| TI-001 | Fake JVM test doubles used instead of production routes | Removed TestHome/TestTreasure, using production routes |
| TI-002 | Weakened instrumentation coverage to silence CI | Restored full NavigationSmokeTest coverage |
| TI-003 | Placeholder APK SHA-256 in QA report | Replaced with real APK hash from verified build |
| TI-004 | Conflicting test counts (12, 14, 20) reported | Standardized on Gradle XML/HTML report totals |
| TI-005 | Stale project state marked Task 3 COMPLETE prematurely | Reopened as IN PROGRESS, closed after verified evidence |
| TI-006 | CI unverified — no real emulator test execution | Run connectedDebugAndroidTest on API 34 emulator |

### Treasure Persistence Issues (RESOLVED)
| ID | Description | Resolution |
|----|-------------|------------|
| TI-007 | No new tests proving persistence behavior | Added MoneyParserTest, DateParserTest, BillDaoTest, BillDatabasePersistenceTest, BillRepositoryTest, BillEntryViewModelTest |
| TI-008 | Destructive migration fallback enabled | Removed fallbackToDestructiveMigration from BudgetShieldDatabase |
| TI-009 | Floating-point money conversion | Created MoneyParser with exact integer arithmetic |
| TI-010 | Ignored Result from createBill() | BillEntryScreen now handles Result, shows errors, prevents duplicate saves |
| TI-011 | Weak date validation | Created DateParser with strict LocalDate validation |
| TI-012 | Blocked "Pay Bill" workflow | TreasureScreen shows "Pay Bill" for ALL unpaid bills |
| TI-013 | Unverified process death claims | Created disk-backed Room persistence tests |
| TI-014 | Mismatched release tagging | Created new verified release with unique tag |

### Navigation 3 Migration (RESOLVED)
| ID | Description | Resolution |
|----|-------------|------------|
| NAV3-001 | Previous implementation used Navigation Compose 2.8.7, not REAL Navigation 3 | Updated to androidx.navigation3:navigation3-runtime:1.1.4 |
| NAV3-002 | Unit tests reported NO-SOURCE (no tests existed) | Created 14 JVM unit tests (RouteCompletenessTest.kt, BackStackPolicyTest.kt) — ALL PASSING |
| NAV3-003 | Dependency versions incorrect | Updated to locked versions: BOM 2026.06.00, Activity 1.13.0, Lifecycle 2.10.0 |
| NAV3-004 | Documentation claimed Navigation 3 but used Navigation 2.x APIs | Corrected all documentation with real versions and APIs |
| NAV3-005 | Runtime QA not executed — no device/emulator testing performed | Fresh install, launch, and navigation QA executed on emulator-5554 (API 34) |
| NAV3-006 | Screenshots not captured — placeholder QA report | 5 runtime screenshots captured and verified at 1080x2400 |

---

## Summary Statistics

| Severity | Count | Blocking Release |
|----------|-------|------------------|
| CRITICAL | 9 | 9 |
| HIGH | 24 | 0 |
| MEDIUM | 31 | 0 |
| LOW | 7 | 0 |
| **TOTAL** | **71** | **9** |

---

*Document generated: 2026-07-23*  
*Audits merged: Architect, Design, QA, Data*  
*Previous issues preserved from KNOWN_BUGS.md*
