# Safe Now Rules

## Calculation Contract

### Safe Now Must

1. **Start from actual cleared money** currently available.
2. **Account for every unpaid bill** and its exact due date.
3. **Account for confirmed future income** and its exact arrival date.
4. **Never use income that arrives after a bill is due** to protect that bill.
5. **Never treat an unpaid paycheck as current cash.**
6. **Exclude uncertain or optional income** unless the user marks it confirmed.
7. **Recalculate after** income, spending, bill payments, edits, deletions, or date changes.
8. **Never become negative** in the displayed spendable result.
9. **Show a warning** when bills are not fully protected.
10. **Explain which bill creates a shortage.**
11. **Support planning across multiple paychecks.**
12. **Correctly handle bills due before the next payday.**
13. **Correctly handle bills due on payday.**
14. **Correctly handle bills due after multiple future paydays.**
15. **Correctly handle overdue bills.**
16. **Correctly handle partial bill payments.**
17. **Correctly handle recurring bills crossing into the next month.**

---

## Baseline Projection Model

```
Projected balance on a date =
    current cleared cash
    + confirmed income arriving on or before that date
    - unpaid protected obligations due on or before that date
```

---

## Planning Horizon

The active planning period extends from today through the **latest protected unpaid obligation currently loaded**, with a minimum horizon through the **end of the next calendar month**.

- Example: If today is July 14, the minimum horizon is through August 31
- If there's a protected bill due September 5, the horizon extends to September 5
- Configurable later through UserSettings.planningHorizonMonths

---

## Same-Day Ordering Rule

**Confirmed income dated on a bill due date is available to protect that bill.**

The initial implementation uses date-level ordering and treats same-day confirmed income as available. Users can mark income as arriving "after" bill payment cutoff if needed.

Processing order for events on the same date:
1. Confirmed income (adds to balance)
2. Protected obligations (subtracts from balance)
3. End-of-day balance check

---

## Overdue Obligations

Treat every unpaid overdue protected bill as due **immediately today**. The projection subtracts the full remaining amount at today's date.

---

## Partial Payments

Subtract only the **unpaid remainder** of each bill occurrence when calculating projections.

```
remainingDue = amountDue - sum(paymentAllocations)
```

---

## Unprotected Bills

Unprotected bills are shown in the app but do NOT reserve money for them in Safe Now calculations. They can be paid with cash available after Safe Now.

---

## Unconfirmed Income

Unconfirmed income never protects bills and never increases Safe Now. It becomes available only when:
1. User marks it as confirmed, OR
2. It is received and a transaction is posted

---

## Actual Cleared Cash

Derived from the selected account opening balance plus posted ledger transactions only.

```
clearedCash = openingBalanceCents + sum(allPostedTransactions)
```

---

## Safe Now Displayed Result

```
Safe Now = max(0, minimum projected balance across all event dates in planning horizon)
```

If the minimum projected balance is below zero:
- Display Safe Now as **0**
- Separately expose:
  - Shortage amount (absolute value of negative balance)
  - First failing date
  - Bill occurrence(s) contributing to the failure

---

## Real-Time Updates

- **Spending entered today:** Reduces cleared cash immediately, triggers recalculation
- **Income confirmed:** Adds to projections from expected date
- **Income received:** Transaction posted, affects cleared cash
- **Bill paid:** Allocation recorded, remaining due reduced
- **Bill protection toggled:** Recalculates Safe Now immediately
- **Dates or amounts edited:** Invalidates and recalculates all affected projections

---

## Safe Now Worked Examples

All amounts in integer cents. Starting balance = $1000.00 = 100000 cents.

---

### Example 1: Bill Due Before Next Payday

**Events:**
- Today: July 14
- Bill due: July 20 (Rent $800.00 = 80000 cents, protected)
- Next payday: July 25 (confirmed $1500.00)

**Projection:**
| Date | Event | Balance Change | Projected Balance |
|------|-------|----------------|-------------------|
| Jul 14 | Starting | +100000 | 100000 |
| Jul 20 | Rent due | -80000 | 20000 |
| Jul 25 | Payday | +150000 | 170000 |

- **Safe Now:** 20000 cents ($200.00)
- **Shortage:** None
- **First failing bill:** None

The bill is before payday, so Safe Now only has starting cash minus rent.

---

### Example 2: Bill and Confirmed Income on Same Day

**Events:**
- Today: July 14
- Confirmed income: July 20 ($500.00)
- Bill due: July 20 ($300.00, protected)

**Projection (same-day income first):**
| Date | Event | Balance Change | Projected Balance |
|------|-------|----------------|-------------------|
| Jul 14 | Starting | +100000 | 100000 |
| Jul 20 | Income | +50000 | 150000 |
| Jul 20 | Bill | -30000 | 120000 |

- **Safe Now:** 100000 cents ($1000.00) — today's balance is the minimum
- **Shortage:** None
- **First failing bill:** None

Same-day income is available to protect same-day bills.

---

### Example 3: Income Arriving One Day After a Bill

**Events:**
- Today: July 14
- Bill due: July 20 ($800.00, protected)
- Confirmed income: July 21 ($1500.00)

**Projection:**
| Date | Event | Balance Change | Projected Balance |
|------|-------|----------------|-------------------|
| Jul 14 | Starting | +100000 | 100000 |
| Jul 20 | Bill | -80000 | 20000 |
| Jul 21 | Income | +150000 | 170000 |

- **Safe Now:** 20000 cents ($200.00)
- **Shortage:** None
- **First failing bill:** None

The income on July 21 cannot protect the bill on July 20.

---

### Example 4: Two Paychecks and Multiple Bills Across Two Months

**Events:**
- Today: July 14, Starting: $1000.00
- Payday 1: July 20 ($1500.00)
- Bill 1: July 25 (Rent $800.00, protected)
- Bill 2: July 28 (Utilities $150.00, protected)
- Payday 2: August 5 ($1500.00)
- Bill 3: August 10 (Insurance $200.00, protected)
- Bill 4: August 15 (Internet $80.00, protected)

**Projection:**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 20 | Payday | +150000 | 250000 |
| Jul 25 | Rent | -80000 | 170000 |
| Jul 28 | Utilities | -15000 | 155000 |
| Aug 5 | Payday | +150000 | 305000 |
| Aug 10 | Insurance | -20000 | 285000 |
| Aug 15 | Internet | -8000 | 277000 |

- **Safe Now:** 100000 cents ($1000.00) — starting balance is minimum
- **Shortage:** None

---

### Example 5: Overdue Bill

**Events:**
- Today: July 14
- Overdue bill: July 10 ($200.00, protected) — unpaid
- Next payday: July 20 ($500.00)

**Projection (overdue treated as today):**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 14 | Overdue | -20000 | 80000 |
| Jul 20 | Payday | +50000 | 130000 |

- **Safe Now:** 80000 cents ($800.00)
- **Shortage:** None
- **Note:** Overdue bills reduce Safe Now immediately

---

### Example 6: Partially Paid Bill

**Events:**
- Today: July 14
- Bill due: July 20 ($500.00, protected)
- Partial payment already recorded: $200.00
- Remaining due: $300.00
- Next payday: July 25 ($800.00)

**Projection:**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 20 | Remaining | -30000 | 70000 |
| Jul 25 | Payday | +80000 | 150000 |

- **Safe Now:** 70000 cents ($700.00)
- **Shortage:** None

Only the remaining $300 is subtracted, not the original $500.

---

### Example 7: Unconfirmed Side Income

**Events:**
- Today: July 14
- Bill due: July 20 ($800.00, protected)
- Unconfirmed income expected: July 19 ($500.00, not confirmed)
- Confirmed income: July 25 ($1000.00)

**Projection (unconfirmed not included):**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 20 | Bill | -80000 | 20000 |
| Jul 25 | Confirmed | +100000 | 120000 |

- **Safe Now:** 20000 cents ($200.00)
- **Shortage:** None

Unconfirmed income on July 19 is NOT used to protect the bill.

---

### Example 8: Unprotected Bill

**Events:**
- Today: July 14
- Protected bill: July 20 (Rent $800.00, protected)
- Unprotected bill: July 22 (Streaming $15.00, unprotected)
- Next payday: July 25 ($1000.00)

**Projection:**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 20 | Rent | -80000 | 20000 |
| Jul 25 | Payday | +100000 | 120000 |

- **Safe Now:** 20000 cents ($200.00)
- **Shortage:** None

Unprotected streaming bill does NOT reduce Safe Now.

---

### Example 9: Spending Transaction Causes Underfunding

**Events (initial):**
- Today: July 14
- Bill due: July 20 (Insurance $900.00, protected)
- Confirmed income: July 18 ($500.00)

**Initial projection:**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 18 | Income | +50000 | 150000 |
| Jul 20 | Bill | -90000 | 60000 |

- **Initial Safe Now:** 60000 cents ($600.00)

**After spending $100.00 on July 14:**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 14 | Spending | -10000 | 90000 |
| Jul 18 | Income | +50000 | 140000 |
| Jul 20 | Bill | -90000 | 50000 |

- **New Safe Now:** 50000 cents ($500.00)
- **Shortage:** None

**After spending $700.00 on July 14 (total $800 spent):**
| Date | Event | Change | Balance |
|------|-------|--------|---------|
| Jul 14 | Start | +100000 | 100000 |
| Jul 14 | Spending | -70000 | 30000 |
| Jul 18 | Income | +50000 | 80000 |
| Jul 20 | Bill | -90000 | -10000 |

- **Safe Now:** 0 cents ($0.00)
- **Shortage:** 10000 cents ($100.00)
- **First failing date:** July 20
- **Failing bill:** Insurance ($900.00 due, but only $800 available after income)

The spending transaction reduced available cash, causing the bill to become underfunded.

---

## Testing Requirement

The implementation must be covered by **deterministic unit tests** for every example above.

## Implementation Note

Do not implement the calculation during Task 2. This document specifies the contract for Task 9.