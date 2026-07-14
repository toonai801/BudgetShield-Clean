# Quality Gates

Mandatory gates before marking any task complete:

## Project Identity
- [ ] Correct project folder (BudgetShield_CLEAN)
- [ ] Correct repository (toonai801/BudgetShield-Clean)
- [ ] Correct package (com.toonai.budgetshield)
- [ ] On correct branch (main)

## Git State
- [ ] Clean working tree before work
- [ ] No uncommitted changes from previous work

## Build Verification
- [ ] Successful debug build (`./gradlew clean assembleDebug`)
- [ ] APK generated at expected path

## Installation
- [ ] Fresh install succeeds
- [ ] Application launches
- [ ] No launch crash

## Feature Testing
- [ ] All assigned controls tested
- [ ] Safe Now unit tests pass (when implemented)

## Visual Verification
- [ ] Screenshots captured
- [ ] Visual comparison completed against reference images

## Documentation
- [ ] Documents updated (PROJECT_STATE.md, CHANGELOG.md, etc.)

## Commit and Push
- [ ] Commit created with descriptive message
- [ ] Commit pushed to remote
- [ ] Remote commit verified
- [ ] Working tree clean after push
