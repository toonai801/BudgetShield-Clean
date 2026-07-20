# Bounds Evidence - BudgetShield Clipping Fix

## Test Configuration
- Device: BudgetShield_Runtime emulator (OpenClaw_API34)
- Screen: 1080x2400 px
- Test Date: 2026-07-19

## Fixes Applied

### 1. Footer Label Clipping (BudgetShieldBottomNav.kt)
**Problem:** Fixed `height(80.dp)` + `navigationBarsPadding()` caused bottom clipping
**Fix:** Changed to `heightIn(min = 80.dp)` allowing flexible height with navigation bars padding

### 2. Safe Now Card Clipping (HomeScreen.kt)
**Problem:** Fixed `height(180.dp)` caused text overflow on larger fonts
**Fix:** Removed fixed height, card now uses wrap-content with `testTag("home_safe_now_card")` and `testTag("home_safe_now_description")`

## Font Scale Test Results

### Font Scale 1.0 (screenshots/home_font_scale_1_0.png)
- ✅ Safe Now card: "You're protected and in control." fully visible
- ✅ Footer labels: All 5 labels (Home, Treasure, Stats, Goals, Settings) fully visible
- ✅ Footer positioned above gesture area

### Font Scale 1.15 (screenshots/home_font_scale_1_15.png)
- ✅ Safe Now card: "You're protected and in control." fully visible
- ✅ Footer labels: All 5 labels fully visible with larger text
- ✅ Footer adapts height to accommodate larger font

### Scrolled Content (screenshots/home_scrolled.png)
- ✅ Footer remains fixed at bottom
- ✅ Content scrolls above footer
- ✅ Footer labels remain fully visible during scroll

## Test Tags Added
- `home_safe_now_card` - Safe Now card container
- `home_safe_now_description` - "You're protected and in control." text
- `bottom_nav_label_home` - Home label in footer
- `bottom_nav_label_treasure` - Treasure label in footer
- `bottom_nav_label_stats` - Stats label in footer
- `bottom_nav_label_goals` - Goals label in footer
- `bottom_nav_label_settings` - Settings label in footer

## Version Information
- versionCode: 4
- versionName: "1.1.2-beta-layout"
