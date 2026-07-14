# Test Plan

## Unit Tests

### Safe Now Calculation Tests
- Current cleared cash only
- Single bill, no income
- Single income, no bills
- Bill before payday
- Bill on payday
- Bill after payday
- Multiple bills across paydays
- Multiple income sources
- Recurring bills
- Partial bill payments
- Overdue bills
- Month boundary crossing
- Edge cases (zero amounts, same dates)

### Data Model Tests
- Income CRUD operations
- Bill CRUD operations
- Transaction CRUD operations
- Savings goal CRUD operations
- Settings persistence

## Integration Tests

### Flow Tests
- Setup Quest completion
- Add income → Safe Now updates
- Add bill → Safe Now updates
- Pay bill → Shield XP updates
- Month switching

### Calculation Integration
- Full Safe Now pipeline
- End-of-month transitions
- Recurring bill generation

## UI Tests

### Navigation Tests
- All screen transitions
- Back button behavior
- Deep linking (if implemented)

### Input Tests
- Valid input handling
- Invalid input validation
- Edge case inputs

## Manual QA

### Visual QA
- Screenshot comparison against reference images
- Dark theme consistency
- Typography consistency
- Spacing consistency

### Functional QA
- Every button tested
- Every input field tested
- Every navigation path tested
- Fresh install behavior
- Update behavior

### Device QA
- Different screen sizes
- Different Android versions (26-35)
- Rotation handling

## Test Requirements

### Before TASK 9
- Plan Safe Now unit tests
- Set up testing framework

### TASK 9
- Implement Safe Now unit tests
- All tests pass

### Before Beta
- Full QA pass on TASK 17
