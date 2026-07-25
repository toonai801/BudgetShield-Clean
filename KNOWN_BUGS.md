# Known Bugs

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
