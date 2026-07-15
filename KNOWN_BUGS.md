# Known Bugs

## Active

### TASK 3 Correction — Navigation 3 Implementation
| Bug ID | Description | Severity | Reported | Status | Resolution |
|--------|-------------|----------|----------|--------|------------|
| NAV3-001 | Previous implementation used Navigation Compose 2.8.7, not REAL Navigation 3 | HIGH | 2025-07-15 | IN PROGRESS | Updating to androidx.navigation3:navigation3-runtime:1.1.4 |
| NAV3-002 | Unit tests reported NO-SOURCE (no tests existed) | MEDIUM | 2025-07-15 | IN PROGRESS | Creating JVM tests for route completeness and back-stack policy |
| NAV3-003 | Dependency versions incorrect (BOM 2025.06.00, Activity 1.10.1, Lifecycle 2.8.7) | HIGH | 2025-07-15 | IN PROGRESS | Updating to locked versions: BOM 2026.06.00, Activity 1.13.0, Lifecycle 2.11.0 |
| NAV3-004 | Documentation claimed Navigation 3 but used Navigation 2.x APIs | MEDIUM | 2025-07-15 | IN PROGRESS | Correcting all documentation with real versions and APIs |
| NAV3-005 | Runtime QA not executed — no device/emulator testing performed | HIGH | 2025-07-15 | IN PROGRESS | Creating emulator, executing fresh install, launch, navigation QA |
| NAV3-006 | Screenshots not captured — placeholder QA report | MEDIUM | 2025-07-15 | IN PROGRESS | Capturing runtime screenshots from actual device/emulator |

## Unresolved Runtime/Test Failures
- NavigationSmokeTest.kt uses Navigation 2.x APIs (NavHost, rememberNavController) — requires update
- No JVM unit tests exist for navigation logic
- No instrumentation tests executed on device/emulator
- APK not verified on fresh install
- Launch not verified with logcat

## Resolved
None.

## Tracking Format
| Bug ID | Description | Severity | Reported | Status | Resolution |
|--------|-------------|----------|----------|--------|------------|

## Severity Levels
- **CRITICAL** — Prevents build, launch, or core functionality
- **HIGH** — Major feature broken, workaround exists
- **MEDIUM** — Feature partially broken, minor workaround
- **LOW** — Cosmetic, enhancement, or edge case
