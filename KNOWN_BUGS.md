# Known Bugs

## Active

None — Task 3 corrections complete.

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

## Unresolved Runtime/Test Failures
None — all Task 3 issues resolved.

## Tracking Format
| Bug ID | Description | Severity | Reported | Status | Resolution |
|--------|-------------|----------|----------|--------|------------|

## Severity Levels
- **CRITICAL** — Prevents build, launch, or core functionality
- **HIGH** — Major feature broken, workaround exists
- **MEDIUM** — Feature partially broken, minor workaround
- **LOW** — Cosmetic, enhancement, or edge case
