# Test Plan

## Unit Tests

### Safe Now Calculation Tests
Deterministic tests for Safe Now calculation engine:

| Test Case | Description | Reference |
|-----------|-------------|-----------|
| Baseline | Current cleared cash only, no bills or income | Starting state |
| Single Bill | One protected bill, no income | Safe Now reduced by bill amount |
| Single Income | One confirmed income, no bills | Safe Now equals starting cash (income not yet received) |
| Bill Before Payday | Bill due before next confirmed income | Safe Now must cover bill from starting cash |
| Bill On Payday | Bill and confirmed income same day | Same-day ordering: income available for bill |
| Income Before Bill | Confirmed income arrives before bill due | Income CAN protect the bill |
| Income After Bill | Confirmed income arrives after bill due | Income CANNOT protect the earlier bill |
| Multiple Bills Cross-Paydays | Two paydays with bills distributed | Minimum balance across full horizon |
| Multiple Income Sources | Salary + side income, different dates | Aggregate confirmed income |
| Recurring Bills | Monthly bills across months | Generated occurrences respected |
| Partial Payment | Bill partially paid, remainder due | Only remaining due reduces Safe Now |
| Overdue Bill | Past-due protected bill | Treated as due today, immediate reduction |
| Month Boundary | Bills crossing calendar months | Horizon extends correctly |
| Unconfirmed Income | Expected but not confirmed income | Never used in projections |
| Unprotected Bill | Bill not marked protected | Does NOT reduce Safe Now |
| Spending Causes Shortage | Transaction reduces available cash | Recalculation detects underfunding |

### Safe Now Worked Example Tests (Deterministic)
Must match the 9 worked examples in SAFE_NOW_RULES.md:
1. Bill due before next payday
2. Bill and confirmed income on same day
3. Income arriving one day after a bill
4. Two paychecks and multiple bills across two months
5. Overdue bill
6. Partially paid bill
7. Unconfirmed side income
8. Unprotected bill
9. Spending transaction causing underfunding — **shortage must be 20000 cents ($200.00)**

Each test must:
- Use integer cents (Long) for all amounts
- Specify exact dates for all events
- Assert minimum projected balance equals expected Safe Now
- Assert shortage amount (if any) is correctly calculated
- Assert first failing date and bill identified

### Data Model Tests
- IncomeSchedule CRUD operations
- IncomeOccurrence generation and confirmation
- **IncomeOccurrence.scheduleId nullable for manual one-time income**
- **IncomeOccurrence.isGenerated true (schedule) vs false (manual)**
- **IncomeOccurrence.receivedDate and receivedAmountCents null until received**
- BillSchedule CRUD operations
- BillOccurrence generation and status
- **BillOccurrence.scheduleId nullable for manual one-time bills**
- **BillOccurrence.isGenerated true (schedule) vs false (manual)**
- **BillOccurrence status derived (not stored): UPCOMING, DUE, OVERDUE, PAID, PARTIAL**
- Transaction immutable ledger (create, never mutate)
- BillPaymentAllocation partial payment linking
- SavingsGoal and SavingsContribution separation
- **SavingsContribution.goalId nullable for general savings**
- ShieldProgress and ShieldXpEvent history
- UserSettings persistence
- Long cents overflow protection
- **Account currentBalanceCents derived from opening + transactions (not stored)**
- **Duplicate occurrence prevention via (scheduleId, date) unique index**
- **Schedule deletion preserves historical occurrences and ledger-linked records**

## Integration Tests

### Flow Tests
- Setup Quest completion through all 6 chapters
- Add income → IncomeOccurrence generated → Safe Now updates
- Add bill → BillOccurrence generated → Safe Now updates
- Pay bill → BillPaymentAllocation created → Shield XP awarded
- Month switching → occurrence regeneration
- Recurring schedule edit → future occurrences updated, historical preserved

### Calculation Integration
- Full Safe Now pipeline with all entity types
- End-of-month transition handling
- Recurring bill occurrence generation
- Cross-account transfers (future)

## UI Tests

### Navigation Tests
All required destinations from SCREEN_MAP.md:
- Home → Treasure, Stats, Goals, Settings, Income Entry, Bill Entry, Savings Entry, Transaction Details, Shield Progression
- Setup Quest → Home (completion)
- Treasure → Bill Entry, Bill Payment, Transaction Details
- Stats → Goals, Settings, Transaction Details
- Goals → Savings Entry, Transaction Details, Shield Progression
- Settings → Setup Quest (restart)
- Income Entry → Home, Setup Quest
- Bill Entry → Treasure, Home, Setup Quest
- Bill Payment → Treasure, Home, Bill Protected Achievement
- Savings Entry → Goals, Home
- Transaction Details → Home, Treasure, Stats, Goals
- Bill Protected Achievement → Home, Treasure, Shield Progression
- Shield Progression → Home, Goals, Settings

Back button behavior for each flow

### Input Tests
- Valid income amount entry (cents parsing)
- Invalid amount rejection (negative, overflow)
- Date picker bounds (past dates, future dates)
- Required field validation
- Edge case inputs (zero amounts, same dates, max Long)

## Manual QA

### Visual QA
- Screenshot comparison against reference images:
  - docs/reference/home-reference.png
  - docs/reference/setup-quest-reference.png
  - docs/reference/bill-protected-reference.png
- Dark theme consistency
- Typography consistency
- Spacing consistency
- Premium dark fantasy aesthetic verification

### Functional QA
- Every button tested on every screen
- Every input field tested
- Every navigation path tested
- Fresh install behavior (Setup Quest)
- Update behavior (existing user data preserved)

### Device QA
- Different screen sizes (small phone, large phone, tablet)
- Different Android versions (26-35)
- Rotation handling (portrait/landscape where supported)
- Dark mode system setting
- Accessibility features (TalkBack, large text)

## Test Requirements

### Before TASK 9
- Plan Safe Now unit tests (all cases + 9 worked examples)
- Set up testing framework (JUnit, MockK, etc.)
- Define test data fixtures

### TASK 9
- Implement Safe Now unit tests
- All deterministic tests pass
- Coverage includes all edge cases from SAFE_NOW_RULES.md

### Before Beta
- Full QA pass on TASK 17
- All manual QA checklists completed
- Screenshot evidence for visual QA
- Device matrix testing complete