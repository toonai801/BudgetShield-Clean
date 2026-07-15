# Decisions

## Architecture-Independent Data Decisions

### Monetary Storage
- All monetary values stored as **Long cents** (integer minor units)
- Never use Float or Double for money
- Display: `$X.YY` format derived from cents / 100

### Schedule vs Occurrence Separation
- **Schedules** define recurrence rules and templates
- **Occurrences** represent dated obligations generated from schedules
- **Transactions** are immutable ledger events
- Historical occurrences linked to transactions remain immutable when schedules change

### Immutable Transaction Ledger
- Transaction ledger is the audit trail of truth
- Corrections create new transactions, never mutate existing ones
- Historical activity derived from ledger, not mutable current-state flags

### Same-Day Income Ordering
- Confirmed income dated on a bill due date is available to protect that bill
- Date-level ordering: income processed before obligations on the same date

### Safe Now Planning Horizon
- Planning horizon extends from today through the latest protected unpaid obligation
- Minimum horizon: through end of next calendar month
- Configurable via UserSettings.planningHorizonMonths

## Product Decisions

### App Name
**Budget Shield** — Native Android budgeting application presented as a game.

### Core Concept
A budgeting app that answers: "How much money can I safely spend right now while still paying every protected bill by its due date?"

### Primary Result Name
**Safe Now** — This term is fixed and cannot be renamed.

### Visual Style
Premium dark fantasy finance game aesthetic.

## Technical Decisions

### Platform
Native Android (Kotlin)

### Package
`com.toonai.budgetshield`

### Min SDK
26 (Android 8.0)

### Target SDK
35

### Theme
Material3 Dark with custom styling

### Storage
Local persistent storage (Room/SQLite planned)

### Architecture
Will be defined in TASK 3
