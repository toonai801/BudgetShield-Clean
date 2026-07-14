# Data Model Plan

## Overview

Local persistent storage using Room/SQLite.

## Entities (Planned)

### Income
- ID
- Name
- Amount
- Payday schedule (weekly, biweekly, monthly, etc.)
- Start date
- Is confirmed (boolean)
- Created at
- Updated at

### Bill
- ID
- Name
- Amount
- Due date
- Recurrence (none, weekly, biweekly, monthly, etc.)
- Is paid (boolean)
- Is protected (boolean)
- Category
- Created at
- Updated at

### Transaction
- ID
- Type (income, bill_payment, savings, spending)
- Amount
- Date
- Description
- Related income ID (optional)
- Related bill ID (optional)
- Created at

### SavingsGoal
- ID
- Name
- Target amount
- Current amount
- Deadline (optional)
- Is completed (boolean)
- Created at
- Updated at

### UserSettings
- ID
- Currency
- Theme preference
- Notifications enabled
- Created at
- Updated at

### ShieldProgress
- ID
- Current XP
- Current level
- Streak days
- Created at
- Updated at

## Calculation Data

Safe Now calculation will use:
- Current cleared cash
- Unpaid bills with due dates
- Confirmed income with arrival dates
- Projected balances across planning period

## Notes

This is a planning document. Implementation in future tasks.
