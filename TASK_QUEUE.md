# Task Queue

| Task | Deliverable | Status | Evidence / Notes |
|------|-------------|--------|------------------|
| TASK 1 | Clean project shell | COMPLETE | Commit 6ce7d9af3753f070a2d842d2064ca3ccafcfb629 |
| TASK 2 | Product/design/project contracts | COMPLETE | Contract c2b96391; Reference bf999d25; Document repair 48e5b06b; Logic correction a625993079318acbca155a528f85c1146e2701ab |
| TASK 3 | Android architecture and navigation foundation | COMPLETE | Single-activity Compose, Navigation 3, 13 destinations, build successful, tests created |
| TASK 4 | Exact design system and reusable components | NOT STARTED | |
| TASK 5 | Setup Quest | NOT STARTED | |
| TASK 6 | Home screen visual implementation | NOT STARTED | |
| TASK 7 | Income and payday system | NOT STARTED | |
| TASK 8 | Bills and recurrence system | NOT STARTED | |
| TASK 9 | Safe Now calculation engine with unit tests | NOT STARTED | |
| TASK 10 | Home screen live-data integration | NOT STARTED | |
| TASK 11 | Transactions and editing | NOT STARTED | |
| TASK 12 | Savings, wants, and food budgets | NOT STARTED | |
| TASK 13 | Shield XP and achievement system | NOT STARTED | |
| TASK 14 | Stats, goals, and settings | NOT STARTED | |
| TASK 15 | Full navigation and interaction QA | NOT STARTED | |
| TASK 16 | Visual accuracy pass | NOT STARTED | |
| TASK 17 | Fresh-install beta QA | NOT STARTED | |
| TASK 18 | Signed beta APK and GitHub release | NOT STARTED | |

## Acceptance Criteria Policy
Each future task must have acceptance criteria before its status changes to IN PROGRESS. Preserve every existing task and evidence entry.

## TASK 3 Acceptance Criteria

1. **Project identity verified**: Folder BudgetShield_CLEAN, repo toonai801/BudgetShield-Clean, branch main, clean working tree at starting HEAD eb54be35
2. **Gradle foundation pinned**: AGP 8.13.2, Gradle 8.13, Kotlin plugins 2.2.21, Java 17, compileSdk 36, targetSdk 35, minSdk 26
3. **Dependencies added**: Compose BOM 2026.06.00, Activity Compose 1.13.0, Lifecycle 2.11.0, Navigation 3 1.1.4, Kotlinx serialization 1.9.0
4. **Single MainActivity**: Extends ComponentActivity, uses setContent with BudgetShieldTheme, only launcher activity
5. **Old shell removed**: XML HomeActivity removed, activity_home.xml removed, no duplicate launchers
6. **13 typed destinations**: SetupQuest, Home, Treasure, Stats, Goals, Settings, IncomeEntry, BillEntry, BillPayment, SavingsEntry, TransactionDetails, BillProtected, ShieldProgression
7. **Navigation 3 type-safe routes**: Serializable typed route keys, rememberNavBackStack, NavDisplay
8. **Functional placeholder screens**: Each screen shows destination name + ARCHITECTURE FOUNDATION label, visible controls navigate correctly
9. **Required route wiring**: All SCREEN_MAP.md paths implemented and functional
10. **Back-stack rules correct**: Setup Quest completion replaces stack, Home back exits, no endless duplicates for main destinations
11. **Architecture documentation**: docs/ARCHITECTURE.md created with navigation strategy, state ownership, future boundaries
12. **Automated navigation tests**: Compose UI tests prove all 13 destinations reachable, Setup Quest stack replacement, Back behavior
13. **Build passes**: ./gradlew clean testDebugUnitTest assembleDebug succeeds
14. **APK created**: app-debug.apk builds successfully
15. **Fresh install works**: APK installs on device/emulator
16. **Launch succeeds**: App launches without crash
17. **Manual QA**: qa/TASK3_NAVIGATION_QA.md created with 13-destination matrix, device info, SHA-256
18. **Screenshots captured**: Setup Quest, Home, Treasure, Bill Protected, one nested screen in qa/task3/screenshots/
19. **GitHub Actions**: .github/workflows/android-debug.yml runs on push/PR, uses Java 17, caches Gradle, runs tests, uploads APK artifact
20. **Documents updated**: PROJECT_STATE.md, CHANGELOG.md, DECISIONS.md, KNOWN_BUGS.md, QUALITY_GATES.md reflect Task 3
21. **Task 3 COMPLETE**: All gates pass, clean working tree after commit
22. **Task 4 NOT STARTED**: No Task 4 work begun
