# Budget Shield Test Plan

**Status:** DRAFT — reconstructed from current tests, workflows, quality gates, and recovery findings on 2026-08-02; owner approval required

**Authority after approval:** Minimum verification strategy and evidence required for task, beta, and release acceptance

**Current verified checkpoint:** Task 18 — 226/226 JVM tests and 23/23 API 34 connected tests passed for recovery commit `37c49e5`; GitHub Actions run `30776785734`

## 1. Test objectives

Testing must establish that Budget Shield:

- Calculates Safe Now correctly with exact money and real dates.
- Preserves financial and setup data across lifecycle and upgrade events.
- Routes every visible control to the correct functional destination.
- Contains no fabricated financial state or enabled dead controls.
- Works accessibly and responsively across the approved Android/device matrix.
- Produces a reproducible, signed, installable, upgrade-safe release artifact.

A successful compile, a green unit suite, an APK, or screenshots alone cannot establish product completion.

## 2. Evidence levels

| Level | Purpose | Typical evidence |
|---|---|---|
| Static | Prevent obvious source/configuration defects | Compiler, lint, dependency/config checks, forbidden-pattern scans |
| JVM unit | Verify pure logic and isolated policies | JUnit results for money, dates, Safe Now, recurrence, navigation policy, models |
| Local integration | Verify Room/DAO/repository behavior | In-memory/file Room tests, migration helper tests, transaction rollback tests |
| Connected component | Verify Compose interaction and lifecycle | Instrumentation tests on emulator/device, recreation/process-death assertions |
| End-to-end | Verify complete user journeys | Fresh install through setup, financial actions, relaunch, upgrade |
| Visual/accessibility | Verify rendered quality and usability | Screenshot matrix, semantics, touch target, font-scale and scanner results |
| Release | Verify distributable artifact | Signing, R8, install/upgrade/rollback, hash, provenance, workflow evidence |

## 3. Current automated inventory

### JVM tests

The current repository contains 16 JVM test suites and 226 passing test methods at the Task 18 checkpoint. Coverage areas include:

- Safe Now calculation and recalculation
- Money parsing and formatting
- Strict date parsing
- Bill model/category behavior
- DAO and database persistence
- Room migrations
- Bill repository and entry ViewModel
- Setup/navigation models
- Route completeness, shell ownership, and back-stack policy

Passing existing tests are regression assets, not proof that all contract cases exist.

### Connected tests

Three instrumentation classes currently contain 23 test methods:

- `SetupQuestFlowTest`
- `NavigationSmokeTest`
- `PersistentFooterTest`

At Task 18, all 23 passed locally and in GitHub Actions on an API 34 x86_64 Google APIs emulator. They principally cover setup progression/persistence, route smoke behavior, setup-to-Home stack replacement, and footer visibility.

### CI workflows

- `qa-gate.yml`: clean, compile, JVM tests, lint, debug APK, Android-test APK; currently scoped to `main` push/PR plus manual dispatch.
- `android-debug.yml`: JVM build/test plus API 34 connected tests; currently scoped to `main` push/PR plus manual dispatch.
- Release workflow: produces a debug beta after non-device gates; it does not currently require the connected suite and is not a signed production release.

The recovery branch requires explicit workflow dispatch/API verification until branch triggers are corrected.

## 4. Test data rules

- Tests use deterministic explicit dates and timezones.
- Monetary fixtures are integer cents.
- Tests never depend on execution order or existing device data.
- Connected tests start from a declared state: fresh install, seeded upgrade, or documented persisted fixture.
- Every test owns and cleans only its database/preferences/files.
- No production singleton, activity, coroutine, or emulator state leaks between tests.
- Screenshots name the commit, API, resolution, density, font scale, locale, theme, and state.
- Personal data, credentials, keystores, and tokens never enter fixtures or artifacts.

## 5. Required functional suites

### 5.1 Setup Quest

- First-run loading gate cannot flash/bypass Home.
- Chapters 1–6 render and validate required input.
- Exact cash/income/savings/budget parsing.
- Strict payday and bill due-date validation.
- Add zero, one, and multiple bills.
- Back/forward retains valid entries.
- Activity recreation, process death, force-stop, and relaunch resume the correct draft.
- Invalid partial input is not silently committed as valid data.
- Activation commits all intended records atomically, clears draft, marks completion, awards XP once, and replaces stack with Home.
- Back from Home cannot return to setup.
- Footer hidden during setup and visible immediately after activation.

### 5.2 Safe Now

Implement every case in `SAFE_NOW_RULES.md`, including:

- Positive, exactly zero, and shortage
- Same-day income-before-bill ordering
- Confirmed/active income filtering
- Overdue, protected, paid, and partially paid bills
- Multiple dates and deepest-vs-first shortage distinction
- Configured and obligation-extended horizons with real month ends
- All recurrence types after their rules are approved
- Invalid dates, overflow, inconsistent data, and database error states
- Identical result after recreation, relaunch, and supported migration
- Accessible explanation semantics

### 5.3 Bills and payments

- Create/read/update allowed schedule data with exact money/date validation.
- Protect/unprotect according to approved UX.
- Full and partial payments; reject zero, negative, malformed, and excessive payments.
- Bill state, transaction, XP, achievement/streak, and Safe Now update atomically.
- Repeated submit/retry cannot double-pay or double-award XP.
- Missing/deleted bill route ID shows recoverable error.
- Bills list, totals, filters, empty state, and transaction entry points use real data.

### 5.4 Income

- Each approved frequency and occurrence boundary.
- Confirmed and active toggles.
- Month/year and leap-boundary occurrence generation.
- Edit/deactivate behavior does not rewrite historical occurrences.
- Adding income persists transaction/XP behavior exactly once if that is approved.

### 5.5 Savings and goals

- Create goal, contribute, complete, and display progress.
- Exact relationship between total savings and goal allocations.
- Emergency fund and priority behavior if user-visible.
- Deadline validation.
- Contribution transaction and XP are atomic/idempotent.
- Savings remain excluded from Safe Now absent explicit transfer.

### 5.6 Budgets and spending

- Month-scoped unique categories.
- Food, Wants, Other, empty, and over-budget states.
- Log spending with exact cents and valid category/month.
- Budget aggregate reconciles with transactions.
- Month switching and year boundary.
- Owner-approved effect—or explicit non-effect—on Safe Now.

### 5.7 Transactions

- Income, bill payment, savings, spending, refund, and correction/reversal conventions.
- Details/history render persisted data only.
- Missing transaction ID and deleted relationship behavior.
- Date-range/category/type aggregates.
- Immutable-ledger enforcement; no ordinary destructive edit/delete.
- Ordering is stable when dates/timestamps match.

### 5.8 Gamification

- XP base awards and level boundaries.
- One award per qualifying action despite retry/recreation.
- Achievement seeding, progress, unlock, and reward idempotency.
- Streak timezone/day transitions, missed day, repeat same-day activity, best streak.
- Treasure/Goals/Shield screens show real persisted state and honest empty states.

### 5.9 Settings

- Preference persistence and validation.
- Planning horizon change recalculates Safe Now.
- Notification/timezone behavior where implemented.
- Re-onboarding/reset confirmation and data-retention policy.
- Transaction History is reachable if released; otherwise its control is absent.

## 6. Navigation and interaction matrix

For every route in `SCREEN_MAP.md`:

- Route renders without crash or unknown fallback.
- Every visible enabled control is clicked and its effect asserted.
- Footer visibility, order, selected owner, and semantics are correct.
- Single-top and Back behavior are asserted.
- Setup completion stack replacement is asserted.
- Distinct parameterized IDs navigate independently.
- Invalid IDs produce an error and safe exit.
- Rapid double tap and repeated navigation do not duplicate financial actions.
- IME open/close, scroll, and animation do not make controls unreachable.

The navigation inventory is a traceability checklist; a route-presence unit test does not replace interaction testing.

## 7. Persistence and migration matrix

### Lifecycle

Each multistep or financial flow is tested across:

- Activity recreation
- Background/foreground
- Process death with saved/persisted state only
- Force-stop/relaunch
- Device reboot where practical
- Interrupted commit/fault injection

### Database

- Fresh creation at current schema
- Each owner-supported version → current
- Every chained supported upgrade path
- Nonempty records in every old table
- Relationships, amounts, dates, flags, indexes, and IDs retained
- Safe Now and aggregates correct after upgrade
- Downgrade behavior matches approved policy
- Schema export matches entity declarations

## 8. Visual acceptance

Visual claims require current screenshots from the candidate commit and comparison to immutable approved references.

Required state coverage:

- Every screen in loading, empty, typical, long-content, and error state where applicable
- Home positive, zero, and shortage Safe Now
- Setup all six chapters and validation errors
- Bills unpaid, partial, paid, protected, overdue
- Treasure/Stats/Goals with empty and populated real data
- Keyboard-visible entry screens
- Confirmation/success screens

Required configuration coverage, subject to owner matrix approval:

- At least one compact phone and one larger phone
- API 26 compatibility smoke, API 34 CI baseline, and target/current API
- Portrait; landscape for critical flows
- Font scale 1.0, 1.15, and an approved accessibility scale such as 1.3 or 1.5
- Light/dark system bar variations while the app remains dark themed
- Approved locale/long-text case

Compare layout, typography, color, spacing, insets, clipping, scrolling, footer, and state accuracy. Emoji rendering cannot be called pixel-accurate across devices without an owner-approved icon policy.

## 9. Accessibility acceptance

- TalkBack traversal and action labels for every core flow
- Correct semantic roles and selected/disabled states
- No duplicate or misleading content descriptions
- 48dp minimum interactive targets
- Text contrast and non-color-only warnings
- Dynamic text without clipped amounts, dates, or primary actions
- Keyboard/IME navigation and error announcement
- Focus restoration after dialogs/navigation
- Android accessibility scanner or equivalent report, with reviewed exceptions

## 10. Device and compatibility matrix

Minimum proposed matrix for owner approval:

| Purpose | API/device |
|---|---|
| Minimum SDK smoke | API 26 phone emulator/device |
| Main connected regression | API 34 x86_64 Google APIs emulator |
| Target behavior | API 35 phone emulator/device |
| Compile/current compatibility | API 36 runtime when stable image/tooling is available |
| Physical sanity | At least one real supported Android phone before public beta |

Database upgrade testing additionally needs an installable APK/database fixture from every supported released schema version; forbidden legacy repositories are not used.

## 11. Performance and resilience

- Cold/warm launch does not display the wrong first-run destination.
- Safe Now and dashboard remain responsive with large but realistic bill/transaction histories.
- Room queries and Compose lists avoid main-thread blocking and runaway recomposition.
- Rapid save/payment taps are idempotent.
- Disk-full/database exceptions show recoverable errors.
- Rotation, backgrounding, and low-memory recreation do not duplicate actions.
- Release build has no financial-value logging and no debug-only dependency on correctness.

## 12. CI gates

### Every pull request

- Clean checkout/build
- Compile debug
- All JVM tests
- Lint with zero errors and reviewed warning budget
- Debug APK and Android-test APK assembly
- API 34 connected suite
- Contract/document link and consistency checks
- No secret, keystore, database, generated build, or unintended binary changes

### Candidate release

- All PR gates on the exact candidate SHA
- Full device/persistence/migration/visual/accessibility matrix
- Release variant compile/test/lint
- R8/minification and ProGuard verification
- Release signing using protected secrets
- Clean install and supported upgrade
- Launch/core journey smoke on the signed artifact
- SHA-256, size, version, commit, signing certificate, and provenance recorded
- Independent audit and owner release approval

CI must not publish a release when the connected gate is absent, skipped, cancelled, or failing.

## 13. Defect handling

- Record reproduction, expected/actual result, exact SHA, device/API/configuration, logs, and evidence.
- Severity reflects financial/user harm and release impact.
- Fixes add focused regression coverage.
- Existing historical reports are hypotheses until reproduced on the current branch.
- A flaky test is a defect in product or harness until its cause is proven; retries cannot be used to conceal it.
- No test is deleted, ignored, or weakened merely to obtain green status without owner-approved rationale.

## 14. Exit criteria

### Task exit

- Task-specific acceptance criteria declared before implementation.
- Relevant automated and manual gates pass on the task commit.
- No unrelated regression or hidden failure.
- Control documents updated.
- Commit pushed and remote SHA verified.

### Beta exit

- All contracts approved.
- All enabled product journeys function and persist.
- Safe Now financial matrix passes.
- No fabricated data/dead controls.
- Approved device, visual, and accessibility evidence passes.
- Signed beta artifact and upgrade path verified.

### Release exit

- Every mandatory gate in `QUALITY_GATES.md` passes with current evidence.
- No open release-blocking defect.
- Independent review completed.
- Protected merge and immutable release provenance completed.

## 15. Current gaps

- Current tests are concentrated on setup/navigation and selected data logic; most production controls lack end-to-end coverage.
- Safe Now lacks the complete intended horizon, recurrence, active-income, invalid-data, and overflow matrix.
- Migration evidence is incomplete and schema export is disabled.
- Visual/accessibility/device coverage is not a current candidate matrix.
- CI branch triggers do not automatically cover the recovery branch.
- The release workflow can publish without connected tests and produces a debug-signed artifact.
- Signing, shrinking, ProGuard, supported upgrade, real-device, and independent release gates remain open.

Task 19 records the required strategy. Task 20 must implement and execute it in the approved order.

## 16. Owner decisions required

1. Supported physical-device/API matrix.
2. Required accessibility font scale and locale matrix.
3. Oldest supported upgrade version.
4. Warning budget and which lint categories are release-blocking.
5. Independent reviewer/approval policy and protected-branch requirements.
6. Whether signed beta and production release have separate exit criteria.
