# Data Model Plan

## Money and Date Rules

### Monetary Values
- Store all monetary values as **integer minor units using Long cents**
- Never use Float or Double for money calculations
- Display format: `$X.YY` derived from cents / 100

### Identifiers and Timestamps
- Use stable UUID or auto-generated Long IDs for all entities
- Include explicit `createdAt` and `updatedAt` timestamps on all mutable entities
- Timestamps stored as epoch milliseconds (Long)

### Date Handling
- Date-only values (due dates, income dates): LocalDate stored as YYYY-MM-DD string or epoch day
- Timezone handling: UserSettings stores preferred timezone; timestamps stored in UTC, displayed in user timezone
- Planning horizon calculations use date-level comparison

### Transaction Ledger
- The transaction ledger is the immutable audit trail
- Historical activity is derived from ledger entries, not from mutable current-state flags
- Corrections are new ledger entries, not mutations to existing entries

---

## Planned Entities

### Account / CashSource
The source of actual cleared money and its opening balance.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Display name (e.g., "Checking", "Cash") |
| openingBalanceCents | Long | Opening balance in cents |
| currentBalanceCents | Long | Derived: opening + posted transactions |
| isDefault | Boolean | Primary account for Safe Now |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

### IncomeSchedule
Recurrence rules and expected amount for an income source.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Source name (e.g., "Paycheck") |
| amountCents | Long | Expected amount in cents |
| recurrenceRule | String | RRULE or custom (weekly, biweekly, monthly, etc.) |
| startDate | String | First occurrence date (YYYY-MM-DD) |
| timezone | String | Timezone for date calculations |
| isConfirmedTemplate | Boolean | Default confirmation status for occurrences |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

### IncomeOccurrence
One dated expected or received income event.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| scheduleId | Long | Foreign key to IncomeSchedule |
| expectedDate | String | Expected date (YYYY-MM-DD) |
| expectedAmountCents | Long | Expected amount in cents |
| isConfirmed | Boolean | User has confirmed this will arrive |
| receivedDate | String | Actual received date (null if not yet) |
| receivedAmountCents | Long | Actual amount received (null if not yet) |
| transactionId | Long? | Linked ledger transaction when received |
| isGenerated | Boolean | True if auto-generated from schedule |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

### BillSchedule
Recurring bill template, normal amount, category, and recurrence rule.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Bill name (e.g., "Rent") |
| normalAmountCents | Long | Typical amount in cents |
| category | String | Category for grouping |
| recurrenceRule | String | RRULE or custom (monthly, weekly, etc.) |
| dueDateDay | Int | Day of month/week for due date |
| isProtectedDefault | Boolean | Default protection status |
| endDate | String? | Optional end date for recurrence |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

**Note:** No `isPaid` field on the schedule. Payment status belongs to each BillOccurrence.

### BillOccurrence
One dated obligation generated from a schedule.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| scheduleId | Long | Foreign key to BillSchedule |
| dueDate | String | Due date (YYYY-MM-DD) |
| amountDueCents | Long | Amount due in cents |
| isProtected | Boolean | Whether Safe Now reserves for this |
| status | Enum | UPCOMING, DUE, OVERDUE, PAID, PARTIAL |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

**Calculated fields (not stored):**
- amountPaidCents: Sum of linked BillPaymentAllocation amounts
- remainingDueCents: amountDueCents - amountPaidCents

### Transaction
Immutable ledger event for income, spending, bill payment, savings, correction, or transfer.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| type | Enum | INCOME_RECEIVED, SPENDING, BILL_PAYMENT, SAVINGS, CORRECTION, TRANSFER |
| amountCents | Long | Amount in cents (positive for inflow, negative for outflow) |
| date | String | Transaction date (YYYY-MM-DD) |
| postedAt | Long | Timestamp when recorded |
| description | String | User-entered description |
| accountId | Long | Account affected |
| isReversalOf | Long? | If this corrects another transaction |
| createdAt | Long | Timestamp (immutable after creation) |

### BillPaymentAllocation
Links one payment transaction to one or more bill occurrences.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| transactionId | Long | The BILL_PAYMENT transaction |
| billOccurrenceId | Long | Which bill occurrence was paid |
| allocatedAmountCents | Long | Amount allocated to this bill |
| createdAt | Long | Timestamp |

Supports partial payments: multiple allocations can exist for one bill if paid in parts.

### BudgetCategory / BudgetBucket
Month-specific budget tracking for Wants and Food.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Category name ("Wants", "Food") |
| monthKey | String | YYYY-MM for this budget instance |
| plannedAmountCents | Long | Budgeted amount in cents |
| spentAmountCents | Long | Derived from transactions |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

### SavingsGoal
Goal definition separated from contribution history.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | Goal name |
| targetAmountCents | Long | Target amount in cents |
| deadline | String? | Optional deadline (YYYY-MM-DD) |
| priority | Int | Display order |
| isCompleted | Boolean | Marked complete by user |
| completedAt | Long? | Timestamp when completed |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

### SavingsContribution
Individual contribution to a savings goal.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| goalId | Long | Which goal (null for general savings) |
| amountCents | Long | Contribution amount |
| contributionDate | String | Date contributed |
| transactionId | Long | Linked SAVINGS transaction |
| note | String? | Optional note |
| createdAt | Long | Timestamp |

**Note:** `currentAmount` is NOT stored on the goal; it is calculated from contribution history.

### ShieldProgress
Current progression state.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key (single row) |
| currentXp | Long | Current XP total |
| currentLevel | Int | Calculated level |
| streakDays | Int | Consecutive days with savings |
| lastSavingsDate | String? | Date of last savings contribution |
| updatedAt | Long | Timestamp |

### ShieldXpEvent
Immutable XP history and reason.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| amount | Int | XP gained (positive) |
| reason | String | Description of why XP was awarded |
| category | Enum | INCOME, BILL_PAID, SAVINGS, STREAK, ACHIEVEMENT |
| relatedEntityId | Long? | Related bill, goal, etc. |
| earnedAt | Long | Timestamp |
| createdAt | Long | Timestamp |

### UserSettings
Currency, timezone, first-run completion, notifications, and planning horizon.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key (single row) |
| currency | String | Currency code (USD, EUR, etc.) |
| timezone | String | IANA timezone ID |
| isFirstRunComplete | Boolean | Setup Quest completed |
| notificationsEnabled | Boolean | Push notifications on/off |
| dailyReminderTime | String? | HH:MM for daily reminder |
| billReminderDaysBefore | Int | Days before due date to remind |
| planningHorizonMonths | Int | Default: 2 months minimum horizon |
| createdAt | Long | Timestamp |
| updatedAt | Long | Timestamp |

---

## Relationships and Constraints

### Schedule → Occurrence Integrity
- Deleting or editing a schedule must NOT silently rewrite completed historical occurrences
- Historical occurrences (linked to transactions) remain immutable
- Future generated occurrences can be regenerated if schedule changes

### Occurrence Distinguishability
- Generated future occurrences have `isGenerated = true`
- Manually entered one-time occurrences have `isGenerated = false`
- Both types can coexist in the same date range

### Income Linkage
- A received income occurrence must link to its ledger transaction via `transactionId`
- This enables audit trail from projected income to actual cash

### Bill Payment Linkage
- A bill occurrence can have zero, one, or multiple payment allocations
- Total allocated = sum of BillPaymentAllocation amounts
- Remaining due = amountDueCents - total allocated

### Transaction Corrections
- Corrections create new transactions, never mutate existing ones
- New transaction can reference original via `isReversalOf`
- Original remains in ledger as "reversed by transaction X"

### Month Views
- Month views based on dates and occurrences
- No single global `isPaid` flag drives the view
- Query aggregates occurrences and transactions by date range

---

## Calculation Data

Safe Now calculation will use:
- Account opening balance (Long cents)
- Posted transactions (verified ledger)
- Confirmed income occurrences with arrival dates
- Protected bill occurrences with due dates
- Planning horizon from UserSettings

All calculations use Long cents to avoid floating-point errors.

## Notes

This is a planning document. Implementation in future tasks. The occurrence-based model ensures:
- Immutable financial history
- Accurate partial payment tracking
- Clear audit trail from schedule to occurrence to transaction
- Safe Now calculations based on actual posted ledger entries