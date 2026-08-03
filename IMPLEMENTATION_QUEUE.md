# BudgetShield Implementation Queue - Final

## Recovery Progress — Task 18 Complete (2026-08-02)

Recovery priority 3 is complete. The setup/Home/footer connected-test blocker is closed with 23/23 tests passing locally and in GitHub Actions run `30776785734` for commit `37c49e5`.

The next executable priority is contract reconstruction: Product, Safe Now, Screen Map, Data Model, and Test Plan. Existing historical defect hypotheses below remain inputs only and must still be reproduced before implementation.

## Recovery Priority Override — 2026-08-02

This historical queue is preserved, but “Final” is no longer a current status. The verified recovery order is:

1. Reconcile documentation truth and restore missing approved contracts.
2. Make connected-test success mandatory for CI/release truth.
3. Repair the two current setup-completion/Home-stack/footer failures.
4. Verify Safe Now, exact money/date rules, Room migrations, and process-death persistence.
5. Remove production placeholders and dead controls.
6. Verify every financial and gamification feature.
7. Complete design, accessibility, device, upgrade, signing, shrinking, and independent release gates.

See `RECOVERY_BASELINE_2026-08-02.md` for evidence and acceptance order. Do not implement an old defect solely because it appears below; reproduce it first.

**Generated:** 2026-07-23  
**Lead Architect Review:** COMPLETE  
**Total Defects:** 71  
**Blocking Release:** 9 Critical

---

## Executive Summary

This queue prioritizes defects based on:
1. **Blockers first** - QA cannot test until these are fixed
2. **Dependencies second** - Some fixes require infrastructure first
3. **User journeys third** - Core functionality must work
4. **Polish last** - Theme and visual fixes

---

## Phase 1: P0 - Unblock All Testing (MUST FIX FIRST)

**Goal:** Make the app testable from fresh install → Chapter 6 → Home
**Estimated Time:** 4-6 hours  
**Dependencies:** None  
**Risk:** LOW - isolated fixes, minimal regression risk

### Root Cause Analysis: QA-001

**The Real Problem:** The "Next" button shows `clickable="false"` in UI dump despite being enabled. This is a **recomposition race condition**, not just a clickable modifier issue.

**Evidence Points:**
1. UI dump shows `clickable="false"` even when button appears enabled
2. StateFlow emission may not be triggering recomposition
3. Navigation callback chain may be broken at multiple points

**Required Investigation:**
```kotlin
// Check SetupQuestViewModel line ~80-120
goToNextChapter() {
    // 1. Does this emit to StateFlow?
    // 2. Does StateFlow value update in UI?
    // 3. Does navigation callback get invoked?
}

// Check SetupQuestScreen.kt line ~520
NavigationFooter(
    enabled = state.isNextEnabled,  // Does this recompose when state changes?
    onClick = viewModel::goToNextChapter  // Is this properly wired?
)
```

### Phase 1 Implementation Order

| Order | ID | File | Fix Description | Verification |
|-------|-----|------|-------------------|--------------|
| 1.1 | **QA-001** | SetupQuestViewModel.kt | Fix StateFlow emission - ensure `isNextEnabled` state actually emits when validation passes | Log state emission, verify emission occurs |
| 1.2 | **QA-001** | SetupQuestScreen.kt | Add explicit recomposition trigger - wrap navigation footer in `key()` or derived state | UI dump shows `clickable="true"` |
| 1.3 | **QA-001** | SetupQuestScreen.kt | Fix navigation callback wiring - verify `onClick` lambda captures updated state | Next button click triggers navigation |
| 1.4 | **ARCH-003** | SetupQuestViewModel.kt | Fix Chapter 2→3 navigation - invoke `navigateToNext()` callback after `saveDraft()` | Chapter 2 complete → Chapter 3 appears |
| 1.5 | **ARCH-001** | SetupQuestScreen.kt ~330-350 | Add clickable modifier to Chapter 2 date field: `.clickable { showDatePicker = true }` | Date field opens picker on tap |
| 1.6 | **ARCH-002** | SetupQuestScreen.kt Chapter 3 | Add Number keyboard: `KeyboardOptions(keyboardType = KeyboardType.Number)` | Numeric keyboard appears for date field |
| 1.7 | **ARCH-002** | BillEntryScreen.kt ~160-180 | Add Number keyboard to due date field (same fix as Chapter 3) | Numeric keyboard appears |

### Phase 1 Verification Gate

**MUST PASS before proceeding:**
- [ ] Fresh install → Enter Chapter 1 → Tap Next → Chapter 2 appears
- [ ] Chapter 2 → Tap date field → Date picker opens
- [ ] Chapter 2 → Complete → Chapter 3 appears
- [ ] Chapter 3 → Tap date field → Numeric keyboard appears
- [ ] Can complete full Setup Quest through Chapter 6
- [ ] Arrives at Home screen with bottom navigation (even if empty)

---

## Phase 2: P1 - Core Functionality & Navigation (HIGH)

**Goal:** All primary user journeys work end-to-end  
**Estimated Time:** 1-2 days  
**Dependencies:** Phase 1 complete (app is testable)  
**Risk:** MEDIUM - Navigation 3 changes affect all screens

### Analysis: Bottom Navigation Architecture

**Affected Screens:** HomeScreen, BillsScreen, GoalsScreen, SettingsScreen, StatsScreen, TreasureScreen, TransactionDetailsScreen

**Shared Component Pattern:**
```kotlin
// Create reusable BudgetShieldBottomNav component
@Composable
fun BudgetShieldBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // Background: BackgroundDark
    // Selected: CyanAccent
    // Unselected: TextSecondary
    // Items: Home, Bills, Goals, Settings
}
```

**Navigation 3 Integration Check:**
- Historical Issues confirm Navigation 3 migration is RESOLVED
- Routes are defined and working
- Bottom nav needs to call `navigateTo(String)` from Navigation 3

### Phase 2 Implementation Order

| Order | ID | File | Fix Description | Dependencies |
|-------|-----|------|-----------------|--------------|
| 2.1 | **DES-001,005,013,020,025** | Multiple | Create reusable BudgetShieldBottomNav component in components/ | None (UI only) |
| 2.2 | **DES-001** | HomeScreen.kt | Add BudgetShieldBottomNav to HomeScreen | 2.1 |
| 2.3 | **DES-005** | BillsScreen.kt | Add BudgetShieldBottomNav to BillsScreen | 2.1 |
| 2.4 | **DES-013** | GoalsScreen.kt | Add BudgetShieldBottomNav to GoalsScreen | 2.1 |
| 2.5 | **DES-020** | SettingsScreen.kt | Add BudgetShieldBottomNav to SettingsScreen | 2.1 |
| 2.6 | **DES-025** | StatsScreen.kt | Add BudgetShieldBottomNav to StatsScreen | 2.1 |
| 2.7 | **DES-031** | TreasureScreen.kt + ShieldProgressionViewModel.kt | Create ViewModel binding: `TreasureScreen(viewModel: ShieldProgressionViewModel)`, collect XP/streak/achievement flows | None |
| 2.8 | **DATA-001** | IncomeRepository.kt + IncomeScheduleDao.kt | Fix `hasActiveSchedules()`: add `getActiveScheduleCount()` query, return actual count | None |
| 2.9 | **ARCH-005** | StatsScreen.kt + TransactionDetailsScreen.kt | Wire "View All" navigation: replace empty `onClick = {}` with actual navigation callback | 2.1 |
| 2.10 | **ARCH-006** | BillProtectedScreen.kt | Accept `billId: Long` parameter, query BillRepository, display actual bill data | None |
| 2.11 | **DES-012** | TransactionDetailsScreen.kt | Accept `transactionId` parameter, query TransactionRepository, display actual category/name | None |
| 2.12 | **DES-016** | TreasureScreen.kt | Replace Canvas icons with emoji: 🛡️, 🔥, etc. per design contract | 2.7 |

### Phase 2 Verification Gate

**MUST PASS before proceeding:**
- [ ] All main screens (Home, Bills, Goals, Settings) show bottom navigation
- [ ] Bottom nav switches between screens correctly
- [ ] TreasureScreen shows real XP/streak data (not "No XP records")
- [ ] StatsScreen "View All" navigates to transaction list
- [ ] TransactionDetails shows actual transaction (not "Rent Payment")
- [ ] BillProtected shows actual bill name/amount

---

## Phase 3: P2 - Data Integrity & Category Implementation (MEDIUM)

**Goal:** Real data throughout, bill categories work, theme compliance  
**Estimated Time:** 1-2 days  
**Dependencies:** Phase 2 complete  
**Risk:** HIGH - Database migrations can corrupt user data

### Analysis: Category Dependency Chain

This is the most complex dependency chain in the register:

```
DATA-002 (DB Migration)
    ↓
Bill.kt Entity Update (add category field)
    ↓
BillRepository.kt Update (category methods)
    ↓
BillEntryViewModel.kt Update (category state)
    ↓
ARCH-004 BillEntryScreen.kt (category selection UI)
```

**Migration Safety:**
- Must preserve existing bill data
- Category field nullable or default value
- Test on database with existing bills before release

### Phase 3 Implementation Order

| Order | ID | File | Fix Description | Dependencies |
|-------|-----|------|-----------------|--------------|
| 3.1 | **DATA-002** | BudgetShieldDatabase.kt | Migration: Add `category` column to `bills` table with default NULL | None |
| 3.2 | **DATA-002** | Bill.kt | Add `category: String?` field to entity | 3.1 |
| 3.3 | **DATA-002** | BillDao.kt | Add category query methods if needed | 3.2 |
| 3.4 | **ARCH-004** | BillEntryViewModel.kt | Add category to state, validation, save logic | 3.2 |
| 3.5 | **ARCH-004** | BillEntryScreen.kt | Implement category dropdown: select → update ViewModel → persist | 3.4 |
| 3.6 | **DATA-003** | HomeViewModel.kt | Fix streak calculation: implement proper streak logic or delegate to repository | None |
| 3.7 | **ARCH-007** | ShieldProgressionScreen.kt | Wire to ShieldProgressionViewModel, display real XP history | 2.7 |
| 3.8 | **DES-002** | HomeScreen.kt | Add hero card border: `BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))` | None |
| 3.9 | **DES-003** | HomeScreen.kt | Fix "Safe to Spend" typography: use `headlineMedium` + dynamic color | None |
| 3.10 | **DES-004** | HomeScreen.kt | Fix amount text: use `MaterialTheme.typography.displayLarge` | None |
| 3.11 | **DES-006** | BillsScreen.kt | Apply themed cards with `PanelDark` background and 16dp radius | None |
| 3.12 | **DES-007** | BudgetMenuScreen.kt | Ensure 48dp minimum touch targets | None |
| 3.13 | **DES-008** | BudgetMenuScreen.kt | Apply `PanelDark` background to menu card | None |
| 3.14 | **DES-009** | IncomeEntryScreen.kt | Theme income type selector with `CyanAccent` selected state | None |
| 3.15 | **DES-010** | SavingsEntryScreen.kt | Theme goal linkage selector with `PanelDark` background | None |
| 3.16 | **DES-011** | SettingsScreen.kt | Group danger zone actions, use `DangerDot` color, add warning container | None |

### Phase 3 Verification Gate

**MUST PASS before proceeding:**
- [ ] Existing bills survive migration (no data loss)
- [ ] New bills can have category selected and persisted
- [ ] Re-opening bill shows correct category
- [ ] Streak displays actual value (not always 0)
- [ ] XP History shows real entries
- [ ] All hero cards have cyan border at 30% opacity
- [ ] All screens meet 48dp touch target requirement

---

## Phase 4: P3 - Architecture Consistency (LOW)

**Goal:** Technical debt cleanup, ViewModel standardization  
**Estimated Time:** 1 day  
**Dependencies:** Phase 3 complete  
**Risk:** MEDIUM - Pattern changes affect many files

### Phase 4 Implementation Order

| Order | ID | File | Fix Description | Dependencies |
|-------|-----|------|-----------------|--------------|
| 4.1 | **ARCH-008** | SetupQuestViewModel.kt | Migrate from manual Factory to Hilt constructor injection | None |
| 4.2 | **ARCH-008** | BillsViewModel.kt | Migrate from manual Factory to Hilt | None |
| 4.3 | **ARCH-008** | GoalsViewModel.kt | Migrate from manual Factory to Hilt | None |
| 4.4 | **ARCH-008** | SavingsEntryViewModel.kt | Migrate from manual Factory to Hilt | None |
| 4.5 | **TD-001** | *Test files* | Migrate to coroutine test helpers, remove blocking DAO methods | None |
| 4.6 | **TD-002** | SetupQuestViewModel.kt | Replace ExecutorService with `viewModelScope` | None |
| 4.7 | **DATA-004** | BudgetCategoryDao.kt | Add `getCategoryById(categoryId: Long)` method | None |
| 4.8 | **DATA-005** | DatabaseMigrations.kt + BudgetShieldDatabase.kt | Consolidate migrations, remove duplicates | None |
| 4.9 | **DATA-006** | UserSettingsDao.kt | Remove duplicate `insert()` method | None |
| 4.10 | **DATA-007** | SavingsGoalRepository.kt | Use UserSettings timezone instead of system default | None |

### Phase 4 Verification Gate

**MUST PASS before release:**
- [ ] All ViewModels use Hilt constructor injection
- [ ] `hiltViewModel()` used in all screens
- [ ] All manual Factory classes removed
- [ ] No blocking DAO methods remain
- [ ] All tests pass with coroutine test helpers

---

## Phase 5: P4 - UI Polish & Design Compliance (LOW)

**Goal:** Complete design contract compliance  
**Estimated Time:** 1 day  
**Dependencies:** Phase 4 complete  
**Risk:** LOW - Visual fixes only

### Phase 5 Implementation Order (Remaining DES defects)

| Order | ID | File | Fix Description |
|-------|-----|------|-----------------|
| 5.1 | **DES-014** | HomeScreen.kt | Action buttons: 15% opacity CyanAccent background |
| 5.2 | **DES-015** | StatsScreen.kt | "View All" themed link with CyanAccent |
| 5.3 | **DES-017** | BillEntryScreen.kt | Form title: proper header with icon background |
| 5.4 | **DES-018** | BillEntryScreen.kt | Text fields: CyanAccent border when focused |
| 5.5 | **DES-019** | BillEntryScreen.kt | Due date: add calendar trailing icon |
| 5.6 | **DES-021** | BillEntryScreen.kt | Save button: themed with CyanAccent15 background |
| 5.7 | **DES-022** | BillPaymentScreen.kt | Add back button to top bar |
| 5.8 | **DES-023** | BillPaymentScreen.kt | Currency mask with MoneyParser |
| 5.9 | **DES-024** | BillProtectedScreen.kt | Use CardHeroBackground (`#0A1F2C`) |
| 5.10 | **DES-026** | BillProtectedScreen.kt | Shield icon: 40dp circle with 20% opacity background |
| 5.11 | **DES-027** | BillProtectedScreen.kt | Tier badges: CyanAccent vs GoldAccent color coding |
| 5.12 | **DES-028** | BillsScreen.kt | Protected indicator: 20sp icon size |
| 5.13 | **DES-029** | BillsScreen.kt | FAB: 56dp circular with 15% opacity CyanAccent |
| 5.14 | **DES-030** | BudgetMenuScreen.kt | Icon backgrounds: 20% opacity circles |
| 5.15 | **DES-032** | BudgetMenuScreen.kt | Chevron indicators for tappable items |
| 5.16 | **DES-033** | BudgetMenuScreen.kt | Selected state styling |
| 5.17 | **DES-034** | GoalsScreen.kt | Progress bars with gradient styling |
| 5.18 | **DES-035** | GoalsScreen.kt | Category-to-emoji mapping verification |
| 5.19 | **DES-036** | IncomeEntryScreen.kt | Payday date: calendar icon affordance |
| 5.20 | **DES-037** | IncomeEntryScreen.kt | Custom themed frequency selector |
| 5.21 | **DES-038** | SavingsEntryScreen.kt | Currency mask with MoneyParser |
| 5.22 | **DES-039** | SavingsEntryScreen.kt | Quick add buttons: $50, $100, $500 |
| 5.23 | **DES-040** | SettingsScreen.kt | Section headers with PanelDark backgrounds |
| 5.24 | **DES-041** | SettingsScreen.kt | Themed switches with CyanAccent |
| 5.25 | **DES-042** | SetupQuestScreen.kt | Chapter cards: PanelDark with proper radius |
| 5.26 | **DES-043** | SetupQuestScreen.kt | Progress indicator: CyanAccent color, PanelDark track |
| 5.27 | **DES-044** | SetupQuestScreen.kt | Error placement per design contract |
| 5.28 | **DES-045** | SetupQuestScreen.kt | Remove card elevation (flat design) |
| 5.29 | **DES-046** | SetupQuestScreen.kt | Themed dialogs with PanelDark |
| 5.30 | **DES-047** | SetupQuestScreen.kt | "Activate My Shield" GoldAccent prominence |
| 5.31 | **DES-048** | ShieldProgressionScreen.kt | Interactive affordance for XP history |
| 5.32 | **DES-049** | ShieldProgressionScreen.kt | Level progress UI |
| 5.33 | **DES-050** | StatsScreen.kt | Legend items: 16dp spacing |
| 5.34 | **DES-051** | StatsScreen.kt | Category progress bars: PanelBorder for track |
| 5.35 | **DES-052** | TransactionDetailsScreen.kt | Dynamic category icon mapping |
| 5.36 | **DES-053** | TransactionDetailsScreen.kt | Themed destructive button |
| 5.37 | **DES-054** | TransactionDetailsScreen.kt | XP indicator: only show for bill payments |
| 5.38 | **DES-055** | TransactionDetailsScreen.kt | Error banner: PanelDark background |
| 5.39 | **DES-056** | TreasureScreen.kt | Canvas accessibility: content descriptions |
| 5.40 | **DES-057** | TreasureScreen.kt | Close button: "←" emoji (not "<") |
| 5.41 | **DES-058** | HomeScreen.kt | Month selector: verify border radius vs contract |
| 5.42 | **DES-059** | HomeScreen.kt | Activity items: color-specific 20% opacity backgrounds |
| 5.43 | **DES-060** | BillEntryScreen.kt | Screen padding: 20dp (not 16dp) |
| 5.44 | **DES-061** | BillEntryScreen.kt | Cancel action: visual distinction |
| 5.45 | **DES-062** | BillPaymentScreen.kt | Payment method: themed radio styling |
| 5.46 | **DES-063** | BillProtectedScreen.kt | Title: header styling |

---

## Risk Assessment

### High Risk Items

| ID | Risk | Mitigation |
|----|------|------------|
| DATA-002 | DB migration corruption | Test with existing user data, backup strategy, incremental migration |
| ARCH-008 | ViewModel migration breaks DI | Migrate one at a time, test each screen after migration |
| QA-001 | Root cause unclear | Add logging, verify StateFlow emission before fixing UI |
| DES-031 | ViewModel creation affects multiple screens | Create comprehensive ViewModel, test all data flows |

### Migration Risk

| Migration | From | To | Risk Level |
|-----------|------|-----|------------|
| Bills table | No category | Add category column | HIGH - Must preserve existing bills |
| ViewModel DI | Manual Factory | Hilt constructor | MEDIUM - Compile-time safe but runtime behavior may differ |
| DAO methods | Blocking | Coroutine suspending | LOW - Test-only impact |

### Testing Strategy

**After Phase 1:**
- [ ] Fresh install → Complete Setup Quest → Home
- [ ] Verify Chapter 1→2→3→4→5→6 navigation
- [ ] Verify date pickers open in Chapter 2
- [ ] Verify numeric keyboard in Chapter 3

**After Phase 2:**
- [ ] Bottom navigation on all main screens
- [ ] Navigation between main screens works
- [ ] TreasureScreen shows real data
- [ ] "View All" navigation works

**After Phase 3:**
- [ ] Existing bills survived migration
- [ ] New bills can have categories
- [ ] Categories persist and display
- [ ] Streak/XH History show real data

**After Phase 4:**
- [ ] All screens load with Hilt injection
- [ ] No manual ViewModel factories remain
- [ ] All tests pass

**After Phase 5:**
- [ ] Visual QA against design contract
- [ ] Accessibility audit
- [ ] Touch target verification

---

## Key Questions Answered

### 1. Why is QA-001 really happening?

**Root Cause:** StateFlow emission/recomposition race condition

The UI dump shows `clickable="false"` despite the button appearing enabled. This indicates:
1. **Primary issue:** `isNextEnabled` state in ViewModel emits, but recomposition doesn't trigger
2. **Secondary issue:** Navigation callback may be captured at composition time with stale state
3. **Validation issue:** `goToNextChapter()` may be checking validation that doesn't match UI state

**Required code inspection:**
- SetupQuestViewModel.kt lines 80-120: Check StateFlow emission
- SetupQuestScreen.kt line ~520: Check how `enabled` state is passed to footer
- Verify `goToNextChapter()` actually emits state changes

### 2. What's the dependency chain for bill categories?

```
DATA-002 (Migration)
    ↓ (requires)
Bill.kt Entity (add category field)
    ↓ (requires)
BillDao.kt (category query methods)
    ↓ (requires)
BillRepository.kt (category persistence)
    ↓ (requires)
BillEntryViewModel.kt (category in state)
    ↓ (requires)
ARCH-004 BillEntryScreen.kt (category UI)
```

**Implementation order:** Migration → Entity → DAO → Repository → ViewModel → UI

### 3. Which screens share bottom navigation?

**Primary screens (always visible):**
- HomeScreen (DES-001)
- BillsScreen (DES-005)
- GoalsScreen (DES-013)
- SettingsScreen (DES-020)

**Secondary screens (may have nav):**
- StatsScreen (DES-025) - accessed from Home
- TransactionDetailsScreen - detail view
- TreasureScreen (DES-031) - accessed from Home

**Recommendation:** Create reusable `BudgetShieldBottomNav` component in `components/` directory, use in all main screens.

### 4. What could break existing data?

**Database migrations (Phase 3):**
- Adding `category` column to `bills` table
- Must use nullable type with default null
- Must preserve existing rows

**Mitigation:**
1. Write migration test before implementing
2. Use Room's `Migration` class with proper SQL
3. Test on database with sample bills
4. Never use `fallbackToDestructiveMigration()` in production

---

## Implementation Engineer Notes

### Priority Order Summary

1. **Phase 1:** QA-001 is THE blocker - nothing else matters until fresh install works
2. **Phase 2:** Get navigation and ViewModels working - enables all user journeys
3. **Phase 3:** Database work - careful with migrations
4. **Phase 4:** Architecture cleanup - low risk once app works
5. **Phase 5:** Polish - visual fixes only

### Daily Checkpoints

- **Day 1:** Phase 1 complete (QA-001 working)
- **Day 2-3:** Phase 2 complete (navigation + ViewModels)
- **Day 4-5:** Phase 3 complete (categories + real data)
- **Day 6:** Phase 4 complete (DI migration)
- **Day 7:** Phase 5 complete (polish)

### Blockers

If Phase 1 takes longer than 1 day:
- Escalate to Lead Architect
- May need deeper investigation of Navigation 3 state flow

---

*Document generated by Lead Architect Agent*  
*Review complete: All 71 defects analyzed and sequenced*
