# Decisions

## Architecture-Independent Data Decisions

### Monetary Storage
- All monetary values stored as **Long cents** (integer minor units)
- Never use Float or Double for money
- Display: `$X.YY` format derived from cents / 100

### Schedule vs Occurrence Separation
- **Schedules** define recurrence rules and templates
- **Occurrences** represent dated obligations generated from schedules
- **Transactions** are immutable ledger events
- Historical occurrences linked to transactions remain immutable when schedules change

### Immutable Transaction Ledger
- Transaction ledger is the audit trail of truth
- Corrections create new transactions, never mutate existing ones
- Historical activity derived from ledger, not mutable current-state flags

### Same-Day Income Ordering
- Confirmed income dated on a bill due date is available to protect that bill
- Date-level ordering: income processed before obligations on the same date

### Safe Now Planning Horizon
- Planning horizon extends from today through the latest protected unpaid obligation
- Minimum horizon: through end of next calendar month
- Configurable via UserSettings.planningHorizonMonths

## Product Decisions

### App Name
**Budget Shield** — Native Android budgeting application presented as a game.

### Core Concept
A budgeting app that answers: "How much money can I safely spend right now while still paying every protected bill by its due date?"

### Primary Result Name
**Safe Now** — This term is fixed and cannot be renamed.

### Visual Style
Premium dark fantasy finance game aesthetic.

## Technical Decisions

### Lifecycle Version Compatibility
- **Lifecycle 2.10.0** is the intentional compatible version for AGP 8.13.2 and compileSdk 36
- Lifecycle 2.11.0 requires compileSdk 37 and AGP 9.2.0, which is outside Task 3 scope
- This decision prevents unnecessary project risk while maintaining full functionality

### Platform
Native Android (Kotlin)

### Package
`com.toonai.budgetshield`

### Min SDK
26 (Android 8.0)

### Target SDK
35

### Compile SDK
36

### Architecture
Single-activity Jetpack Compose with Navigation 3 type-safe routes

### Build Foundation
- Android Gradle Plugin: 8.13.2
- Gradle: 8.13
- Kotlin: 2.2.21 (Android, Compose, Serialization plugins)
- Java toolchain: 17

### Compose Foundation
- Compose BOM: 2026.06.00
- Activity Compose: 1.13.0
- Lifecycle: 2.10.0 (2.11.0 requires compileSdk 37)
- Navigation 3: 1.1.4 (androidx.navigation3:navigation3-runtime and navigation3-ui)
- Kotlinx Serialization: 1.9.0

### Theme
Task 3: Minimal dark Material3 theme placeholder
Task 4: Premium dark fantasy finance game aesthetic (teal/cyan primary, gold accents, treasure/shield imagery)

### Storage
Local persistent storage (Room/SQLite planned for future tasks)

### Navigation Strategy
- Type-safe routes with `@Serializable`
- **14 destinations:** SetupQuest, Home, Treasure, Bills, Stats, Goals, Settings, IncomeEntry, BillEntry, BillPayment, SavingsEntry, TransactionDetails, BillProtected, ShieldProgression
- Back-stack managed by Navigation 3
- Start route: SetupQuest (temporary, until first-run persistence)

---

## Screen Ownership Correction (2026-07-18)

### Decision: Separate Treasure from Bills

**Problem:** The original design incorrectly defined Treasure as the bills list and protected money destination. This caused Home's "Pay Bill" button to navigate to bill entry instead of bill management, and overloaded Treasure with both rewards hub and bill management responsibilities.

**Decision:** Create a dedicated "Bills" route that owns:
- Persisted bill list display
- Protected money totals and calculations
- Add Bill action
- Pay Bill action
- Transaction history

Treasure is now exclusively the **gamified rewards hub** containing:
- Treasure Chests (collectibles)
- Achievements
- XP and Shield Level progress
- Streaks
- Reward History

### Corrected Entry Points

| Action | Entry Route | Target Route |
|--------|-------------|--------------|
| Home → Pay Bill | Home | Bills (NOT BillEntry) |
| Bills → Add Bill | Bills | BillEntry |
| Bills → Pay Bill | Bills | BillPaymentWithId |
| Bill Entry Save | BillEntry | Bills (NOT Treasure) |
| Home → Treasure | Home | Treasure (rewards hub) |

### Screen Ownership Summary

| Screen | Purpose | Data Dependencies |
|--------|---------|-------------------|
| Home | Dashboard, Safe Now, quick actions | Cleared cash, protected count |
| **Bills** | **Bill management, payments, protected money** | BillRepository, occurrences |
| **Treasure** | **Rewards hub: chests, achievements, XP, streaks** | Shield XP, streaks, collectibles (when implemented) |
| Stats | Read-only financial statistics | Transactions, aggregated data |
| Goals | Read-only goal progress display | Savings goals, contributions |

### Navigation Registry Update
- `BudgetShieldRoute.kt`: Added `Bills` object route
- `BudgetShieldRouteRegistry`: DESTINATION_COUNT updated from 13 to 14
- `BudgetShieldNavigation.kt`: Home's onNavigateToBillEntry now routes to `Bills`
- `BillEntryScreen`: onNavigateToTreasure callback now returns to `Bills`

### Visual Requirements
- Bills: Dark premium finance-game aesthetic, "Bills & Payments" header
- Treasure: Dark fantasy rewards hub, "Treasure Vault" header, chest/achievement themed

### Test Updates
- `RouteCompletenessTest`: Updated to expect 14 routes, verify Bills and Treasure are distinct
- `NavigationSmokeTest`: Updated to verify Bills destination reachable via Pay Bill, Treasure shows rewards hub

This decision was made to correct the architectural confusion between game rewards (Treasure) and financial obligations (Bills), enabling each screen to have a clear, single responsibility.
