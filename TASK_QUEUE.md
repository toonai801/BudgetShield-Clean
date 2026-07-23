# Task Queue

| Task | Deliverable | Status | Evidence / Notes |
|------|-------------|--------|------------------|
| TASK 1 | Clean project shell | COMPLETE | Commit 6ce7d9af3753f070a2d842d2064ca3ccafcfb629 |
| TASK 2 | Product/design/project contracts | COMPLETE | Contract c2b96391; Reference bf999d25; Document repair 48e5b06b |
| TASK 3 | Android architecture and navigation foundation | COMPLETE | Navigation 3, 14 destinations, 97 tests passing |
| TASK 4 | Functional Beta Intake and Home | COMPLETE | Version 1.2.0-beta-intake-home, 6-step Setup Quest, live data Home, Safe Now calculation |
| TASK 5 | Design system and reusable components | NOT STARTED | |
| TASK 6 | Income and payday system | COMPLETE | IncomeSchedule model, DAO, Repository, IncomeEntryScreen, IncomeEntryViewModel with frequency selection, XP integration |
| TASK 7 | Bills and recurrence engine | COMPLETE | Bill model with payment tracking, recurrence fields, BillRepository with payment validation, recurrence engine for generating instances |
| TASK 8 | Savings, wants, and food budgets | COMPLETE | BudgetCategory model (Food/Wants types), DAO, BudgetRepository with month-key scoping, SavingsGoal model, SavingsEntryScreen with XP integration |
| TASK 9 | Safe Now calculation engine | COMPLETE | SafeNowCalculator.kt with all documented rules |
| TASK 10 | Transactions and editing | COMPLETE | Transaction model with type/category, DAO with date range queries, Repository, TransactionDetailsScreen with editing UI, TransactionViewModel with state management |
| TASK 11 | Shield XP and achievement system | COMPLETE | XpEntry model with activity types, XpEntryDao, XpRepository with level calculation, ShieldLevels configuration, AchievementDao, ShieldProgressionScreen with ViewModel integration, achievement display |
| TASK 12 | Stats, goals, and settings | COMPLETE | GoalsScreen + GoalsViewModel (savings goals, streaks, shield preview), StatsScreen + StatsViewModel (spending stats, category breakdown, monthly summaries), SettingsScreen with setup quest navigation |
| TASK 13 | Full navigation and interaction QA | NOT STARTED | |
| TASK 14 | Visual accuracy pass | NOT STARTED | |
| TASK 15 | Fresh-install beta QA | NOT STARTED | |
| TASK 16 | Signed beta APK and GitHub release | IN PROGRESS | APK built, release pending |

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
