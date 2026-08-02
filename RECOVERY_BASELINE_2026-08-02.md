# Budget Shield Recovery Baseline

**Date:** 2026-08-02

**Repository:** `toonai801/BudgetShield-Clean`

**Recovery branch:** `recovery/full-audit-2026-08-02`

**Audited commit:** `575a9d516fc8f64432ff62bb7438240b83214e14`
**Audit mode:** Evidence-only collection followed by an owner-approved documentation-only truth commit; no product code or Git history changed

## Executive verdict

**Status: RED — builds locally, but is not release-ready and the repository records are not trustworthy as a unified source of truth.**

The current commit compiles, passes 226 JVM unit tests, passes Android lint with warnings, and assembles a debug APK. However, the latest connected-test run executed 23 tests and failed 2 critical setup/navigation tests. Production code still contains confirmed placeholders and hard-coded user-facing data. Several authoritative contracts named by the README are absent. Release configuration is incomplete, `main` is unprotected, eight APK binaries are committed, and project-control documents make mutually incompatible claims.

No existing completion checkbox or prior report should be accepted without current reproducible evidence.

## 1. Verified repository identity and state

| Item | Verified result |
|---|---|
| Remote | `https://github.com/toonai801/BudgetShield-Clean.git` |
| Protected working branch | `recovery/full-audit-2026-08-02` |
| HEAD | `575a9d516fc8f64432ff62bb7438240b83214e14` |
| Branch origin | Exact copy of `main` at audit start |
| Package/application ID | `com.toonai.budgetshield` |
| Working tree before audit | Clean |
| Approved tracked changes | Baseline report plus current-status overlays in existing management documents |
| Local-only setup file | Ignored `local.properties` pointing to the Android SDK |
| Tracked files | 655 |
| Git packed repository size | 231.36 MiB |

The repository's `AGENT_RULES.md`, README, and quality records hard-code an old Linux/OpenClaw path and `main` as the active branch. Those environment-specific assertions are stale. The user explicitly authorized this Windows checkout and the protected recovery branch; repository identity, remote, package, HEAD, and branch are otherwise verified.

## 2. Local toolchain established

| Tool | Verified state |
|---|---|
| Git | 2.53.0.windows.3 |
| GitHub CLI | 2.97.0, authenticated as `toonai801` |
| GitHub scopes | `repo`, `workflow`, `read:org`, `gist` |
| Android Studio | 2026.1.3.7 |
| Java | Eclipse Temurin 17.0.20+8 for project builds |
| Gradle wrapper | 8.13 |
| Android platform | API 36 installed |
| Android build tools | 35.0.0 and 36.0.0 installed |
| Android platform tools | Installed; `adb` functional |
| Connected device/emulator | None |

Android Studio's bundled Java 25 can launch Gradle but cannot compile this project's Kotlin DSL: it fails with `IllegalArgumentException: 25.0.2`. Java 17 is therefore the required local runtime, matching GitHub Actions and the repository contract.

## 3. Reproduced baseline gates

All mandated local non-device gates were run from the recovery branch with Java 17.

| Gate | Current result | Evidence |
|---|---|---|
| `gradlew clean` | PASS | `BUILD SUCCESSFUL` in 33s |
| `gradlew compileDebugKotlin` | PASS | `BUILD SUCCESSFUL` in 1m 42s |
| `gradlew testDebugUnitTest` | PASS | 226 tests, 0 failures, 0 errors, 0 skipped |
| `gradlew lintDebug` | PASS WITH WARNINGS | 0 fatal, 0 errors, 38 warnings |
| `gradlew assembleDebug` | PASS | `BUILD SUCCESSFUL` in 53s |

### Generated debug artifact

| Item | Value |
|---|---|
| Path | `app/build/outputs/apk/debug/app-debug.apk` |
| Size | 23,375,684 bytes |
| SHA-256 | `F14C487CC9B0BB74D4C7DCDCC5AD27D4FCCB38B23C45173F2E42742CA71CA622` |
| Signing | Debug signing only |

### Lint inventory

| Lint category | Count |
|---|---:|
| `GradleDependency` | 14 |
| `DefaultLocale` | 9 |
| `NewerVersionAvailable` | 8 |
| `UnusedResources` | 3 |
| `MonochromeLauncherIcon` | 1 |
| `KaptUsageInsteadOfKsp` | 1 |
| `OldTargetApi` | 1 |
| `ObsoleteSdkInt` | 1 |

Compiler warnings also identify deprecated migration APIs, deprecated status/navigation bar mutation, deprecated Canvas path calls, and an always-true condition in `TransactionDetailsScreen.kt`.

## 4. Current connected-test truth

No local emulator or Android device was connected, so this audit does **not** claim a local connected-test pass.

The latest GitHub Actions run for the audited commit is authoritative current device evidence:

- Workflow: `Android Debug Build and Test`
- Run: `30194446293`
- Commit: `575a9d516fc8f64432ff62bb7438240b83214e14`
- JVM build/test job: PASS
- Instrumentation job: FAIL
- Executed: 23 connected tests
- Passed: 21
- Failed: 2

Failures:

1. `NavigationSmokeTest.completeSetupQuestNavigatesToHomeAndReplacesStack`
   - `ComposeTimeoutException`
   - condition not satisfied after 20,000 ms
2. `PersistentFooterTest.footerShowsAfterSetupCompletion`
   - `ComposeTimeoutException`
   - condition not satisfied after 20,000 ms

The emulator emitted a `Failed to find ColorBuffer` message, but it continued executing all 23 tests. The actionable failure is two reproducible Compose test timeouts in setup completion/home navigation and footer visibility.

## 5. Project-control contradictions

| Source | Claim | Current evidence / contradiction |
|---|---|---|
| Latest commit message | 226 unit tests, 69 connected tests, 0 errors | 226 unit tests is reproduced; latest CI connected run is 21/23, so “0 errors” is false as a complete status |
| `PROJECT_STATE.md` | Paused; 20/23 connected pass; version code 7 | Current app is version code 8; latest CI is 21/23 |
| `TASK_QUEUE.md` | Tasks 13–15 complete; 23/23 connected pass | Latest CI disproves 23/23 and fresh-install completion cannot be accepted |
| `KNOWN_BUGS.md` | No active release blockers; all 23 connected pass | Latest CI has two release-blocking failures |
| `QUALITY_GATES.md` | Every gate checked; remote HEAD `5572462`; branch `main` | Audited HEAD is `575a9d5`; connected workflow fails; recovery work is on protected branch |
| `DEFECT_REGISTER.md` | 71 open defects, nine critical blockers | Several cited defects remain in code, but the register is historical and must be revalidated item by item |
| `IMPLEMENTATION_QUEUE.md` | QA-001 is the primary blocker with a proposed root cause | Current failures are in setup completion/home replacement and footer visibility; proposed root cause is not yet proven |
| `CHANGELOG.md` | Home contains no hard-coded values | Production `HomeScreen.kt` still contains a hard-coded `Rent Payment` preview/example |
| `DECISIONS.md` and route comments | 14 destinations | Registry count is 17, route file declares 18 route types including parameterized payment route |

## 6. Missing authoritative contracts

The README declares the following documents authoritative, but they are absent from the audited commit:

- `docs/PRODUCT_CONTRACT.md`
- `docs/SAFE_NOW_RULES.md`
- `docs/SCREEN_MAP.md`
- `docs/DATA_MODEL_PLAN.md`
- `docs/TEST_PLAN.md`

Only `docs/DESIGN_CONTRACT.md` remains in `docs/`. The changelog says the missing documents were previously created and repaired, but the recovery rules prohibit silently retrieving old commits or legacy project material. They must be reconstructed from current approved requirements and current code, with owner review.

Other named management files absent from the repository include `SESSION_NOTES.md` and `IDEAS.md`.

## 7. Feature and architecture inventory

### Build architecture

- Native Android, Kotlin, Jetpack Compose, single activity
- Android Gradle Plugin 8.13.2
- Kotlin 2.2.21
- compileSdk 36, targetSdk 35, minSdk 26
- Navigation 3 runtime/UI 1.1.4 with serializable `NavKey` routes
- Hilt 2.56.1
- Room 2.7.1
- Database version 4

### Production screens found

18 screen files are present:

- Setup Quest
- Home
- Treasure
- Bills
- Stats
- Goals
- Settings
- Income Entry
- Bill Entry
- Bill Payment
- Savings Entry
- Transaction Details
- Transaction History
- Bill Protected
- Shield Progression
- Budget Menu
- Log Spending
- Budgets

### Navigation inventory

The production registry reports 17 destinations. The route file contains 18 route types because `BillPaymentWithId` is a parameterized route in addition to `BillPayment`. Comments still say 14 destinations and are stale.

### Data inventory

Room database version 4 declares:

- Bill
- UserSettings
- IncomeSchedule
- BudgetCategory
- SetupDraft
- Transaction
- XpEntry
- Achievement
- SavingsGoal
- UserStreak

Explicit migrations exist for 1→2, 2→3, and 3→4. Risks:

- `exportSchema = false`, so migration history is not exported for durable schema verification.
- Migration 1→2 is duplicated in `DatabaseMigrations.kt` and `BudgetShieldDatabase.kt`.
- Production enables destructive downgrade migration.
- Release migration/upgrade evidence is not current.

### Test inventory

- 16 JVM test suites, 226 current passing tests
- 3 instrumentation test classes
- 23 connected test methods executed by latest CI
- Current connected result: 21 pass, 2 fail

## 8. Confirmed production-code defects at audited HEAD

The following were confirmed directly in current production code, rather than copied from old reports:

1. `IncomeRepository.hasActiveSchedules()` returns `true` unconditionally.
2. `HomeViewModel.calculateStreak()` returns `0` with a TODO.
3. `XpRepository` contains a path that returns `0`.
4. `HomeScreen.kt` contains hard-coded `Rent Payment` content.
5. `TransactionDetailsScreen.kt` contains hard-coded `Rent Payment` content.
6. `TransactionDetailsScreen.kt` contains an empty `onClick = { }` handler.
7. `TreasureScreen.kt` shows `No XP records`, indicating incomplete real-data experience.
8. `DesignSystem.kt` contains empty click handlers in preview/example composables; these must be distinguished from live production paths before classification.
9. The compiler reports an always-true condition in `TransactionDetailsScreen.kt`.
10. Current connected tests fail setup completion → Home stack replacement and post-setup footer visibility.

Several old defect-register claims are already obsolete—for example, `BudgetCategoryDao` now has `getCategoryById`. The old 71-defect total must therefore not be copied forward unchanged.

## 9. Design and accessibility baseline

The surviving design contract defines a dark fantasy finance-game visual system, but it uses emoji as the primary icon system. This conflicts with the stated premium cross-device quality target because emoji rendering varies by Android version, font, OEM, and locale.

The repository contains many screenshots, but their provenance is mixed and several sets document contradictory states, including stuck setup screens. Screenshot existence alone does not prove current visual compliance. A new screenshot matrix must be captured from the repaired commit at defined device dimensions, font scales, and system-bar conditions.

Confirmed audit needs:

- Reconcile four-tab vs five-tab/footer expectations with current route ownership.
- Verify 48dp touch targets and content descriptions.
- Verify clipping at 1.0×, 1.15×, and larger accessibility font scales.
- Verify safe-area/system-bar handling on API 26 through current target behavior.
- Replace or formally approve the emoji icon strategy.
- Validate every empty, loading, error, positive Safe Now, and shortage state.

## 10. CI, release, and repository hygiene

### CI

- `qa-gate.yml` passes clean, compile, JVM tests, lint, debug APK, and Android-test APK assembly.
- `android-debug.yml` separately runs the emulator suite and currently fails.
- A green QA Gate therefore does **not** mean connected QA passed.
- The release workflow re-runs non-device gates but does not run connected tests before publishing.

### Release configuration

- Release minification is disabled.
- No release signing configuration exists.
- The build script references `app/proguard-rules.pro`, but that file is absent.
- The release workflow publishes a debug-signed APK and explicitly labels it a debug beta.
- Version is `1.2.0-beta-intake-home-v8` / versionCode 8, inconsistent with multiple documents and release names.

### GitHub governance

- `main` has no branch protection.
- Repository has no pull-request history.
- Repository has no issue history.
- Recovery branch exists and is pushed.
- Numerous releases exist despite contradictory QA state.

### Repository bloat

- 8 APK files are tracked.
- Total tracked APK bytes: 179,297,244.
- Git pack size: 231.36 MiB.
- Build logs, test logs, screenshots, and repeated QA artifacts are also tracked.

No binaries or history were deleted during this audit. Cleanup should first preserve provenance and choose whether Git LFS, GitHub Releases, or history rewriting is authorized.

## 11. Exact repair order

1. **Establish documentation truth**
   - Add this baseline to the recovery branch.
   - Reconcile `PROJECT_STATE`, `TASK_QUEUE`, `KNOWN_BUGS`, `QUALITY_GATES`, `CHANGELOG`, `DEFECT_REGISTER`, and `IMPLEMENTATION_QUEUE` without erasing history.
   - Reconstruct missing contracts from current approved requirements and code, not forbidden legacy sources.
2. **Make CI truth unambiguous**
   - Require connected-test status alongside the non-device QA gate.
   - Prevent release publication when connected tests are absent or failing.
   - Add PR workflow coverage for the recovery branch.
3. **Fix the current release blocker**
   - Reproduce and diagnose the two setup-completion/footer timeouts.
   - Determine whether the defect is application state, persistence, Navigation 3 stack replacement, test synchronization, or a combination.
   - Add focused regression evidence before changing unrelated UI.
4. **Verify Safe Now and exact money/date behavior**
   - Reconstruct the missing Safe Now contract.
   - Test same-day income ordering, overdue bills, partial payments, horizon rules, integer parsing, locale handling, and shortage explanations.
5. **Secure Room schema and migration behavior**
   - Enable schema export.
   - Consolidate migrations.
   - Add migration tests for every supported version path.
   - Remove destructive downgrade behavior unless explicitly approved.
6. **Complete setup and process-death persistence**
   - Fresh install, activity recreation, process death, force-stop/relaunch, interrupted setup, and upgrade behavior.
7. **Remove production placeholders and dead controls**
   - Fix hard-coded transaction/bill data, empty click handlers, always-true conditions, always-true schedule checks, and zero-only streak paths.
8. **Verify every financial feature**
   - Bills, income, savings, budgets, transactions, partial payments, editing, recurring schedules, and history.
9. **Verify gamification features**
   - XP, achievements, streaks, Stats, Goals, Treasure, Settings, and their persistence.
10. **Rebuild the design system deliberately**
    - Resolve icon policy, component tokens, footer structure, responsive layout, accessibility, and screenshot acceptance matrix.
11. **Run device QA**
    - Instrumentation suite on a real emulator/device, fresh install, upgrade migration, process death, font scale, and screenshots.
12. **Harden release engineering**
    - Release signing without committed secrets, R8/minification, ProGuard rules, provenance, immutable tags, artifact hashes, rollback and upgrade testing.
13. **Independent final audit and protected merge**
    - Protect `main`, require PR review and required checks, and merge only after every gate has current reproducible evidence.

## 12. Approved first commit

**Owner approval granted on 2026-08-02.**

Approved commit scope:

- Add `RECOVERY_BASELINE_2026-08-02.md` containing this verified baseline.
- Reconcile current-state headers/status tables in existing management documents while preserving all historical entries.
- Mark connected tests accurately as 21/23 for audited commit `575a9d5`.
- Record the two current connected failures and links to the failing workflow.
- Mark Tasks 13–16 as reopened/unverified where their evidence is contradicted.
- Record missing contracts as recovery tasks; do not fabricate their contents.
- Add no product-code changes.
- Add no generated build output, APK, SDK path, credential, or secret.

Approved commit message, adapted to the repository's required format:

`Task 17: Establish verified Budget Shield recovery baseline`

Required verification before commit:

- `git diff --check`
- `git status --short`
- `git diff --stat`
- full `git diff` review
- confirm `local.properties` and `app/build/` remain ignored

## 13. External blockers and owner decisions

The following require owner direction or external capability later; they do not block the documentation baseline:

1. Release keystore ownership, storage, rotation, and recovery policy.
2. Whether APK history should be preserved in Git LFS, GitHub Releases only, or removed through history rewriting.
3. Approval of the final icon system and premium visual references.
4. Supported upgrade floor and database downgrade policy.
5. Branch-protection and required-review policy for `main`.
6. Physical-device matrix, if any, beyond emulator coverage.

## Bottom line

The project has a viable compilable foundation and a substantial JVM test suite, but it is not a trustworthy beta or release candidate today. The immediate next action should be a documentation-only truth baseline, followed by the two connected setup/footer failures. Core financial correctness and persistence must be verified before broad visual redesign.
