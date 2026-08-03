# Task Queue

## Task 20.3 Execution — 2026-08-02 (Current Authority)

- **Task 20:** IN PROGRESS
- **Completed increment:** durable Room schema export, reconciled v4→v5 migration, real v1→v5 preservation proof, duplicate migration removal, and non-destructive downgrade proof at `b874b2f`
- **Evidence:** 241/241 JVM tests; debug app, Android test APK, and lint pass; schema identity `33a854ca5e4e735d335a371f314b2c4f`
- **Device limitation:** repair-panel Android UI test remains compiled but awaits an attached API 34 device/emulator
- **Next increment:** immutable ledger and atomic financial-mutation recovery under the approved data contract
- **Release status:** BLOCKED until all remaining Task 20 gates pass

## Task 20.2 Execution — 2026-08-02 (Current Authority)

- **Task 20:** IN PROGRESS
- **Completed increment:** two-anchor semimonthly recurrence, database version 5 migration, income/setup UI, destructive-downgrade removal, and blocking Safe Now repair panel at `fd526e1`
- **Evidence:** 239/239 JVM tests; debug app, Android test APK, and lint pass
- **Device limitation:** repair-panel Android UI test compiled but awaits an attached API 34 device/emulator for execution
- **Next increment:** repair and prove the complete versions-1-through-current migration chain, enable durable schema export, then continue ordered ledger/persistence recovery
- **Release status:** BLOCKED until all remaining Task 20 gates pass

## Task 20 Execution — 2026-08-02 (Current Authority)

- **Task 20:** IN PROGRESS
- **Completed increment:** 20.1 Safe Now core contract repair at `f051663`
- **Evidence:** 234/234 JVM tests plus successful debug assembly and lint
- **Next increment:** implement the approved two-anchor semimonthly data contract and guided invalid-data repair path before broader persistence/navigation recovery
- **Release status:** BLOCKED until all remaining Task 20 gates pass

## Task 19 Closure — 2026-08-02 (Current Authority)

- **Task 19:** COMPLETE
- **Owner approval:** GRANTED for all five reconstructed contracts with the recommended decisions
- **Decision record:** Fourteen approved directions in `DECISIONS.md`
- **Scope:** Documentation-only approval closure; no production or schema change
- **Next recovery task:** Task 20 — full product and release recovery under the approved contracts

This closure supersedes the IN REVIEW section retained below for audit history.

## Task 19 Contract Review — 2026-08-02 (Superseded by Closure)

- **Task 19:** IN REVIEW
- **Reconstruction:** COMPLETE — Product, Safe Now, Screen Map, Data Model, and Test Plan drafts now exist
- **Approval:** PENDING OWNER REVIEW
- **Scope:** Documentation only; no production code, schema, workflow, or release artifact changed
- **Next action:** Owner reviews the decisions listed in each contract; Task 19 closes only after explicit approval/revision
- **Task 20:** NOT STARTED and blocked behind Task 19 approval

Acceptance criteria and their current state are recorded in the current Task 19 section of `PROJECT_STATE.md`.

## Task 18 Closure — 2026-08-02 (Current Authority)

- **Task 18:** COMPLETE
- **Repair commits:** `4daf153`, `37c49e5`
- **Connected tests:** 23/23 pass locally and in GitHub Actions on API 34
- **GitHub evidence:** Android Debug Build and Test run `30776785734` for commit `37c49e5`
- **Root cause:** the connected tests launched competing activity instances and tagged the confirmation row instead of the actual checkbox; on slower CI the coordinate click missed while the keyboard/scroll animation was active, leaving Chapter 2 confirmation false and Next disabled
- **Next recovery task:** Task 19 — reconstruct the five missing product and engineering contracts for owner review

This closure supersedes the older 21/23 and Task 18 “NEXT” statements retained below for audit history.

## Recovery Status Override — 2026-08-02

The historical task table below is preserved, but its completion claims are not current acceptance evidence. The latest CI run for audited commit `575a9d5` executed 23 connected tests and failed 2. Tasks 13–16 are therefore reopened or blocked until their gates are reproduced on the recovery branch.

| Recovery Task | Deliverable | Status | Evidence / Notes |
|---|---|---|---|
| TASK 17 | Verified recovery baseline and documentation truth reconciliation | COMPLETE | `RECOVERY_BASELINE_2026-08-02.md`; local clean/compile/226 unit/lint/assemble gates reproduced |
| TASK 18 | Repair setup completion, Home stack replacement, and post-setup footer | COMPLETE | 23/23 local and GitHub API 34 connected tests; run `30776785734`; commits `4daf153`, `37c49e5` |
| TASK 19 | Reconstruct and approve missing product, Safe Now, screen, data, and test contracts | COMPLETE | Five contracts reconstructed and owner-approved with recommended decisions on 2026-08-02 |
| TASK 20 | Full financial, persistence, navigation, visual, accessibility, device, and release recovery | NEXT | Must follow the approved contracts and verified repair order in evidence-backed increments |

| Task | Deliverable | Status | Evidence / Notes |
|------|-------------|--------|------------------|
| TASK 1 | Clean project shell | COMPLETE | Commit 6ce7d9af3753f070a2d842d2064ca3ccafcfb629 |
| TASK 2 | Product/design/project contracts | COMPLETE | Contract c2b96391; Reference bf999d25; Document repair 48e5b06b |
| TASK 3 | Android architecture and navigation foundation | COMPLETE | Navigation 3, 18 destinations, 39+ tests passing |
| TASK 4 | Functional Beta Intake and Home | COMPLETE | Version 1.2.0-beta-intake-home, 6-step Setup Quest, live data Home, Safe Now calculation |
| TASK 5 | Design system and reusable components | COMPLETE | Theme, typography, color system, Material3 components, reusable UI patterns |
| TASK 6 | Income and payday system | COMPLETE | IncomeSchedule model, DAO, Repository, IncomeEntryScreen, IncomeEntryViewModel with frequency selection, XP integration |
| TASK 7 | Bills and recurrence engine | COMPLETE | Bill model with payment tracking, recurrence fields, BillRepository with payment validation, recurrence engine for generating instances |
| TASK 8 | Savings, wants, and food budgets | COMPLETE | BudgetCategory model (Food/Wants types), DAO, BudgetRepository with month-key scoping, SavingsGoal model, SavingsEntryScreen with XP integration |
| TASK 9 | Safe Now calculation engine | COMPLETE | SafeNowCalculator.kt with all documented rules |
| TASK 10 | Transactions and editing | COMPLETE | Transaction model with type/category, DAO with date range queries, Repository, TransactionDetailsScreen with editing UI, TransactionViewModel with state management |
| TASK 11 | Shield XP and achievement system | COMPLETE | XpEntry model with activity types, XpEntryDao, XpRepository with level calculation, ShieldLevels configuration, AchievementDao, ShieldProgressionScreen with ViewModel integration, achievement display |
| TASK 12 | Stats, goals, and settings | COMPLETE | GoalsScreen + GoalsViewModel (savings goals, streaks, shield preview), StatsScreen + StatsViewModel (spending stats, category breakdown, monthly summaries), SettingsScreen with budget setup and profile |
| TASK 13 | Full navigation and interaction QA | REOPENED | Historical 23/23 claim contradicted by latest CI result: 21/23 |
| TASK 14 | Visual accuracy pass | UNVERIFIED | Historical screenshots exist, but current screenshot matrix and premium-design acceptance evidence are incomplete |
| TASK 15 | Fresh-install beta QA | REOPENED | Setup completion/Home and footer connected tests currently fail |
| TASK 16 | Signed beta APK and GitHub release | BLOCKED | Connected tests fail; release signing, shrinking, and final release gates are incomplete |

## Acceptance Criteria Policy
Each future task must have acceptance criteria before its status changes to IN PROGRESS. Preserve every existing task and evidence entry.

## TASK 4 Acceptance Criteria (Functional Beta)

1. ✅ Project identity verified: Folder BudgetShield_CLEAN, repo toonai801/BudgetShield-Clean, branch main
2. ✅ Version bumped: 1.2.0-beta-intake-home (versionCode 7)
3. ✅ Setup Quest 6 steps: Cash, Income, Bills, Savings, Budgets, Activate
4. ✅ First-run gate non-bypassable: runBlocking in MainActivity.onCreate()
5. ✅ Footer hidden during setup: BudgetShieldNavShell checks for SetupQuest
6. ✅ Room migration 1→2: Preserves existing bills, adds new tables
7. ✅ Setup draft persistence: SetupDraftDao for process-death resume
8. ✅ Safe Now calculation: SafeNowCalculator with all 9 documented examples
9. ✅ Home live data: All values from Room, no hardcoded values
10. ✅ Home controls functional: Previous/next month, pay bill, quick actions
11. ✅ Hilt DI: DatabaseModule and CalculationModule
12. ✅ Build passes: ./gradlew clean assembleDebug — SUCCESS
13. ✅ Tests pass: ./gradlew testDebugUnitTest — 97 tests PASSED
14. ✅ Lint passes: ./gradlew lintDebug — SUCCESS
15. ✅ APK created: app-debug.apk with SHA-256
16. ✅ CHANGELOG updated: Entry for 1.2.0-beta-intake-home
17. ✅ PROJECT_STATE updated: Current task status
18. ✅ Task 4 COMPLETE: All gates pass

## TASK 4 Implementation Evidence

**APK:**
- File: app/build/outputs/apk/debug/app-debug.apk
- Size: 22,015,224 bytes
- SHA-256: 3029d1224686a81a6ac571d4206bab8fe76263bd5d83f13f91ea036548f0f85d

**Test Results:**
- Unit Tests: 97 PASSED
- Build: SUCCESS
- Lint: SUCCESS (only deprecation warnings)

**Key Files:**
- MainActivity.kt — Non-bypassable first-run gate
- SetupQuestScreen.kt — 6-step onboarding
- HomeScreen.kt — Live data, no hardcoded values
- HomeViewModel.kt — Combines all DAOs
- SafeNowCalculator.kt — Safe Now calculation engine
- BudgetShieldDatabase.kt — Migration 1-2

## TASK 13 Acceptance Criteria (Navigation QA)

1. ✅ All 18 routes reachable from production navigation
2. ✅ Connected tests: 23/23 PASS
3. ✅ Back navigation works correctly
4. ✅ Footer visibility correct (hidden in SetupQuest, visible elsewhere)
5. ✅ No dead buttons or silent failures
6. ✅ Deep links functional where applicable
7. ✅ Navigation state survives configuration changes

## TASK 14 Acceptance Criteria (Visual QA)

1. ✅ Screenshot evidence exists for all primary screens
2. ✅ Footer not clipped
3. ✅ No system-bar overlap
4. ✅ Correct dark fantasy styling applied
5. ✅ Empty states handled gracefully
6. ✅ Long text fits correctly

## TASK 15 Acceptance Criteria (Fresh Install QA)

1. ✅ Fresh install launches without crash
2. ✅ Setup Quest begins automatically
3. ✅ Setup data persists after activity recreation
4. ✅ Setup data persists after process death
5. ✅ Footer appears after setup completion
6. ✅ Home loads real persisted data
7. ✅ No startup crash or ANR

## TASK 16 Acceptance Criteria (Release)

1. ✅ All QA gates pass (clean, compile, test, lint, assemble)
2. ✅ Connected tests pass (23/23)
3. ✅ GitHub Actions workflow validates commit SHA
4. ✅ Release uses immutable versioned tags
5. ✅ APK SHA-256 recorded
6. ⚠️ Signed release (requires keystore — external blocker)
7. ⚠️ GitHub release created with verified artifacts
