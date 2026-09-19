# Budget Shield Screen Map

**Status:** APPROVED — owner-approved with the recommended decisions on 2026-08-02

**Authority:** Screen ownership, route inventory, navigation entry/exit rules, and persistent-footer behavior

## 1. Navigation model

Budget Shield is a single-activity Jetpack Compose application using type-safe Navigation 3 `NavKey` routes.

The production registry contains 18 canonical destination entries. The route file contains an additional parameterized `BillPaymentWithId(billId)` variant accepted by the registry validator, so code comments that still say “14 destinations” are stale.

The source tree contains 18 `*Screen.kt` files. `TransactionHistoryScreen` is registered as a Home-owned secondary route.

## 2. Persistent footer

The footer is visible on every registered destination except Setup Quest. Its fixed order and ownership are:

| Order | Tab | Route | Purpose |
|---:|---|---|---|
| 1 | Home | `Home` | Safe Now dashboard and financial actions |
| 2 | Treasure | `Treasure` | Rewards, XP, achievements, streaks |
| 3 | Stats | `Stats` | Read-only financial statistics |
| 4 | Goals | `Goals` | Savings goals and progress |
| 5 | Settings | `Settings` | Preferences and configuration |

All financial secondary screens currently select Home as their owning footer tab. Setup Quest has no selected tab and no footer.

## 3. Authoritative route inventory

| # | Route | Screen | Owner | Primary purpose | Expected entry | Expected exit |
|---:|---|---|---|---|---|---|
| 1 | `SetupQuest` | `SetupQuestScreen` | None | Six-chapter first-run setup | Fresh install/incomplete setup; approved reset flow | Successful activation replaces the stack with Home |
| 2 | `Home` | `HomeScreen` | Home | Safe Now dashboard, selected month, quick actions, activity | Initial destination after completed setup; footer | Back exits at root; secondary actions push owned routes |
| 3 | `Treasure` | `TreasureScreen` | Treasure | Real-data XP, achievements, streaks, chests/reward history | Footer; Home entry point if shown | Footer or Back |
| 4 | `Bills` | `BillsScreen` | Home | Bill list, protected totals, add/pay actions | Home “Pay Bill”; Budget Menu | Home, Bill Entry, parameterized Bill Payment, Transaction Details |
| 5 | `Stats` | `StatsScreen` | Stats | Financial aggregates and category breakdown | Footer | Transaction History or footer |
| 6 | `Goals` | `GoalsScreen` | Goals | Savings goals, streaks, shield summary | Footer | Savings Entry, Transaction History, Shield Progression, footer |
| 7 | `Settings` | `SettingsScreen` | Settings | Preferences and monthly budget configuration | Footer; Budget Menu | Footer, Transaction History, approved Setup Quest/reset flow |
| 8 | `IncomeEntry` | `IncomeEntryScreen` | Home | Add or maintain income schedule | Home quick action; Budget Menu | Home after save/cancel; Setup Quest only in explicit setup context |
| 9 | `BillEntry` | `BillEntryScreen` | Home | Add bill obligation | Bills | Bills after save/cancel; Home where explicitly offered |
| 10 | `BillPayment` | `BillPaymentScreen` | Home | Generic bill-payment flow without preselected ID | Direct internal route only | Bill Protected on success; Back on cancel |
| 11 | `SavingsEntry` | `SavingsEntryScreen` | Home | Record savings/goal contribution | Home; Goals; Budget Menu | Goals or Home |
| 12 | `TransactionDetails(transactionId?)` | `TransactionDetailsScreen` | Home | Inspect one transaction; optional ID shows an honest empty state | Home activity and Transaction History rows | Back, Transaction History, or footer destinations |
| 13 | `BillProtected` | `BillProtectedScreen` | Home | Confirm successful protection/payment | Bill Payment | Home, Treasure, or Shield Progression |
| 14 | `ShieldProgression` | `ShieldProgressionScreen` | Home | Shield level and XP progression | Home, Goals, Bill Protected | Back/footer |
| 15 | `BudgetMenu` | `BudgetMenuScreen` | Home | Menu for Bills, Income, Savings, Settings | Home menu action | Selected destination or Back/dismiss |
| 16 | `LogSpending` | `LogSpendingScreen` | Home | Record categorized spending | Home; Budgets | Back after completion/cancel |
| 17 | `Budgets` | `BudgetsScreen` | Home | View monthly budget categories and usage | Home | Log Spending or Back |
| 18 | `TransactionHistory` | `TransactionHistoryScreen` | Home | Inspect transaction ledger history | Home activity, Bills, Stats, Goals, Settings | Back or Transaction Details |

### Accepted parameterized variant

| Route | Screen | Rule |
|---|---|---|
| `BillPaymentWithId(billId)` | `BillPaymentScreen` | Preferred Bills-to-payment route. The ID must resolve to a real bill; missing/deleted IDs produce an explicit error and safe Back path. |

## 4. Implemented route status

`TransactionHistoryScreen` is registered as `TransactionHistory` and reachable from Home-owned history controls.

## 5. Route ownership rules

### Home-owned secondary routes

These keep Home selected in the footer:

- Bills
- Income Entry
- Bill Entry
- Bill Payment / Bill Payment With ID
- Savings Entry
- Transaction Details
- Transaction History
- Bill Protected
- Shield Progression
- Budget Menu
- Log Spending
- Budgets

### Dedicated top-level routes

Treasure, Stats, Goals, and Settings select their own footer tabs.

### Setup Quest

Setup Quest is outside the main application shell:

- Footer hidden
- Cannot be bypassed on first run
- Successful completion clears/replaces the previous stack with Home
- Back from Home cannot return to setup

## 6. Primary user flows

### First run

```text
Launch → Loading/first-run check → Setup Quest chapters 1–6
      → Activate → replace stack → Home
```

### Protect and pay a bill

```text
Home → Bills → Add Bill → Bills
Home → Bills → select Pay → BillPaymentWithId
     → successful payment/protection → Bill Protected → Home/Treasure/Shield Progression
```

### Add income

```text
Home quick action or Budget Menu → Income Entry → Save → Home
```

### Save money

```text
Home, Goals, or Budget Menu → Savings Entry → Save → Goals or Home
```

### Track spending and budgets

```text
Home → Budgets → Log Spending → Back to Budgets
Home → Log Spending → completion returns to prior screen
Home → Transaction History → Transaction Details
```

### Explore progress

```text
Footer → Treasure
Footer → Goals → Shield Progression
Home → Shield Progression
```

## 7. Screen-state contract

Every data-backed screen must define and visibly handle:

- Loading
- Populated/success
- Empty
- Validation failure
- Persistence/network-independent local error
- Stale or missing route identifier, when parameterized

A sample or preview value may appear only in design preview/test code. Production empty states must not masquerade as real user data.

## 8. Back-stack rules

- Top-level navigation uses single-top behavior by route value.
- Selecting a different top-level tab adds or activates the intended destination without duplicate consecutive entries.
- Nested Back removes the current destination and returns to the prior destination.
- Back from a sole Home root exits the activity.
- Setup completion clears all setup/nested entries before adding Home.
- Parameterized routes require deliberate equality behavior: navigating to a second transaction/bill ID must not be suppressed merely because the route class matches.

`navigateSingleTop` compares full route values, so identical consecutive destinations are suppressed while distinct parameterized IDs remain reachable.

## 9. Screen-specific acceptance notes

### Home

- Safe Now is live, not hard-coded.
- Month controls are functional.
- Quick actions navigate to the correct owner screens.
- Activity items resolve to actual transaction identifiers.
- Positive, zero, shortage, empty, loading, and error states are testable.

### Bills and Bill Payment

- Bills and Treasure remain separate responsibilities.
- Every displayed bill comes from persistence.
- Partial/full payments validate exact cents and refresh the bill list/Safe Now.
- Missing bill IDs do not fall back to fabricated bill content.

### Treasure, Goals, Stats, and Shield Progression

- All numbers, achievements, streaks, charts, and progress derive from persisted records.
- Empty states are honest.
- No “coming soon” or fake locked reward is presented as completed functionality in a claimed beta.

### Transaction Details/History

- A details route normally carries a real transaction ID.
- Missing ID uses an honest selection/empty state, not a sample “Rent Payment.”
- Editing/deleting must conform to the immutable-ledger decision.
- History is reachable and tested as a Home-owned route.

### Settings

- Preference changes persist and survive relaunch.
- Reset/re-onboarding requires confirmation and a defined data-retention outcome.
- Transaction History control opens the registered history route.

## 10. Accessibility and responsive behavior

For every screen and state:

- Interactive targets are at least 48dp unless a documented platform exception applies.
- Controls have accurate roles, labels, and content descriptions.
- Reading and focus order follows the visible task order.
- Status is not communicated by color or emoji alone.
- Text remains usable at approved accessibility font scales.
- Content respects status/navigation bars, display cutouts, and IME insets.
- The footer does not obscure scrollable content.
- Landscape, narrow width, and long localized text do not clip core actions.

## 11. Required navigation tests

- Every registered route renders without an unknown-screen fallback.
- Registry count and route validator agree.
- Footer order and ownership mapping are exact.
- Setup footer hidden; post-setup footer visible.
- Setup completion leaves a one-entry Home stack.
- Back behavior from every secondary route.
- Repeated top-level tap does not duplicate the top entry.
- Two different parameterized IDs navigate correctly.
- Invalid/deleted IDs show a recoverable error.
- All visible buttons and footer items reach the documented destination.
- Transaction History is reachable and tested for the next beta.

## 12. Approved navigation decisions and current contradictions

The owner approved the five-tab footer in the order Home, Treasure, Stats, Goals, Settings and required Transaction History to become a reachable, tested next-beta route. The following implementation contradictions remain Task 20 work:

- Route comments saying 14 destinations were stale; the registry now contains 18 canonical entries.
- `BillPaymentWithId` is valid but omitted from `allDestinations` as a separate entry, which must be documented consistently.
- `TransactionHistoryScreen` is now registered and wired from Settings/history controls.
- Some Home callbacks are intentionally empty because their controls are hidden/removed; tests must confirm they cannot appear enabled.
- `navigateSingleTop` now compares route values so distinct parameterized IDs are not suppressed.
- Transaction Details permits no ID only as an honest empty state; it must not show sample transaction data.
- `BillProtectedScreen` is invoked without passing the paid bill ID, so confirmation detail can fall back to generic values.

This draft makes those gaps visible; it does not approve them.
