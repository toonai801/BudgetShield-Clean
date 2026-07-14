# Screen Map

## Required Destinations

| Screen | Purpose |
|--------|---------|
| Setup Quest | Onboarding flow |
| Home | Main dashboard with Safe Now |
| Treasure | Bills and protected money |
| Stats | Financial statistics |
| Goals | Savings goals tracking |
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
- Add Income
- Pay Bill
- Save Money
- Open Safe Now details
- Switch month
- Open Recent Activity
- Navigate to Treasure
- Navigate to Stats
- Navigate to Goals
- Navigate to Settings

**Entry Points:**
- App launch (after setup complete)
- Return from any other screen
- Notification tap

**Exit Points:**
- Treasure screen
- Stats screen
- Goals screen
- Settings screen
- Income Entry
- Bill Entry
- Savings Entry
- Transaction Details
- Shield Progression
- Bill Payment

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

**Purpose:** View bills and protected money

**Primary Information:**
- Protected money total
- Bills list with due dates
- Payment status indicators
- Protection status (shield icon)

**Required Actions:**
- Add bill
- Edit bill
- Pay bill
- View bill details
- Toggle bill protection
- Sort/filter bills

**Entry Points:**
- Home navigation button
- Bill payment completion
- Notification tap

**Exit Points:**
- Bill Entry
- Bill Payment
- Transaction Details
- Home

**Empty State:** "No bills yet" with add button and explanation of protection

**Error State:** Retry / offline message with cached data

**Data Dependencies:**
- Bills list with occurrences
- Payment history
- Protection settings
- Safe Now calculation

---

## Stats Screen

**Purpose:** Financial statistics and insights

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

---

## Goals Screen

**Purpose:** Savings goals tracking and progress

**Primary Information:**
- Active savings goals list
- Progress bars for each goal
- Current streak (consecutive savings)
- Total saved this month

**Required Actions:**
- Add new savings goal
- Edit goal
- Delete goal
- Add contribution to goal
- View contribution history
- Mark goal complete

**Entry Points:**
- Home navigation
- Stats screen navigation
- Achievement completion

**Exit Points:**
- Home
- Stats
- Savings Entry
- Transaction Details

**Empty State:** "No savings goals yet" with add button and example goals

**Error State:** Retry / validation error display

**Data Dependencies:**
- SavingsGoal entities
- SavingsContribution history
- Shield XP for streaks

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
- Home → Pay Bill (add new)
- Treasure → Add Bill
- Setup Quest → Bills chapter

**Exit Points:**
- Treasure
- Home
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
- Treasure → Pay Bill
- Home → Pay Bill
- Notification tap (due bill)

**Exit Points:**
- Treasure
- Home
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
- Treasure → Payment history
- Stats → Transaction drill-down
- Goals → Contribution history

**Exit Points:**
- Home
- Treasure
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
- Treasure
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