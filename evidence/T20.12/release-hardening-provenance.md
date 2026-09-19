# T20.12 Release Hardening and Provenance

Date: 2026-09-19
Branch: recovery/full-audit-2026-08-02

## Local candidate evidence

- `testDebugUnitTest`: PASS
- `assembleDebug`: PASS
- `assembleDebugAndroidTest`: PASS
- `lintDebug`: PASS
- `assembleRelease`: PASS with R8 minification and resource shrinking enabled
- `connectedDebugAndroidTest`: PASS on local phone AVD `FN_WS_Phone_API35` / `emulator-5554` running Android 15
- Connected XML summary: `tests=24 failures=0 errors=0 skipped=0`

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - SHA-256: `3ebe4e6b83acd9cadd90e1ab41f5931e3095ff4972b6c4718dd50973a24f07fd`
  - Size: 24,134,790 bytes
- Android test APK: `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`
  - SHA-256: `098eb95bf7d795febf162d2bc98299d408adde9ccbe57fe4485d204b6fa5d3a1`
  - Size: 1,118,563 bytes
- Unsigned shrunk release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`
  - SHA-256: `62f46825a44b4089a95a6ff6b3b7d0f0cbdfa4d564cef51c67d2d972d29faf15`
  - Size: 3,123,789 bytes

## Release workflow status

`.github/workflows/release-apk.yml` still does not require a connected test suite before creating a GitHub prerelease. An attempted hardening patch was rejected by GitHub because the current OAuth token lacks `workflow` scope. Treat this as a release blocker until a token with workflow permission applies the change.

## Current release status

This is still a locally verified beta candidate, not a production release candidate.

Remaining release blockers:

- Exact-SHA GitHub Actions evidence has not run yet for the current uncommitted/local changes.
- The release workflow still publishes the debug APK as a prerelease artifact and can do so without the connected suite until the workflow-permission blocker is resolved.
- No protected release signing configuration/keystore is present in the repository, as expected; the locally built release APK is unsigned.
- Independent review and owner release approval remain required before calling this a release.

## Notes

T20.11 initially found a merged-semantics regression for `home_safe_now_card`; the production Home card semantics were repaired and both the focused failing test and the full connected suite now pass.
