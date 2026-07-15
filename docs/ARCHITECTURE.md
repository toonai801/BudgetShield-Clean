# Budget Shield Architecture

## Single-Activity Compose Decision

Budget Shield uses a **single-activity architecture** with Jetpack Compose as the UI toolkit. This decision is locked for the project lifecycle.

### Why Single-Activity?

- **Navigation 3**: Type-safe navigation with serializable routes provides compile-time route safety
- **Back-stack management**: Native support for predictable back-stack behavior
- **State restoration**: Automatic state save/restore across configuration changes
- **Reduced complexity**: One activity eliminates activity lifecycle coordination issues
- **Modern Android**: Aligned with Google's recommended architecture patterns

### Migration from XML/AppCompat

Task 3 successfully migrated from the placeholder HomeActivity (AppCompat + XML) to MainActivity (ComponentActivity + Compose). The old shell has been removed:
- `HomeActivity.kt` deleted
- `activity_home.xml` deleted
- `appcompat` and `constraintlayout` dependencies removed

---

## Navigation 3 Type-Safe Routes

### Route Definition

All destinations are defined as serializable Kotlin objects in `BudgetShieldRoute.kt`:

```kotlin
@Serializable
object Home

@Serializable
data class TransactionDetails(val transactionId: Long? = null)
```

### Navigation State Management

- Uses `rememberNavController()` for nav controller instance
- Uses `rememberNavBackStack()` for back-stack state
- Uses `NavHost` with `composable<T>()` for type-safe route registration

### Start Route

**Setup Quest** is the current start destination. This is temporary until first-run persistence is implemented in a future task. Fresh debug launches will always begin at Setup Quest.

### Back-Stack Rules

| Scenario | Behavior |
|----------|----------|
| Setup Quest complete | Replaces stack with Home (Setup Quest removed) |
| Home + system back | Exits app |
| Detail screens | Pop back to previous |
| Main destinations (Home/Treasure/Stats/Goals/Settings) | `launchSingleTop = true` prevents endless duplicates |
| Settings → Restart Setup Quest | Allowed temporary flow |

---

## Package Structure

```
com.toonai.budgetshield/
├── MainActivity.kt                 # Single activity entry point
├── BudgetShieldApp.kt             # Application class
├── navigation/
│   ├── BudgetShieldRoute.kt       # Type-safe route definitions (13 destinations)
│   └── BudgetShieldNavigation.kt  # NavHost with route wiring
└── ui/
    ├── theme/
    │   └── BudgetShieldTheme.kt   # Minimal dark theme (Task 3 placeholder)
    └── screens/
        ├── SetupQuestScreen.kt
        ├── HomeScreen.kt
        ├── TreasureScreen.kt
        ├── StatsScreen.kt
        ├── GoalsScreen.kt
        ├── SettingsScreen.kt
        ├── IncomeEntryScreen.kt
        ├── BillEntryScreen.kt
        ├── BillPaymentScreen.kt
        ├── SavingsEntryScreen.kt
        ├── TransactionDetailsScreen.kt
        ├── BillProtectedScreen.kt
        └── ShieldProgressionScreen.kt
```

---

## State Ownership

### Current (Task 3)

- **UI State**: Managed by composables
- **Navigation State**: Managed by Navigation 3
- **No ViewModels**: Not yet implemented
- **No Repository**: Not yet implemented
- **No Database**: Not yet implemented

### Future Tasks

| Task | Responsibility |
|------|---------------|
| Task 7-8 | ViewModels for income/bill management |
| Task 9 | Safe Now calculation in domain layer |
| Task 10 | Live data integration with Home screen |
| Task 11-13 | Transaction, savings, XP logic |

### Boundaries

- **Composable**: Render state, handle user input, delegate business logic
- **ViewModel (future)**: Hold business state, survive configuration changes
- **Repository (future)**: Abstract data sources
- **Data layer (future)**: Room database, shared preferences

---

## Technical Foundation

### Pinned Versions

| Component | Version |
|-----------|---------|
| Android Gradle Plugin | 8.13.2 |
| Gradle | 8.13 |
| Kotlin | 2.2.21 |
| compileSdk | 36 |
| targetSdk | 35 |
| minSdk | 26 |
| Compose BOM | 2025.06.00 |
| Navigation Compose | 2.8.7 |
| Kotlinx Serialization | 1.9.0 |

### Theme

Task 3 uses a **minimal dark theme** (`BudgetShieldTheme`). This is explicitly NOT the final fantasy-finance styling. Task 4 will implement:
- Premium dark fantasy aesthetic
- Teal/cyan primary glow
- Gold reward accents
- Treasure chest and shield imagery
- Reference screenshot matching

---

## How Future Tasks Attach

### Task 4 (Design System)

- Replaces `BudgetShieldTheme.kt` with final design tokens
- Adds typography, color palette, spacing definitions
- No navigation changes required

### Tasks 7-13 (Features)

- Add ViewModels in `ui/viewmodels/`
- Add domain models in `domain/`
- Add repositories in `data/repositories/`
- Add Room entities in `data/entities/`
- Navigation remains unchanged
- Screens receive ViewModels as parameters

### Task 9 (Safe Now)

- Adds `domain/safenow/` calculation engine
- Uses data from repositories
- Home screen observes calculation results
- Navigation already wired

---

## Testing Strategy

### Unit Tests

- Safe Now calculation (Task 9)
- Domain logic
- Repository behavior

### UI Tests

- Navigation smoke tests (Task 3 complete)
- Screen behavior
- Flow validation

### Instrumentation Tests

- `NavigationSmokeTest.kt` validates:
  - All 13 destinations reachable
  - Setup Quest stack replacement
  - Back-stack behavior
  - Bill Payment → Bill Protected flow

---

## Known Limitations (Task 3)

1. **Setup Quest**: Not persisted; always shows on fresh launch
2. **Safe Now**: Not calculated; shows placeholder
3. **Data**: No persistence; all screens are placeholders
4. **Theme**: Minimal dark, not final fantasy styling
5. **No real transactions**: Ledger not implemented
6. **No XP calculation**: Shield progression is static

All limitations will be addressed in subsequent tasks.

---

## Build Verification

Build command:
```bash
./gradlew clean testDebugUnitTest assembleDebug
```

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

GitHub Actions:
- Runs on push/PR to main
- Java 17, Gradle cache
- Tests + build
- APK artifact upload
