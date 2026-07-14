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

## Baseline Projection Model

```
Projected balance on a date =
    current cleared cash
    + confirmed income arriving on or before that date
    - unpaid protected obligations due on or before that date
```

## Safe Now Calculation

Safe Now must be based on the **lowest projected available balance** across the active planning period.

## Testing Requirement

The implementation must eventually be covered by **deterministic unit tests**.

## Implementation Note

Do not implement the calculation during Task 2.
