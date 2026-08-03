# Budget Shield Data Model Plan

**Status:** APPROVED — owner-approved with the recommended decisions on 2026-08-02

**Authority:** Persistence semantics, entity ownership, money/date representation, ledger rules, and migration expectations

**Current database:** Room database version 4, file `budget_shield_db_v4`

## 1. Data principles

1. Store money as `Long` integer cents. Never persist `Float` or `Double` money.
2. Store business dates as validated ISO `YYYY-MM-DD`; month keys as `YYYY-MM`.
3. Distinguish a recurring schedule/template from its dated occurrences.
4. Treat transactions as an append-only audit trail. Corrections create new ledger events.
5. Make multi-record financial operations atomic Room transactions.
6. Preserve supported user data across schema upgrades.
7. Export and version Room schemas in source control after sensitive-value review.
8. Use explicit foreign-key/relationship policy rather than orphan-prone nullable IDs.
9. Derive totals from persisted source records; do not persist duplicated aggregates without a reconciliation rule.
10. A database failure or inconsistent record fails visibly; it must not silently become a valid zero.

## 2. Current entity inventory

Room version 4 declares ten entities.

### 2.1 `user_settings` — `UserSettings`

Single-row configuration and balance state; expected primary key `id = 1`.

| Field | Type | Meaning / rule |
|---|---|---|
| `id` | Long | Fixed singleton key. |
| `isFirstRunComplete` | Boolean | Setup Quest completion gate. |
| `currency` | String | ISO-like currency code; currently defaults to USD. |
| `timezone` | String | IANA timezone; current default `America/Phoenix`. |
| `notificationsEnabled` | Boolean | Notification preference. |
| `dailyReminderTime` | String? | Requires an approved time format. |
| `billReminderDaysBefore` | Int | Non-negative reminder lead. |
| `planningHorizonMonths` | Int | Positive Safe Now horizon count; default 2. |
| `cashOnHandCents` | Long | Cleared spendable cash. |
| `savingsBalanceCents` | Long | Savings kept separate from Safe Now cash. |
| `selectedMonth` | String | `YYYY-MM`; empty default must be normalized before use. |
| `setupChapter` | Int | 0 not started; 1–6 active; 7 complete. |
| `createdAt`, `updatedAt` | Long | Epoch-millisecond audit timestamps. |

### 2.2 `setup_drafts` — `SetupDraft`

Single-row resumable Setup Quest draft, expected `id = 1`.

Fields: current chapter; cash cents; income name, cents, next-payday date, frequency, and confirmation; savings cents; food budget cents; wants budget cents; update timestamp.

The current draft does not persist the Chapter 3 bill list. If bills are saved directly while setup remains incomplete, the lifecycle/rollback semantics must be explicitly tested and documented.

On successful setup activation, accepted values are persisted to their destination tables, first run becomes complete, and the draft is cleared in one logical transaction.

### 2.3 `income_schedules` — `IncomeSchedule`

| Field group | Current fields | Contract |
|---|---|---|
| Identity | `id`, `name` | Name is user-visible and nonblank. |
| Money | `amountCents` | Positive exact cents. |
| Schedule | `nextPayday`, `nextPaydayDate`, `frequency` | One canonical next-payday field is required; the alias duplication must be removed through a migration. |
| Eligibility | `isConfirmed`, `isActive` | Both true before income can affect Safe Now. |
| Audit | `createdAt`, `updatedAt` | Updated on material change. |

The current schema does not fully represent semimonthly anchors, end-of-month adjustment, or generated occurrences. A complete recurring model needs either explicit recurrence fields plus deterministic generation or a separate occurrence table.

### 2.4 `bills` — `Bill`

Current fields: `id`, name, icon, amount cents, paid amount cents, due date, protected flag, paid flag, and creation timestamp.

Invariants:

- Name nonblank.
- `amountCents > 0`.
- `0 <= paidAmountCents <= amountCents` unless an overpayment policy is approved.
- Due date is a valid ISO date.
- `remainingDueCents = max(0, amountCents - paidAmountCents)`.
- `isPaid` agrees with a zero remainder.
- Protected status identifies obligations included in Safe Now.

The current entity describes a dated obligation, not a full recurring bill schedule. Recurring schedule and generated occurrence separation remains required by `DECISIONS.md`.

### 2.5 `budget_categories` — `BudgetCategory`

Month-scoped category with unique `(name, monthKey)` index.

Fields: identity, name, month key, planned cents, spent cents, category type, active state, icon, and timestamps.

Invariants:

- Valid `YYYY-MM` key.
- Planned and spent amounts are non-negative.
- Category type uses approved vocabulary: food, wants, other.
- Spending totals reconcile to ledger transactions or have a documented source-of-truth rule.

The current mutable `spentAmountCents` duplicates information that may also be derived from transactions. The owner/architecture decision must choose the source of truth and atomic update strategy.

### 2.6 `transactions` — `Transaction`

Fields: ID, type, title, optional description, signed cents, category, icon, related bill/income IDs, XP eligibility/amount, protected state, transaction date, and creation timestamp.

Approved types currently include income, bill payment, savings, spending, and refund.

Contract rules:

- The ledger is append-only after creation.
- `amountCents` sign follows one documented convention per type.
- Related entity IDs are valid when present.
- A correction adds a reversal/correction transaction linked to the original; it does not overwrite or delete history.
- XP derived from a transaction cannot be awarded twice.

Current DAO/repository update and delete operations conflict with this contract and require removal, restriction to recovery tooling, or explicit owner revision.

### 2.7 `savings_goals` — `SavingsGoal`

Fields: ID, name, icon, target cents, current cents, optional deadline, completed state/timestamp, priority, emergency-fund flag, and creation timestamp.

Invariants:

- Target is positive.
- Current amount is non-negative.
- Deadline, if present, is valid ISO.
- Completion state agrees with the approved completion rule.
- Savings goal total and `UserSettings.savingsBalanceCents` must have a defined relationship; they must not drift as independent claims about the same money.

### 2.8 `xp_entries` — `XpEntry`

Append-only gamification ledger containing ID, XP amount, activity type, description, optional related entity ID, entry date, and creation timestamp.

Qualifying activity types include bill protection/payment, income, savings, daily streak, weekly review, goal completion, setup completion, and budget-on-track.

Each source action requires an idempotency rule so retries, recreation, or process death cannot award duplicate XP.

### 2.9 `achievements` — `Achievement`

String-keyed definitions and persisted state: name, description, icon, XP reward, category, unlock flag/timestamp, progress, and target.

Achievement definitions require a versioning/seed policy. Seeding a newer definition set must preserve existing unlocks and avoid duplicate XP awards.

### 2.10 `user_streaks` — `UserStreak`

Singleton derived state: current/best streak, last activity date, active-today flag, total active days, and update timestamp.

The derivation must specify timezone, qualifying activity, late/missed day behavior, clock changes, and idempotent daily updates. Persisted summary must be reconstructible from its source activity or have a reconciliation test.

## 3. Required future separation of schedules and occurrences

The approved architecture distinguishes templates from dated facts. A release-ready recurrence model should add:

### `income_occurrences`

- `id`
- `scheduleId`
- `occurrenceDate`
- `amountCents`
- confirmation/status
- source schedule version or generation key
- created/updated timestamps
- unique generation key preventing duplicates

### `bill_schedules`

- Template name, default amount/category/icon
- Recurrence rule and anchors
- active start/end dates
- next-generation marker

### `bill_occurrences`

- Schedule link (nullable for one-time bills)
- due date and original amount
- paid total and protection state
- immutable historical link to transactions
- unique schedule/date generation key

Historical occurrences linked to transactions remain unchanged when a schedule is edited.

Adding these tables is a planned schema evolution, not authorized by this documentation task.

## 4. Relationships and ownership

```text
UserSettings (singleton)
  ├─ controls first-run state, cash, savings, selected month, horizon
  └─ SetupDraft (singleton, temporary first-run state)

IncomeSchedule ──< IncomeOccurrence (planned)
BillSchedule (planned) ──< BillOccurrence/current Bill
Bill/current occurrence ──< Transaction (bill payment)
BudgetCategory (name + month) ──< Transaction (spending category/month)
SavingsGoal ──< Transaction (savings contribution; explicit link needed)
Transaction/activity ──< XpEntry
Achievement and UserStreak derive from durable activity/XP facts
```

Room foreign keys are not currently declared for the nullable related IDs. Before release, choose and test deletion behavior (`RESTRICT`, `SET NULL`, or archival); cascade deletion of financial history is not acceptable.

## 5. Repository transaction boundaries

The following operations must be atomic:

- Setup activation: settings + income + bills + budgets + draft clear + setup XP
- Bill payment: bill remainder/state + ledger transaction + XP/achievement/streak effects
- Savings contribution: goal + savings balance (if applicable) + ledger + XP
- Log spending: budget usage + ledger + XP/streak if applicable
- Achievement unlock: unlock state + XP reward with idempotency

An operation either commits all required records or none. UI success is emitted only after commit.

## 6. Money and date representation

### Money

- Persist `Long` cents.
- Parse decimal text with exact integer arithmetic.
- Reject negative values where the operation does not allow them, malformed values, more than two decimal places, zero where a positive amount is required, and overflow.
- Format with the configured currency/locale policy; current release scope is USD.

### Dates

- Persist business dates as ISO strings only after strict calendar validation.
- Persist timestamps as epoch milliseconds where ordering/audit time is needed.
- Calculate “today” using the approved user timezone, not an undocumented device/server mix.
- Month keys use `YYYY-MM` and real month boundaries.

## 7. Current migrations

| Migration | Intended change | Current concern |
|---|---|---|
| 1 → 2 | User settings, income schedules, budget categories | A duplicate implementation exists; SQL columns appear inconsistent with current entity fields. |
| 2 → 3 | Setup draft | Must verify actual historical v2 schema and resume behavior. |
| 3 → 4 | Transactions, XP, achievements, savings goals, streaks | Must verify against current entity schemas and supported upgrade artifacts. |

The database currently uses `exportSchema = false` and `fallbackToDestructiveMigrationOnDowngrade()`. Neither is release-approved.

## 8. Migration policy

Before a release candidate:

1. Define the oldest supported production database version.
2. Export current and future Room schemas to a reviewed repository directory.
3. Consolidate each migration path into one source of truth.
4. Add `MigrationTestHelper` tests for every supported direct and chained path.
5. Seed realistic records in every old table, migrate, and verify values and constraints.
6. Test app launch and financial calculations after migration.
7. Remove destructive downgrade or replace it with an owner-approved, user-visible policy.
8. Back up/restore or rollback-test the signed candidate according to release policy.

No migration is accepted merely because an empty database opens.

## 9. Data validation and failure behavior

- DAO/repository boundaries validate money, date, enum vocabulary, and relationship IDs.
- Database exceptions propagate to explicit UI error state.
- Unknown enum strings are handled safely and flagged for repair.
- Orphaned ledger relationships do not erase the financial event.
- Duplicate generated occurrences and duplicate XP are prevented with database uniqueness/idempotency keys.
- Source inconsistencies are logged without leaking sensitive values.

## 10. Backup, privacy, and deletion

Owner decisions are required for Android Auto Backup, device transfer, export, account/data reset, and support diagnostics. Until approved:

- Do not claim cloud backup or cross-device recovery.
- Do not transmit financial records.
- Do not put databases or personal financial exports into CI artifacts or Git.
- A destructive reset requires explicit confirmation and clear scope.

## 11. Required persistence verification

- Fresh database creation at current version
- Upgrade from each supported version with nontrivial data
- Activity recreation and process death during every multi-step entry flow
- Force-stop/relaunch after committed and interrupted operations
- Atomic rollback on injected repository/database failure
- Partial bill payment and associated ledger reconciliation
- Savings, budget, transaction, XP, achievement, and streak reconciliation
- Duplicate-action/idempotency tests
- Safe Now equality before/after relaunch and supported upgrade
- Invalid date/money/relationship records fail safely
- Database downgrade policy test
- Schema identity/hash recorded for the release candidate

## 12. Current gaps requiring Task 20 work

Task 20.2 at `fd526e1` added two nullable semimonthly anchor fields to `IncomeSchedule` and `SetupDraft`, introduced a row-preserving version-5 migration, and removed the destructive-downgrade opt-in. Legacy semimonthly rows intentionally retain null anchors so the app can request repair instead of inventing paydays.

- Room schema export is disabled.
- Complete downgrade-failure and versions-1-through-current migration evidence remains required even though destructive downgrade fallback is now removed.
- Migration 1→2 is duplicated.
- Historical migration SQL appears inconsistent with current model columns.
- Schedule and occurrence concepts are still combined; two-anchor recurrence is now representable, but a durable generated-occurrence model remains open.
- Transaction mutation/deletion APIs conflict with the immutable ledger.
- Related IDs lack enforced foreign-key policy.
- Setup draft does not contain draft bills.
- Savings balance and sum of goals have no explicit reconciliation rule.
- Budget spent totals and spending transactions can become competing sources of truth.
- Idempotency keys for XP, achievements, and generated recurrence are absent.
- Some production screens bypass the exact money parser.

This plan documents the target and risks; Task 19 does not change the schema.

## 13. Approved direction and explicit deferrals

The owner approved:

1. Database versions 1 through current must migrate without data loss.
2. Destructive downgrade is forbidden.
3. Monthly recurrence clamps a missing anchor day to the month's final valid day.
4. Semimonthly/twice-monthly schedules store two user-configured anchors.
5. Re-onboarding/data-reset controls remain hidden until a preservation/deletion policy is approved and implemented.

Android backup/device transfer, savings-goal allocation semantics, budget aggregate source-of-truth details, ledger correction UX/retention, data export, and support diagnostics remain explicitly deferred. Task 20 may design and test an implementation consistent with this contract, but none may be claimed as next-beta functionality without separate owner approval where the contract requires it.
