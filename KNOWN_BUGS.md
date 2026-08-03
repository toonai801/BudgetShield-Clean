# Known Bugs

## Task 19 Contract Closure — 2026-08-02 (Current Authority)

| Defect | Status | Closure evidence |
|---|---|---|
| REC-DOC-001 | CLOSED | Five reconstructed contracts owner-approved with recommended decisions; authority and decisions recorded in `docs/` and `DECISIONS.md` |

Contract restoration does not close the implementation defects described by those contracts. Financial, persistence, navigation, placeholder, CI/release, visual, accessibility, and device issues remain open for Task 20.

## Task 19 Documentation Status — 2026-08-02 (Superseded by Closure)

| Defect | Status | Current evidence |
|---|---|---|
| REC-DOC-001 | MITIGATED — APPROVAL PENDING | All five missing contracts have been reconstructed as drafts in `docs/`; each remains marked owner-approval required |

The absence defect is resolved at the file/reconstruction level, but contract authority is not restored until the owner approves or revises every draft. All product-code, data, CI/release, visual, accessibility, and device gaps cataloged by the drafts remain open for the ordered Task 20 recovery.

## Task 18 Resolution — 2026-08-02 (Current Authority)

| Defect | Status | Resolution evidence |
|---|---|---|
| REC-CT-001 | CLOSED | Setup/Home-stack flow passes locally and in GitHub run `30776785734` |
| REC-CT-002 | CLOSED | Post-setup footer flow passes locally and in GitHub run `30776785734` |

Both timeouts were manifestations of nondeterministic test interaction, not a reproduced failure of the production stack-replacement policy. The tests previously auto-launched one activity and manually launched another, then targeted a tagged row while claiming to click the checkbox. On slower CI, the click missed during keyboard/scroll animation, confirmation stayed false, and the tests timed out waiting for Chapter 3. Commits `4daf153` and `37c49e5` remove the competing launch and target, reveal, click, and verify the actual checkbox.

**Current connected result:** 23/23 pass locally and in GitHub Actions. The older open rows and 21/23 statement below are retained as baseline history and are superseded by this section.

## Current Recovery Blockers — 2026-08-02

| Defect | Description | Severity | Status | Current Evidence |
|---|---|---|---|---|
| REC-CT-001 | Setup completion does not navigate to Home and replace the setup stack within 20 seconds | CRITICAL | OPEN | `NavigationSmokeTest.completeSetupQuestNavigatesToHomeAndReplacesStack`; latest CI at `575a9d5` |
| REC-CT-002 | Persistent footer does not appear after setup completion within 20 seconds | CRITICAL | OPEN | `PersistentFooterTest.footerShowsAfterSetupCompletion`; latest CI at `575a9d5` |
| REC-DOC-001 | Five README-authoritative contracts are absent | HIGH | OPEN | Missing Product, Safe Now, Screen Map, Data Model, and Test Plan documents |
| REC-DATA-001 | Production placeholders remain in schedules, streaks, transactions, and controls | HIGH | OPEN | Confirmed directly in current production code; see recovery baseline |
| REC-REL-001 | Release pipeline can publish without connected-test success and has no release signing configuration | CRITICAL | OPEN | Current workflow/build configuration |

**Current connected result:** 21/23 pass. The historical “no active blockers” section below is superseded but preserved for audit history.

## OpenClaw Rebuild Checkpoint Status

**Preservation Commit:** chore(recovery): preserve beta work before OpenClaw rebuild  
**Date:** 2026-07-22  
**Status:** Project paused, tests partially failing (20/23 connected tests pass)

---

## Resolved — 2026-07-24 (QA Controller Run)

| Defect | Description | Severity | Status | Resolution |
|--------|-------------|----------|--------|------------|
| CT-001 | `SetupQuestFlowTest.setupPersistsAcrossProcessDeath` returns null draft | HIGH | ✅ FIXED | Root cause was Hilt DI missing TransactionRepository in TestDatabaseModule |
| CT-002 | `NavigationSmokeTest.endToEndSetupQuestCompletes` Chapter 2→3 timing | HIGH | ✅ FIXED | Root cause was Hilt DI missing TransactionRepository in TestDatabaseModule |
| CT-003 | `PersistentFooterTest.footerHiddenDuringSetupAppearsAfter` visibility timing | HIGH | ✅ FIXED | Root cause was Hilt DI missing TransactionRepository in TestDatabaseModule |

---

## Active — Release Blockers (UNRESOLVED)

**NONE — All blockers resolved 2026-07-24**

---

## Active — Technical Debt

| Defect | Description | Severity | Status | Resolution |
|--------|-------------|----------|--------|------------|
| TD-001 | Blocking DAO methods added for tests only | MEDIUM | 🔄 TECH DEBT | `*Blocking()` methods in DAOs exist purely for test synchronization; may need coroutine test helpers |
| TD-002 | SetupQuestViewModel uses ExecutorService | MEDIUM | 🔄 TECH DEBT | Thread pool executor in ViewModel for draft persistence — should use coroutines with proper scopes |

---

## Previous Work (Resolved)

### Treasure Persistence Correction (2026-07-18)
| Defect | Description | Severity | Status | Resolution |
|--------|-------------|----------|--------|------------|
| TI-007 | No new tests proving persistence behavior | CRITICAL | ✅ FIXED | Added MoneyParserTest (19), DateParserTest (19), BillDaoTest (12), BillDatabasePersistenceTest (4), BillRepositoryTest (13), BillEntryViewModelTest (6) |
| TI-008 | Destructive migration fallback enabled | CRITICAL | ✅ FIXED | Removed fallbackToDestructiveMigration from BudgetShieldDatabase |
| TI-009 | Floating-point money conversion | CRITICAL | ✅ FIXED | Created MoneyParser with exact integer arithmetic |
| TI-010 | Ignored Result from createBill() | CRITICAL | ✅ FIXED | BillEntryScreen now handles Result, shows errors, prevents duplicate saves. BillEntryViewModelTest verifies Result.failure cases |
| TI-011 | Weak date validation | HIGH | ✅ FIXED | Created DateParser with strict LocalDate validation |
| TI-012 | Blocked "Pay Bill" workflow | HIGH | ✅ FIXED | TreasureScreen shows "Pay Bill" for ALL unpaid bills |
| TI-013 | Unverified process death claims | MEDIUM | ✅ FIXED | Created disk-backed Room persistence tests |
| TI-014 | Mismatched release tagging | MEDIUM | ✅ FIXED | Created new verified release with unique tag |

### TASK 3 Test Integrity (2026-07-15)
| Defect | Description | Severity | Status | Resolution |
|--------|-------------|----------|--------|------------|
| TI-001 | Fake JVM test doubles used instead of production routes | CRITICAL | ✅ FIXED | Remove TestHome/TestTreasure/etc., use production routes |
| TI-002 | Weakened instrumentation coverage to silence CI | CRITICAL | ✅ FIXED | Restore full NavigationSmokeTest coverage |
| TI-003 | Placeholder APK SHA-256 in QA report | HIGH | ✅ FIXED | Replace with real APK hash from verified build |
| TI-004 | Conflicting test counts (12, 14, 20) reported | MEDIUM | ✅ FIXED | Use only Gradle XML/HTML report totals |
| TI-005 | Stale project state marked Task 3 COMPLETE prematurely | MEDIUM | ✅ FIXED | Reopen as IN PROGRESS, close after verified evidence |
| TI-006 | CI unverified — no real emulator test execution | HIGH | ✅ FIXED | Run connectedDebugAndroidTest on API 34 emulator |

---

## Resolved

### TASK 3 Navigation 3 Migration
| Bug ID | Description | Severity | Reported | Status | Resolution |
|--------|-------------|----------|----------|--------|------------|
| NAV3-001 | Previous implementation used Navigation Compose 2.8.7, not REAL Navigation 3 | HIGH | 2025-07-15 | ✅ RESOLVED | Updated to androidx.navigation3:navigation3-runtime:1.1.4 |
| NAV3-002 | Unit tests reported NO-SOURCE (no tests existed) | MEDIUM | 2025-07-15 | ✅ RESOLVED | Created 14 JVM unit tests (RouteCompletenessTest.kt, BackStackPolicyTest.kt) — ALL PASSING |
| NAV3-003 | Dependency versions incorrect (BOM 2025.06.00, Activity 1.10.1, Lifecycle 2.8.7) | HIGH | 2025-07-15 | ✅ RESOLVED | Updated to locked versions: BOM 2026.06.00, Activity 1.13.0, Lifecycle 2.10.0 |
| NAV3-004 | Documentation claimed Navigation 3 but used Navigation 2.x APIs | MEDIUM | 2025-07-15 | ✅ RESOLVED | Corrected all documentation with real versions and APIs |
| NAV3-005 | Runtime QA not executed — no device/emulator testing performed | HIGH | 2025-07-15 | ✅ RESOLVED | Fresh install, launch, and navigation QA executed on emulator-5554 (API 34) |
| NAV3-006 | Screenshots not captured — placeholder QA report | MEDIUM | 2025-07-15 | ✅ RESOLVED | 5 runtime screenshots captured and verified at 1080x2400 |

---

## Tracking Format
| Bug ID | Description | Severity | Reported | Status | Resolution |
|--------|-------------|----------|----------|--------|------------|

## Severity Levels
- **CRITICAL** — Prevents build, launch, or core functionality
- **HIGH** — Major feature broken, workaround exists
- **MEDIUM** — Feature partially broken, minor workaround
- **LOW** — Cosmetic, enhancement, or edge case

---

*Last updated: 2026-07-24 — All connected tests passing (23/23), QA Gate PASSED, Ready for Release*
