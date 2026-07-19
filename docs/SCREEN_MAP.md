# Screen Map

## Required Destinations

| Screen | Purpose |
|--------|---------|
| Setup Quest | Onboarding flow |
| Home | Main dashboard with Safe Now |
| Treasure | **Gamified rewards hub** - collectibles, achievements, XP, streaks |
| Bills | **Bills & Payments** - bill management and protected money |
| Stats | **Read-only** game-like financial statistics (month view) |
| Goals | **Read-only** game-like goal progress display |
| Settings | App configuration |
| Income Entry | Add income sources |
| Bill Entry | Add/edit bills |
| Bill Payment | Pay bills interface |
| Savings Entry | Add savings |
| Transaction Details | View/edit transactions |
| Bill Protected Achievement | Success overlay |
| Shield Progression | XP and level details |

## Screen Specification Template

Each screen must document:

### Purpose
What this screen does and why it exists.

### Primary Information
What the user sees first.

### Required Actions
What the user can do here.

### Entry Points
How the user arrives at this screen.

### Exit Points
Where the user can go next.

### Empty State
What displays when there is no data.

### Error State
What displays on error.

### Data Dependencies
What data this screen requires.

---

## Home Screen

**Purpose:** Main dashboard showing Safe Now and financial overview

**Primary Information:**
- Safe Now amount (large, prominent)
- Next payday information
- Bills protected status
- Quick action buttons
- Recent activity preview
- Current month indicator

**Required Actions:**
- Add Income → Income Entry
- **Pay Bill → Bills** (NOT Bill Entry)
- Save Money → Savings Entry
- Open Safe Now details
- Switch month
- Open Recent Activity
- Navigate to Treasure (rewards hub)
- Navigate to Stats (statistics)
- Navigate to Goals (progress)
- Navigate to Settings

**Entry Points:**
- App launch (after setup complete)
- Return from any other screen
- Notification tap

**Exit Points:**
- Treasure screen (rewards)
- Stats screen (statistics)
- Goals screen (progress)
- Settings screen
- Income Entry
- **Bills (via Pay Bill)**
- Savings Entry
- Transaction Details
- Shield Progression

**Empty State:** Setup Quest prompt (if no income or bills configured)

**Error State:** Retry / fallback display with cached data indicator

**Data Dependencies:**
- Current cleared cash
- Safe Now calculation result
- Next payday date
- Bills protected count
- Recent transactions (last 3-5)
- Current month/year

---

## Setup Quest Screen

**Purpose:** Onboarding new users through a 6-chapter quest

**Primary Information:**
- Chapter progress (1 of 6)
- Current chapter title and instructions
- Input fields relevant to current chapter
- Navigation hints

**Required Actions:**
- Enter initial income source
- Enter pay schedule
- Enter bills with due dates
- Enter savings goals (optional)
- Set currency and timezone
- Complete onboarding
- Skip optional steps
- Go back to previous chapter

**Entry Points:**
- First app launch (no prior setup)
- Settings → Restart Setup Quest

**Exit Points:**
- Home screen (on completion)

**Empty State:** N/A (first run only)

**Error State:** Validation errors with inline messages and retry

**Data Dependencies:**
- User input only
- Default currency/timezone from device

---

## Treasure Screen

**Purpose:** Gamified rewards hub - displays collectibles, achievements, XP, streaks, and reward history. **NOT the bills list.**

**Primary Information:**
- Treasure header with chest icon
- XP and Shield Level progress (with progress bar)
- Current Streak display
- Expandable sections: Treasure Chests, Achievements, Reward History

**Required Actions:**
- View XP/level progress
- View current streak
- Expand/collapse Treasure Chests section
- Expand/collapse Achievements section
- Expand/collapse Reward History section
- Navigate back to Home

**Entry Points:**
- Home navigation button (rewards hub)
- Achievement completion
- Notification tap

**Exit Points:**
- Home
- Shield Progression (if available)

**Empty State:** 
- XP/Level: "Coming Soon" with progress bar at 0%
- Streak: "No active streak" with guidance to save daily
- Chests: "No treasures unlocked yet" with locked chest previews (Bronze/Silver/Gold)
- Achievements: List of locked achievements with progress
- History: "No rewards earned yet"

**Error State:** Retry / data sync error

**Data Dependencies:**
- Shield XP data (when implemented)
- Streak data (when implemented)
- Unlocked treasure/collectible data (when implemented)
- Achievement progress data (when implemented)

**Important:** Treasure contains NO bill list, NO protected money totals, NO Add Bill button, NO Pay Bill button. For bill management, use Bills & Payments screen.

---

## Bills Screen

**Purpose:** Bills & Payments - manage all bills, view protected money, pay bills, add new bills.

**Primary Information:**
- Protected Money Vault card (large total)
- Protection summary (protected count, unprotected count)
- Bills list with due dates, amounts, and status
- "Add Bill" action

**Required Actions:**
- Add bill → Bill Entry
- Pay bill → Bill Payment (with selected bill ID)
- View transaction history
- Delete bill (if editing)
- Navigate back to Home

**Entry Points:**
- Home → **Pay Bill** button
- Bill Entry completion
- Bill Payment completion
- Notification tap (due bill)

**Exit Points:**
- Bill Entry (add new)
- Bill Payment (pay selected)
- Transaction Details (history)
- Home (close/back)

**Empty State:** "No Bills Yet" with "Add Your First Bill" call-to-action

**Error State:** Retry / offline message with cached data

**Data Dependencies:**
- Bills list from BillRepository (Room database)
- Protected money totals (calculated from repository)
- Payment history

**Note:** This screen previously lived in Treasure. It is now a dedicated destination with its own route (Bills).

---

## Stats Screen

**Purpose:** **Read-only** game-like financial statistics display for selected month. Input belongs elsewhere.

**Primary Information:**
- Monthly spending breakdown
- Income vs expenses chart
- Category spending (Wants, Food, etc.)
- Spending trends over time
- Average daily spending

**Required Actions:**
- Switch time period (week/month/year)
- View category details
- Export data (future feature)

**Entry Points:**
- Home navigation
- Goals screen back navigation

**Exit Points:**
- Home
- Goals
- Settings

**Empty State:** "Not enough data yet" with encouragement to add transactions

**Error State:** Retry / fallback with partial data message

**Data Dependencies:**
- Transaction history
- Income history
- Bill payments
- Date range selected

**Note:** Stats is read-only in this design. Data entry flows launch from Home or dedicated entry screens.

---

## Goals Screen

**Purpose:** **Read-only** game-like savings goals progress display. Add/edit/contribution input belongs to dedicated entry flows.

**Primary Information:**
- Active savings goals list
- Progress bars for each goal
- Current streak (consecutive savings)
- Total saved this month

**Required Actions:**
- View goal details
- Launch Savings Entry (from Home) to contribute
- View contribution history

**Entry Points:**
- Home navigation
- Stats screen navigation
- Achievement completion

**Exit Points:**
- Home
- Savings Entry (add contribution)
- Transaction Details

**Empty State:** "No savings goals yet" with add button and example goals

**Error State:** Retry / validation error display

**Data Dependencies:**
- SavingsGoal entities
- SavingsContribution history
- Shield XP for streaks

**Note:** Goals is read-only for goal display. Creating/editing goals and adding contributions happens via dedicated entry flows launched from Home.

---

## Settings Screen

**Purpose:** App configuration and user preferences

**Primary Information:**
- Currency setting
- Timezone setting
- Notification preferences
- Theme options
- Data management options
- About / version info

**Required Actions:**
- Change currency
- Change timezone
- Toggle notifications
- Set notification times
- Export data
- Import data
- Reset data (with confirmation)
- Restart Setup Quest
- View privacy policy
- Contact support

**Entry Points:**
- Home navigation
- Any screen overflow menu

**Exit Points:**
- Home
- Setup Quest (if restart selected)

**Empty State:** N/A

**Error State:** Validation error / permission denied messages

**Data Dependencies:**
- UserSettings entity
- Device locale/timezone defaults
- Storage permissions

---

## Income Entry Screen

**Purpose:** Add or edit income sources

**Primary Information:**
- Income name field
- Amount field (dollar/cent input)
- Payday schedule selector
- Start date picker
- Confirmation status toggle
- Notes field (optional)

**Required Actions:**
- Save income
- Cancel entry
- Delete income (if editing)
- Mark as confirmed/unconfirmed
- Set next occurrence

**Entry Points:**
- Home → Add Income
- Setup Quest → Money In chapter
- Settings → Income management

**Exit Points:**
- Home
- Setup Quest (next chapter)

**Empty State:** N/A

**Error State:** Validation errors (negative amount, invalid date, duplicate name)

**Data Dependencies:**
- IncomeSchedule entity (if editing)
- UserSettings for currency

---

## Bill Entry Screen

**Purpose:** Add or edit bills

**Primary Information:**
- Bill name field
- Amount field
- Due date picker
- Recurrence selector
- Protection toggle
- Category selector
- Notes field (optional)

**Required Actions:**
- Save bill
- Cancel entry
- Delete bill (if editing)
- Toggle protection status
- Set recurrence end date (optional)

**Entry Points:**
- **Bills → Add Bill**
- Setup Quest → Bills chapter

**Exit Points:**
- **Bills** (completion)
- Home (cancel)
- Setup Quest (next chapter)

**Empty State:** N/A

**Error State:** Validation errors (negative amount, past due without protection, invalid recurrence)

**Data Dependencies:**
- BillSchedule entity (if editing)
- Existing categories
- UserSettings for currency

---

## Bill Payment Screen

**Purpose:** Record bill payment and allocate funds

**Primary Information:**
- Bill occurrence being paid
- Amount due
- Amount paid field (defaults to full amount)
- Payment date picker (defaults to today)
- Payment method (optional)
- Remaining balance indicator (if partial)

**Required Actions:**
- Confirm full payment
- Enter partial payment
- Cancel payment
- Split payment across multiple bills (advanced)

**Entry Points:**
- **Bills → Pay Bill** (with bill ID)
- Notification tap (due bill)

**Exit Points:**
- **Bills** (cancel/back)
- Bill Protected Achievement (on success)

**Empty State:** N/A

**Error State:** Insufficient funds warning, amount exceeds due

**Data Dependencies:**
- BillOccurrence entity
- Current cleared cash
- Transaction ledger

---

## Savings Entry Screen

**Purpose:** Add savings contribution

**Primary Information:**
- Amount field
- Goal selector (optional, can be general savings)
- Date picker (defaults to today)
- Streak indicator
- Shield XP preview

**Required Actions:**
- Save contribution
- Cancel entry
- Create new goal from here
- Skip (back to Home)

**Entry Points:**
- Home → Save Money
- Goals → Add Contribution
- Setup Quest → Savings chapter

**Exit Points:**
- Goals
- Home
- Goals → Create Goal (if new goal selected)

**Empty State:** N/A

**Error State:** Validation errors (negative amount, exceeds available cash)

**Data Dependencies:**
- SavingsGoal list
- Current cleared cash
- ShieldProgress

---

## Transaction Details Screen

**Purpose:** View and edit individual transactions

**Primary Information:**
- Transaction type icon
- Amount (positive/negative)
- Date and time
- Description/memo
- Related income or bill (if applicable)
- Running balance (if viewing from history)

**Required Actions:**
- Edit transaction (date, amount, description)
- Delete transaction (with confirmation)
- View related income/bill
- Add correction transaction

**Entry Points:**
- Home → Recent Activity
- **Bills → Payment History**
- Stats → Transaction drill-down
- Goals → Contribution history

**Exit Points:**
- Home
- **Bills**
- Stats
- Goals
- Income Entry (if income-related)
- Bill Entry (if bill-related)

**Empty State:** N/A

**Error State:** Transaction not found / already deleted

**Data Dependencies:**
- Transaction entity
- Related IncomeOccurrence or BillOccurrence
- Related BillPaymentAllocation

---

## Bill Protected Achievement Screen

**Purpose:** Celebrate bill payment success with visual reward

**Primary Information:**
- Success message ("Rent Protected!" or bill name)
- Visual reward (shield animation)
- XP gained amount
- Streak maintained (if applicable)

**Required Actions:**
- Dismiss (tap anywhere or swipe)
- Share achievement (optional)
- View Shield Progression

**Entry Points:**
- After successful bill payment

**Exit Points:**
- Home
- **Bills**
- Shield Progression

**Empty State:** N/A

**Error State:** N/A

**Data Dependencies:**
- Bill that was paid
- XP calculation
- ShieldProgress current state

---

## Shield Progression and XP Details Screen

**Purpose:** View XP history, levels, and progression details

**Primary Information:**
- Current level and title
- XP progress to next level
- XP history list with reasons
- Total XP earned
- Level milestones
- Streak information
- Recent achievements

**Required Actions:**
- View XP breakdown by category
- View level rewards
- Share progress
- Close and return

**Entry Points:**
- Home → XP indicator
- Bill Protected Achievement → View Progression
- Goals → XP details
- Settings → Gamification

**Exit Points:**
- Home
- Goals
- Settings

**Empty State:** N/A (XP starts at 0, always has some data)

**Error State:** Retry / data sync error

**Data Dependencies:**
- ShieldProgress current state
- ShieldXpEvent history
- Calculated level thresholds
- Streak data
