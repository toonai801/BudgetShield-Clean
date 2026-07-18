# RECOVERY VERTICAL SLICE PLAN

**Status:** RECOVERY REQUIRED  
**Date:** 2026-07-18  
**Current Commit:** de3a7b0 Task 3: record verified production tests and evidence  

---

## VERDICT: CURRENT UI REJECTED

The current screens are **placeholder architecture screens** explicitly marked `ARCHITECTURE FOUNDATION - NOT FINAL UI`. They cannot be treated as acceptable baseline UI and must not be polished or expanded.

---

## PART 1: WHAT CAN BE KEPT

### Architecture Layer (KEEP)
These are solid and require no changes:

| Component | File | Status |
|-----------|------|--------|
| Navigation system | `navigation/BudgetShieldRoute.kt` | KEEP |
| Navigation wiring | `navigation/BudgetShieldNavigation.kt` | KEEP |
| Back stack policy | `navigation/BackStackPolicy.kt` | KEEP |
| Route registry | `navigation/BudgetShieldRouteRegistry.kt` | KEEP |
| MainActivity | `MainActivity.kt` | KEEP |
| App entry point | `BudgetShieldApp.kt` | KEEP |
| Tests | All `/test/` files | KEEP |

### Theme Layer (PARTIAL KEEP)

| Component | Status | Note |
|-----------|--------|------|
| Theme file structure | KEEP | Material3 base is correct |
| Color scheme definition | MODIFY | Colors are placeholder teal/orange; needs gold/coin/progression palette |
| Typography scale | KEEP | Material3 typography is acceptable |
| Shape definitions | KEEP | Material3 shapes are acceptable |

---

## PART 2: WHAT MUST BE REPLACED

### Current Screens (ALL REJECTED)

| Screen | Current State | Action |
|--------|---------------|--------|
| `HomeScreen.kt` | Placeholder buttons + "$--.--" mock | COMPLETE REPLACEMENT |
| `TreasureScreen.kt` | Placeholder bill list + "$--.--" | COMPLETE REPLACEMENT |
| `SetupQuestScreen.kt` | "Chapter 1 of 6" placeholder | COMPLETE REPLACEMENT |
| `StatsScreen.kt` | Placeholder | REJECT - not in 3-screen slice |
| `GoalsScreen.kt` | Placeholder | REJECT - not in 3-screen slice |
| `SettingsScreen.kt` | Placeholder | REJECT - not in 3-screen slice |
| `BillEntryScreen.kt` | Functional placeholder | DEFER - not in slice |
| `BillPaymentScreen.kt` | Functional placeholder | DEFER - not in slice |
| `SavingsEntryScreen.kt` | Functional placeholder | DEFER - not in slice |
| `IncomeEntryScreen.kt` | Functional placeholder | DEFER - not in slice |
| `TransactionDetailsScreen.kt` | Functional placeholder | DEFER - not in slice |
| `ShieldProgressionScreen.kt` | Placeholder | DEFER - not in slice |
| `BillProtectedScreen.kt` | Functional placeholder | DEFER - not in slice |

---

## PART 3: EXACT GAP LIST

### Gap: Home Dashboard

**Current:** Column of buttons + generic "Safe Now" card with "$--.--"

**Missing for target:**
- [ ] Visual hierarchy: Today's Earnings as hero number
- [ ] Monthly savings summary card
- [ ] Quest progress bar (visual, not text)
- [ ] Treasure chest visual element
- [ ] Shield progression mini-display
- [ ] Safe-to-spend calculation (Task 9 logic)
- [ ] Bottom navigation (not row of buttons)
- [ ] No generic Material Surface cards with placeholder text

**Gap severity:** CRITICAL - This is the app centerpiece

### Gap: Treasure / Bills Screen

**Current:** Simple text list + "Protected Money" card

**Missing for target:**
- [ ] Treasure vault visual metaphor
- [ ] Protected money hero display with visual weight
- [ ] Bill cards (not simple text bullets)
- [ ] Bill status indicators (protected vs unprotected)
- [ ] Progress-to-protection visualization
- [ ] "Protect This" action with visual affordance
- [ ] Empty state with illustration
- [ ] No generic Surface containers

**Gap severity:** CRITICAL - Core game loop screen

### Gap: Setup Quest / Onboarding

**Current:** "Chapter 1 of 6" + placeholder text + single button

**Missing for target:**
- [ ] Chapter-by-chapter visual progression
- [ ] Illustration/animation per chapter
- [ ] Income setup (not generic "Money In")
- [ ] Bill import/setup flow
- [ ] Savings goal setup
- [ ] Quest selection interface
- [ ] Completion celebration
- [ ] No generic Material3 surfaces

**Gap severity:** HIGH - First impression, retention critical

---

## PART 4: COMPONENT LIST FOR NEW DESIGN SYSTEM

### Visual Components Needed

| Component | Purpose | Complexity |
|-----------|---------|------------|
| `HeroCard` | Large hero number display (Today's Earnings) | Medium |
| `TreasureChest` | Animated/illustrated treasure visualization | High |
| `QuestProgressBar` | Visual quest completion indicator | Medium |
| `ShieldBadge` | Mini shield progression display | Low |
| `ProtectedCard` | Treasure vault protected money display | Medium |
| `BillCard` | Individual bill with status + protect action | Medium |
| `BottomNavBar` | Custom bottom navigation (3-4 items) | Medium |
| `ChapterStepper` | Onboarding chapter indicator | Low |
| `OnboardingSlide` | Per-chapter onboarding content | Medium |
| `SafeToSpendPill` | Calculated safe spending amount | Low |
| `CoinIcon` | Currency/progression visual language | Low |

### Theme Modifications Needed

| Element | Current | Target |
|---------|---------|--------|
| Primary | Teal (`#80CBC4`) | Gold/Amber (coin color) |
| Secondary | Orange (`#FFB74D`) | Progress blue/purple |
| Background | `#121212` | Dark treasure vault (deep blue-black) |
| Surface | `#1E1E1E` | Card depth layers |
| Success/Protected | Not defined | Shield green |
| Warning/Unprotected | Not defined | Alert amber |

---

## PART 5: SCREEN-BY-SCREEN REBUILD ORDER

### Phase 1: Design System Foundation (No new screens yet)
1. Define color palette (gold/progression/shield)
2. Create typography scale for hero numbers
3. Build component primitives (HeroCard, TreasureChest visual)
4. Update theme file with new colors

### Phase 2: Home Dashboard (Week 1)
1. Replace `HomeScreen.kt` entirely
2. Implement Today's Earnings hero
3. Add monthly savings summary
4. Add quest progress bar
5. Replace button nav with BottomNavBar
6. Keep existing navigation calls (onNavigateToTreasure, etc.)

### Phase 3: Treasure Screen (Week 2)
1. Replace `TreasureScreen.kt` entirely
2. Implement treasure vault visual
3. Create bill cards with status
4. Add protect-this actions
5. Keep existing navigation calls

### Phase 4: Setup Quest (Week 3)
1. Replace `SetupQuestScreen.kt` entirely
2. Build chapter stepper
3. Create onboarding slides
4. Implement per-chapter flows
5. Keep onComplete navigation

### Phase 5: Integration & Navigation Cleanup
1. Ensure navigation between 3 screens works
2. Remove or hide non-slice screens from nav
3. Test back stack behavior
4. Dark/light theme verification

---

## PART 6: RISKS

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Owner vision unclear | Medium | High | Require screenshot/design approval before each screen |
| Animation complexity | Medium | Medium | Start static, add animation in polish phase |
| Safe-to-spend calculation wrong | Medium | High | Keep calculation separate from UI; UI can show placeholder until Task 9 |
| Timeline slip | High | Medium | 3-week commitment per phase; owner sign-off gates |
| Rebuild vs refactor debate | Medium | Medium | Document: UI is rejected, not improvable; rebuild required |
| Navigation regression | Low | High | Keep navigation layer untouched; only screen content changes |

---

## PART 7: SMALLEST SAFE IMPLEMENTATION PLAN

### Constraint: Do not destabilize working architecture

**Safe Approach:**
1. Keep all 13 routes registered in navigation
2. Replace only screen content (@Composable functions)
3. Hide non-slice screens from user navigation (can still route programmatically)
4. Do not delete old screen files until new ones verified (rename first)

### Step-by-Step Safety

```
Step 1: Backup
- Copy HomeScreen.kt → HomeScreenOld.kt
- Copy TreasureScreen.kt → TreasureScreenOld.kt
- Copy SetupQuestScreen.kt → SetupQuestScreenOld.kt

Step 2: Create New (don't delete old yet)
- HomeScreenNew.kt with target design
- TreasureScreenNew.kt with target design
- SetupQuestScreenNew.kt with target design

Step 3: Swap in Navigation
- Update BudgetShieldNavigation.kt to use *New screens
- Test navigation between 3 screens

Step 4: Cleanup
- Delete *Old.kt files once verified
- Rename *New.kt → original names
```

### What NOT to do:
- ❌ Don't delete Route objects from BudgetShieldRoute.kt
- ❌ Don't change MainActivity.kt
- ❌ Don't touch tests in /test/
- ❌ Don't modify gradle/build files
- ❌ Don't touch persistence layer

---

## OWNER REQUIREMENTS FOR PROCEEDING

Before any code is written, owner must provide:

1. **Visual target:** Screenshots, mockups, or design reference for:
   - Home dashboard hero layout
   - Treasure vault visual treatment
   - Onboarding chapter flow

2. **Color palette:** Exact hex codes or reference palette

3. **Animation scope:** Which elements need animation vs static acceptable

4. **Quest progression:** Visual treatment for quest progress indicator

5. **Sign-off on plan:** Confirm this recovery plan is acceptable approach

---

## CONCLUSION

Current UI is **architecturally sound but visually rejected**. The navigation layer, theme structure, and test coverage are keepable. The screen content is not.

Recovery requires a **vertical slice rebuild of 3 screens only**, preserving the working architecture underneath. No new features. No additional screens. No beta expansion.

**Next step:** Owner provides visual targets and approves this plan.

---

**Written by:** TOON (coordinator)  
**Status:** Awaiting owner input  
**Do not code until owner approves plan and provides visual references.**
