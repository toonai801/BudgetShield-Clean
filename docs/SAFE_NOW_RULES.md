# Safe Now Rules

**Status:** APPROVED — owner-approved with the recommended decisions on 2026-08-02

**Authority:** Normative calculation and explanation contract for Safe Now

**Implementation note:** This contract states required behavior. Section 13 lists known differences in current code.

## 1. Definition

**Safe Now** is the greatest non-negative amount the user can spend today while the projected spendable-cash balance remains non-negative at every event date through the planning horizon.

In plain language:

> Start with cleared spendable cash, add only confirmed income when it becomes available, subtract every protected unpaid bill when it is due, and use the lowest projected balance as Safe Now.

Safe Now is a projection, not a bank balance or guarantee.

## 2. Required inputs

| Input | Required representation | Rule |
|---|---|---|
| Today | Valid local calendar date, ISO `YYYY-MM-DD` | Establishes overdue handling and horizon start. |
| Cash on hand | Signed-safe `Long` cents | Cleared, spendable cash only. |
| Savings | `Long` cents, stored separately | Excluded unless explicitly transferred to cash. |
| Income occurrences | Name, `Long` cents, ISO date, confirmed, active | Only active and confirmed occurrences in the horizon count. |
| Bills | Name, amount, paid amount, ISO due date, protected, paid | Only protected unpaid remainders count. |
| Planning horizon | Positive month count plus latest protected obligation | Must cover the minimum horizon described below. |

All monetary arithmetic is exact integer-cent arithmetic. Calculation inputs must not pass through binary floating-point conversion.

## 3. Included and excluded amounts

### Included

- Cleared cash on hand at the start of today
- Active, confirmed income occurrences available on or after today and no later than the horizon end
- Remaining unpaid amount of every protected bill due on or before the horizon end

### Excluded

- Savings balances and savings goals
- Unconfirmed income
- Inactive income schedules
- Income outside the planning horizon
- Unprotected bills
- Fully paid bills
- The already-paid portion of a partially paid bill
- Fabricated, preview, or pending UI values
- Credit limits or possible borrowing
- Category budget limits, unless the owner separately approves them as Safe Now operands

## 4. Planning horizon

The horizon begins today.

The intended horizon end is the later of:

1. The end of the configured number of calendar months beginning with the current month; and
2. The due date of the latest protected unpaid obligation already known to the app.

The approved decision currently defines a minimum through the end of next calendar month and exposes `UserSettings.planningHorizonMonths`, defaulting to 2. A configuration below the approved minimum is invalid.

Example: if today is 2026-08-02 and the configured horizon is two calendar months, the minimum end is 2026-09-30. A protected bill due 2026-10-15 extends the horizon to 2026-10-15.

The implementation must use real month lengths. Synthetic dates such as February 31 are forbidden, even as intermediate horizon markers.

## 5. Event normalization

Before projection:

1. Validate all dates.
2. Reject or quarantine malformed financial records; do not silently include them under lexical string ordering.
3. Convert each protected unpaid bill to one debit equal to `max(0, amountCents - paidAmountCents)`.
4. Move an overdue protected bill's effective event date to today.
5. Expand each supported active income schedule into confirmed occurrences through the horizon.
6. Ignore zero-value bill remainders and inactive/unconfirmed income.
7. Detect arithmetic overflow and fail closed with an error state rather than wrapping.

## 6. Same-day ordering

Events on the same date are applied in this order:

1. Starting cash, on today only
2. Confirmed income becoming available that day
3. Protected bill remainders due that day, including overdue bills assigned to today

This order is fixed: confirmed income dated on a bill's due date is available to protect that bill.

Within the income group or bill group, item order must not affect the end-of-day projected balance. Explanations may use stable date/name/ID ordering for deterministic output.

## 7. Calculation

Let:

- `C` be cleared cash on hand.
- `I(d)` be the total included income on date `d`.
- `B(d)` be the total included protected bill remainder on date `d`.
- `P(d)` be the projected end-of-day balance through date `d`.

Then:

```text
P(d) = C + sum(I(t), today <= t <= d) - sum(B(t), today <= t <= d)
minimumProjectedBalance = min(C, P(d) for every event date d in the horizon)
SafeNow = max(0, minimumProjectedBalance)
```

The explicit inclusion of `C` ensures that an otherwise empty event list returns current cleared cash, not zero.

If the minimum projected balance is negative:

```text
SafeNow = 0
shortageCents = absolute value of minimumProjectedBalance
firstFailingDate = earliest d where P(d) < 0
```

`shortageCents` describes the deepest projected shortage in the horizon. The first failing date describes when risk first appears. Both must be labeled distinctly.

## 8. Partial payments and bill state

- `remainingDueCents = max(0, amountCents - paidAmountCents)`.
- A partial payment immediately reduces the bill debit used in future calculations.
- A bill with `remainingDueCents == 0` is treated as fully paid even if a stale boolean flag disagrees; the inconsistency must be repaired or surfaced.
- A payment greater than the remaining amount is rejected unless an explicit overpayment policy is approved.
- Bill-payment persistence and transaction creation must be atomic so Safe Now cannot observe half of the operation.

## 9. Recurring income

Each active, confirmed schedule must generate occurrences deterministically:

- **weekly:** every 7 days from the stored next payday
- **biweekly:** every 14 days from the stored next payday
- **monthly:** same intended day in each following month; short-month adjustment policy requires owner approval
- **semimonthly/twice monthly:** uses two user-configured anchor days; a missing day clamps to that month's final valid day, and an anchor pair that would collapse onto the same date in any month is invalid and must be rejected
- **one time:** exactly one occurrence

The current model does not yet store both anchors. Until the approved model and migration changes are implemented and verified, the UI must not claim complete recurring projection support.

## 10. Result and explanation contract

### Positive result

The UI shows:

- Safe Now amount
- Horizon end or an accessible way to inspect it
- A concise explanation that protected obligations are covered under the current inputs
- A visible path to review the included bills and income

### Zero without shortage

When the minimum balance is exactly zero:

- Safe Now is `$0.00`.
- The result is not labeled as a shortage.
- The explanation states that all projected cash is required for protected obligations.

### Shortage

The UI shows:

- Safe Now `$0.00`
- Projected shortage amount
- First failing date
- Protected bill or bills contributing through that date
- A warning that is conveyed by text/semantics, not color alone

### Invalid or unavailable calculation

Invalid dates, arithmetic overflow, database failure, or inconsistent source data produce an explicit unavailable/error state. The app must not replace a failed calculation with `$0.00` as though that were a valid result.

## 11. Worked examples

All amounts are exact cents; dates use `YYYY-MM-DD`.

| # | Inputs | Expected result |
|---:|---|---|
| 1 | Cash $500; no included income or bills | Safe Now $500; no shortage. |
| 2 | Cash $500; protected bill $200 tomorrow | Projected minimum $300; Safe Now $300. |
| 3 | Cash $500; unprotected bill $200 tomorrow | Bill excluded; Safe Now $500. |
| 4 | Cash $100; confirmed income $400 on 08-10; protected bill $350 on 08-10 | Income precedes bill; minimum remains $100; Safe Now $100. |
| 5 | Cash $100; unconfirmed income $400 on 08-10; protected bill $350 on 08-10 | Income excluded; Safe Now $0; deepest shortage $250; first failure 08-10. |
| 6 | Cash $300; protected bill $100 with $40 already paid | Remaining debit $60; Safe Now $240. |
| 7 | Cash $100; overdue protected bill remainder $120 | Debit occurs today; Safe Now $0; shortage $20; first failure today. |
| 8 | Cash $600; bill $200 on 08-10 and bill $500 on 08-20; income $300 on 08-15 | Balances: $400, $700, $200; Safe Now $200. |
| 9 | Cash $250; protected bill $100 in horizon and another $500 beyond minimum horizon but already known/protected | Horizon extends to later bill; minimum -$350; Safe Now $0; shortage $350. |

These examples are normative. Test fixtures must use explicit dates rather than the device clock.

## 12. Required verification matrix

At minimum, automated tests must cover:

- No events
- Positive, zero, and shortage results
- Multiple event dates
- Multiple same-day income and bill events
- Same-day confirmed income ordering
- Unconfirmed and inactive income exclusion
- Overdue bill handling
- Protected/unprotected and paid/unpaid filtering
- Partial payment remainder
- Latest-obligation horizon extension
- Configured month horizon and actual month lengths, including leap year
- Each approved recurrence frequency
- Duplicate records and deterministic ordering
- Empty names without calculation corruption
- Negative/zero invalid source values
- `Long` boundary and overflow failure
- Invalid ISO dates and timezone/date rollover boundaries
- Database-to-calculator integration
- Process death/relaunch producing the same result
- UI explanation semantics for positive, zero, shortage, and error states

## 13. Task 20 implementation status

Task 20.1 at checkpoint `f051663` repaired the following calculator differences:

- Income must now be both confirmed and active.
- Weekly, biweekly, monthly, and one-time schedules expand through the horizon.
- `planningHorizonMonths` now produces a real calendar end date, and later known protected bills extend it.
- Dates are parsed and validated before calculation instead of compared lexically.
- Arithmetic overflow now fails closed.

The following differences remain open:

- Semimonthly/twice-monthly schedules cannot conform to the approved two-anchor rule until the schema, migration, repository, and UI store both anchors; the current single recorded payday is counted once rather than fabricated into a second date.
- Its shortage explanation lists all protected bills due through the first failing date, which may not clearly identify the event that first caused failure.
- A compatibility wrapper uses the device's current date implicitly, reducing deterministic testability.
- Invalid persisted financial data blocks calculation, but the guided record-level repair UI is not yet implemented.

The new focused tests and full JVM suite verify the completed portion only. Task 20 remains in progress, and this status does not waive the open requirements.

## 14. Approved owner decisions

1. Category budgets remain advisory and do not reduce Safe Now.
2. Monthly recurrence uses the same intended day when available and the month's final valid day otherwise.
3. Semimonthly/twice-monthly schedules use two user-configured payday anchors.
4. Confirmed income is available before protected bills at date-level precision on the same payday/due date.
5. Every known protected unpaid bill extends the horizon when later than the configured minimum horizon.
6. Invalid persisted financial data blocks calculation and presents guided repair; it is never silently ignored or converted to zero.
7. Semimonthly anchor days clamp to the month's final valid day when necessary; a pair that can collapse onto the same date is invalid and must be rejected rather than double-counted.

The full approval record is in `DECISIONS.md`. Core financial correctness has an initial verified checkpoint, but full conformity remains unverified until the open Task 20 implementation and evidence satisfy this contract.
