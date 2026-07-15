# Changelog

### TASK 2 Final Document Repair
- Completed docs/SCREEN_MAP.md with full specifications for all 13 required destinations:
  - Stats, Goals, Settings, Income Entry, Bill Entry, Bill Payment
  - Savings Entry, Transaction Details, Shield Progression, plus existing screens
- Repaired docs/DATA_MODEL_PLAN.md with architecture-independent decisions:
  - Long cents for all monetary values (no Float/Double)
  - Schedule vs occurrence separation (IncomeSchedule/IncomeOccurrence, BillSchedule/BillOccurrence)
  - Immutable transaction ledger with audit trail
  - BillPaymentAllocation for partial payments
  - SavingsGoal separated from SavingsContribution
  - Removed unsafe isPaid from recurring templates
- Clarified docs/SAFE_NOW_RULES.md with precise decisions:
  - Planning horizon: through latest protected obligation, minimum through end of next month
  - Same-day ordering: confirmed income available for same-day bills
  - Overdue bills treated as due today
  - Partial payment handling (remaining due only)
  - Unprotected bills excluded from Safe Now
  - Unconfirmed income never protects bills
  - Added 9 worked examples with integer cents calculations
- Updated docs/TEST_PLAN.md with future deterministic test categories for all Safe Now examples
- Updated DECISIONS.md with architecture-independent data decisions
- Updated PROJECT_STATE.md and TASK_QUEUE.md with final Task 2 status
- Confirmed no application code, resources, Gradle files, or reference images changed
- Task 3 remains NOT STARTED

### TASK 2 Logic and Metadata Correction
- Removed nonexistent 8f115f4 reference from all documents
- Corrected docs/DATA_MODEL_PLAN.md data model contradictions:
  - Account.currentBalanceCents: documented as derived query/result only (not stored mutable field)
  - IncomeOccurrence.scheduleId: changed to Long? for manual one-time income without schedule
  - IncomeOccurrence.receivedDate and receivedAmountCents: String? and Long? (null until received)
  - BillOccurrence.scheduleId: changed to Long? for manual one-time bills
  - BillOccurrence.isGenerated: added Boolean to distinguish generated vs manual
  - BillOccurrence.status: documented as derived from dueDate/remainingDue/payments (not stored)
  - SavingsContribution.goalId: Long? for general savings without specific goal
  - Added uniqueness constraints on (scheduleId, date) for duplicate occurrence prevention
  - Documented that schedule deletion preserves historical occurrences and ledger-linked records
- Corrected docs/SAFE_NOW_RULES.md:
  - Removed unimplemented "arriving after cutoff" user feature mention
  - Fixed Example 9 shortage calculation: spending $800 from $1000 leaves $200; $500 income = $700; $900 bill = -$200; shortage is $200 (20000 cents), not $100
- Corrected docs/TEST_PLAN.md:
  - Split "Income After Bill" test into two cases: Income Before Bill (CAN protect) and Income After Bill (CANNOT protect)
  - Updated Example 9 expected shortage to 20000 cents
  - Added data-model tests for nullable fields, derived values, and duplicate prevention
- Corrected DECISIONS.md: removed "after cutoff" reference from Same-Day Income Ordering
- No application code, resources, Gradle files, or reference images changed
- Task 3 remains NOT STARTED

### TASK 3 — Android Architecture and Navigation Foundation
- Migrated from XML/AppCompat placeholder to single-activity Jetpack Compose
- Updated Gradle: AGP 8.13.2, Gradle 8.13, Kotlin 2.2.21, compileSdk 36
- Removed old shell: HomeActivity.kt, activity_home.xml deleted
- Created MainActivity.kt (ComponentActivity) with setContent
- Created BudgetShieldApp.kt Application class
- Added Navigation 3 with type-safe serializable routes
- Created 13 destination routes: SetupQuest, Home, Treasure, Stats, Goals, Settings, IncomeEntry, BillEntry, BillPayment, SavingsEntry, TransactionDetails, BillProtected, ShieldProgression
- Created BudgetShieldNavigation.kt with NavHost and route wiring
- Created 13 placeholder screens with ARCHITECTURE FOUNDATION labels
- Implemented back-stack rules: Setup Quest completion replaces stack
- Created BudgetShieldTheme.kt minimal dark theme placeholder
- Created NavigationSmokeTest.kt with 8 automated navigation tests
- Created .github/workflows/android-debug.yml for CI
- Created docs/ARCHITECTURE.md documenting decisions and structure
- Created qa/TASK3_NAVIGATION_QA.md with reachability matrix
- Updated DECISIONS.md with Task 3 architecture/toolchain decisions
- Build: BUILD SUCCESSFUL
- Task 4 remains NOT STARTED

## [Unreleased]

### TASK 2 — Product/Design/Project Contracts
- Created README.md
- Created PROJECT_STATE.md
- Created TASK_QUEUE.md
- Created DECISIONS.md
- Created KNOWN_BUGS.md
- Created QUALITY_GATES.md
- Created AGENT_RULES.md
- Created docs/PRODUCT_CONTRACT.md
- Created docs/SAFE_NOW_RULES.md
- Created docs/DESIGN_CONTRACT.md
- Created docs/SCREEN_MAP.md
- Created docs/DATA_MODEL_PLAN.md
- Created docs/TEST_PLAN.md
- Established reference image directory

### TASK 2 Correction — Reference Images Added
- Added `docs/reference/home-reference.png` (main dashboard with Safe Now and treasure chest)
- Added `docs/reference/setup-quest-reference.png` (Setup Quest Chapter 1 of 6, Money In)
- Added `docs/reference/bill-protected-reference.png` (Bill Protected achievement overlay)
- Verified formats: all PNG, 864x1536 pixels
- Verified SHA-256 hashes
- Updated PROJECT_STATE.md: Task 2 marked COMPLETE, all reference images tracked
- Updated TASK_QUEUE.md: Task 2 marked COMPLETE with evidence
- Task 3 remains NOT STARTED

## [1.0.0-shell] — TASK 1 Complete

### TASK 1 — Clean Project Shell
- Fresh Android/Kotlin project
- Package: com.toonai.budgetshield
- Dark theme (Material3)
- Placeholder HomeActivity with clean build message
- No legacy code from BudgetBuddy/Budget-App/Treasure projects
- Commit: 6ce7d9af3753f070a2d842d2064ca3ccafcfb629
