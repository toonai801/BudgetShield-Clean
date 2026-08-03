# Budget Shield Product Contract

**Status:** DRAFT — reconstructed from the current recovery branch on 2026-08-02; owner approval required

**Applies to:** Budget Shield Android application (`com.toonai.budgetshield`)

**Authority after approval:** Product intent, vocabulary, scope, and user-visible behavior

**Does not prove:** That the current implementation satisfies this contract

## 1. Product promise

Budget Shield is a native Android budgeting application presented as a game. It helps a person protect upcoming obligations and answers one primary question:

> How much money can I safely spend right now while still paying every protected bill by its due date?

The answer is called **Safe Now**.

Budget Shield must make the answer understandable, conservative, and traceable. It must never imply that a projected amount is guaranteed, and it must not silently count unconfirmed income or ignore a protected obligation.

## 2. Fixed vocabulary

The following names are product requirements:

- Application: **Budget Shield**
- Primary result: **Safe Now**
- Protected obligation: **protected bill**
- First-run experience: **Setup Quest**
- Gamification progress: **Shield XP** and **Shield Level**
- Rewards area: **Treasure** or **Treasure Vault**

Safe Now must not be renamed to “cash available,” “balance,” “spending money,” “disposable income,” or “cash on hand.” Those concepts are not interchangeable.

## 3. Product principles

1. **Protection before permission.** Safe Now is limited by the worst projected balance across the planning horizon, not merely today's cash.
2. **Confirmed facts only.** Unconfirmed or inactive income cannot increase Safe Now.
3. **Exact money.** Monetary values use integer cents. User input must not be converted through `Float` or `Double` in production financial paths.
4. **Real calendar rules.** Dates are validated as actual calendar dates and stored in ISO `YYYY-MM-DD` form.
5. **No fabricated financial state.** Production screens may not present sample bills, transactions, rewards, balances, or progress as if they belong to the user.
6. **Explain risk.** A shortage state identifies the amount, first failing date, and affected obligations when those values can be determined.
7. **Durable progress.** Completed financial actions and setup progress survive activity recreation, process death, force-stop, and ordinary app upgrade.
8. **No dead controls.** A visible enabled control must perform its labeled action. Unimplemented actions are hidden or clearly disabled with an honest explanation.
9. **Accessible by default.** Core flows support screen readers, minimum touch targets, scalable text, system insets, and non-color-only meaning.
10. **Evidence over claims.** Build success alone is not product acceptance. Functional, persistence, visual, accessibility, and device evidence are required.

## 4. Intended user and operating model

The primary user is an individual managing personal cash, expected income, bills, savings, and discretionary budgets on one Android device.

Current product boundaries:

- Local-first Room/SQLite storage
- Single user and one local currency configuration
- No bank connection or automatic account reconciliation
- No claim of fiduciary, banking, tax, credit, or investment advice
- No cloud synchronization contract yet
- No shared household or multi-user contract yet

The default currency is USD. Locale and multi-currency behavior require a separate approved contract before they can be claimed as supported.

## 5. First-run Setup Quest

Setup Quest is a non-bypassable six-chapter first-run gate:

| Chapter | Name | Required outcome |
|---:|---|---|
| 1 | Cash on Hand | Capture the user's cleared starting cash in exact cents. |
| 2 | Payday | Capture an income name, exact amount, next payday, recurrence frequency, and explicit confirmation. |
| 3 | Bills | Capture zero or more protected obligations with name, exact amount, and valid due date. |
| 4 | Savings | Capture savings separately from spendable cash. |
| 5 | Monthly Budgets | Capture Food/Essentials and Wants/Extras monthly limits. |
| 6 | Shield Review | Review the entered facts and explicitly activate Budget Shield. |

Setup requirements:

- The footer is hidden throughout Setup Quest.
- Leaving and returning resumes the latest valid saved chapter and inputs.
- Invalid or incomplete required input prevents forward progress and presents a useful error.
- Activation persists all accepted setup data, marks first run complete, clears the setup draft, and replaces the navigation stack with Home.
- Back from Home after activation must not return to Setup Quest.
- Savings must remain distinct from cash on hand unless the user explicitly transfers money between them.

## 6. Core financial capabilities

### 6.1 Home

Home is the dashboard and primary Safe Now surface. It must:

- Show the selected month and allow previous/next month navigation.
- Show Safe Now from current persisted inputs.
- Distinguish positive, zero, loading, empty, error, and projected-shortage states.
- Show factual supporting values, such as protected bills and activity, without fabricated previews.
- Provide working entry points for income, bills, savings, budgets, spending, transaction details/history, Shield progress, and the Budget Menu when those features are enabled.

### 6.2 Income

The user can add and maintain income schedules containing a name, exact amount, next payday, frequency, confirmation state, and active state. Only active, confirmed income occurrences within the planning horizon can increase Safe Now.

Supported frequency vocabulary is: weekly, biweekly, semimonthly/twice monthly, monthly, and one time. The implementation must define occurrence generation for each frequency before recurring projections are accepted.

### 6.3 Bills

The user can add bills, mark them protected, see remaining amounts, and record full or partial payments. A protected unpaid remainder reduces the projected balance on its due date. Overdue protected remainders are treated as due today.

A bill payment must:

- Reject zero, negative, malformed, or excessive amounts.
- Update the bill's paid amount and paid state atomically.
- Create an associated ledger transaction.
- Preserve a traceable relationship to the bill.
- Recalculate dependent views, including Safe Now.

### 6.4 Savings and goals

Savings balances and goals are tracked separately from cash on hand. A savings contribution creates a durable financial record and updates the associated goal when applicable. Savings do not increase Safe Now unless an explicit approved transfer makes those funds spendable cash.

### 6.5 Budgets and spending

Food, Wants, and other budget categories are scoped by calendar month. Logged spending updates the relevant monthly category and creates a transaction. Whether an unspent category budget reduces Safe Now is an owner decision; until approved, budget tracking must not be described as part of the Safe Now formula.

### 6.6 Transactions

Transactions are the durable financial audit trail for income, bill payments, savings, and spending. New behavior must follow the immutable-ledger decision: corrections append compensating or replacement events rather than silently rewriting history.

The current schema exposes update/delete operations. That is an implementation gap against the immutable-ledger requirement and must be resolved before release acceptance.

## 7. Gamification capabilities

Gamification may reward healthy use, but it must never obscure financial truth or fabricate progress.

- Shield XP is earned only from persisted qualifying actions.
- Shield Level is derived from total persisted XP and the approved level thresholds.
- Achievements have a real measurable target and persisted unlock state.
- Streaks are derived from persisted activity dates.
- Treasure is the rewards/progression hub; it does not own bill management.
- Bills owns obligations, payments, protected totals, and related transaction history.

If XP, achievements, streaks, chests, or reward history lack a real data source, the UI must show an honest empty state or remain hidden.

## 8. Navigation contract

The persistent footer has five top-level destinations in this order:

1. Home
2. Treasure
3. Stats
4. Goals
5. Settings

Home owns secondary financial screens, including Bills, Income Entry, Bill Entry/Payment, Savings Entry, Transaction Details, Bill Protected, Shield Progression, Budget Menu, Log Spending, and Budgets. Setup Quest has no footer.

Navigation invariants:

- Top-level taps use single-top behavior.
- Secondary-screen Back returns to the preceding valid destination.
- Completing Setup Quest replaces the whole stack with Home.
- A visible footer identifies the correct owning tab.
- Unknown routes are not silently accepted.

The detailed route inventory is authoritative in `SCREEN_MAP.md` after both contracts are approved.

## 9. Settings

Settings owns user preferences and budget configuration. The persisted settings model currently includes first-run completion, currency, timezone, notifications, reminder timing, planning horizon months, cash, savings, setup chapter, and selected month.

Resetting or reopening Setup Quest is destructive to user expectations even if data is retained. The precise reset/re-onboarding behavior requires an explicit confirmation design and owner approval before release.

## 10. Privacy, security, and resilience

- Financial data remains local unless a later approved feature explicitly transmits it.
- Secrets, credentials, signing keys, and personal financial data must not be committed to Git.
- Database upgrades preserve supported user data and are tested from every supported version.
- Destructive downgrade behavior is not release-approved.
- Logs must not expose sensitive financial details in release builds.
- Release artifacts require controlled signing, shrinking validation, provenance, and a recorded SHA-256.

## 11. Product acceptance conditions

The product cannot be called a verified beta or release candidate until current evidence demonstrates:

- All five product/engineering contracts are owner-approved.
- Safe Now matches its worked examples and edge-case matrix.
- Every enabled financial control works end-to-end and persists.
- No fabricated production data or dead controls remain.
- Fresh install, upgrade, recreation, process death, force-stop, and relaunch preserve the expected state.
- JVM, lint, build, and connected-device gates pass on the exact candidate commit.
- Visual and accessibility acceptance is captured at approved screen sizes and font scales.
- Release signing, shrinking, installation, upgrade, rollback policy, artifact hash, and GitHub governance gates are satisfied.

## 12. Known implementation gaps (not approved behavior)

The recovery inventory found these current-code discrepancies:

- Safe Now does not yet expand recurring income occurrences and does not consult `isActive`.
- The configured planning-horizon month count is not used by the calculator.
- Some entry screens convert currency through `Double` instead of the exact money parser.
- Production placeholder/sample transaction content and an empty click handler remain.
- Active-income detection, streak calculation, and one XP path contain placeholder returns.
- Transaction update/delete APIs conflict with the immutable-ledger decision.
- Transaction History has an implemented screen but no registered route.
- Migration schema export, supported upgrade evidence, signing, shrinking, and release governance remain open.

These gaps are inputs to Task 20. Their presence does not change the intended contract, and this draft does not claim that they are fixed.

## 13. Owner approvals required

The owner must explicitly accept or revise:

1. Whether monthly category budgets reduce Safe Now.
2. The supported currency/locale scope beyond USD.
3. The supported database upgrade floor and downgrade policy.
4. Reset/re-onboarding behavior from Settings.
5. The final emoji/icon policy referenced by the surviving design contract.
6. Whether Transaction History is a required route for the next beta.
7. Whether the current five-tab footer is the final information architecture.

Approval should record the date, approving owner, and any accepted revisions in this file and `DECISIONS.md`.
