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

## Reference Screens

All screens must match the visual language defined in DESIGN_CONTRACT.md.

### Home Screen
**Purpose:** Main dashboard showing Safe Now and financial overview

**Primary Information:**
- Safe Now amount (large, prominent)
- Next payday information
- Bills protected status
- Quick action buttons

**Required Actions:**
- View Safe Now details
- Add transaction
- Navigate to other screens

**Entry Points:**
- App launch (after setup)
- Return from other screens

**Exit Points:**
- Treasure screen
- Stats screen
- Goals screen
- Settings screen
- Transaction entry

**Empty State:** Setup Quest prompt (first run)

**Error State:** Retry / fallback display

**Data Dependencies:**
- Current cleared cash
- Income schedule
- Bill schedule
- Safe Now calculation

### Setup Quest Screen
**Purpose:** Onboarding new users

**Primary Information:**
- Welcome messaging
- Step-by-step guidance
- Progress indication

**Required Actions:**
- Enter initial income
- Enter bills
- Complete onboarding

**Entry Points:**
- First app launch

**Exit Points:**
- Home screen (on completion)

**Empty State:** N/A (first run only)

**Error State:** Validation errors with retry

**Data Dependencies:**
- User input only

### Treasure Screen
**Purpose:** View bills and protected money

**Primary Information:**
- Bill list with due dates
- Protected amounts
- Payment status

**Required Actions:**
- Add bill
- Edit bill
- Pay bill
- View bill details

**Entry Points:**
- Home navigation

**Exit Points:**
- Bill entry
- Bill payment
- Home

**Empty State:** "No bills yet" with add button

**Error State:** Retry / offline message

**Data Dependencies:**
- Bills list
- Payment history

### Bill Protected Achievement
**Purpose:** Celebrate bill payment success

**Primary Information:**
- Success message
- Visual reward
- XP gained

**Required Actions:**
- Dismiss
- Share (optional)

**Entry Points:**
- After bill payment

**Exit Points:**
- Home
- Treasure

**Empty State:** N/A

**Error State:** N/A

**Data Dependencies:**
- Bill that was paid
- XP calculation
