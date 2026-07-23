# Budget Shield Design Contract

## Overview
Budget Shield is a native Android budgeting application presented as a dark fantasy finance game. The visual style is premium dark theme with cyan/teal primary accents and gold secondary accents.

## Color Palette

### Background Colors
- **BackgroundDark**: `#02070D` - Main app background (deep navy-black)
- **PanelDark**: `#06121D` - Card/panel backgrounds (slightly lighter navy)
- **FooterBackground**: `#06121D` - Bottom nav background
- **PanelBorder**: `#14364A` - Border lines, dividers

### Accent Colors
- **CyanAccent**: `#17E8F2` - Primary accent (bright cyan)
- **CyanSoft**: `#10CDD9` - Secondary cyan variant
- **GreenAccent**: `#2FE6A7` - Positive/success (emerald)
- **GoldAccent**: `#FFC545` - Secondary accent (gold)
- **BlueAccent**: `#1678B9` - Tertiary accent (royal blue)

### Text Colors
- **TextPrimary**: `#F4F7FB` - Main text (off-white)
- **TextMuted**: `#A6B1BF` - Secondary text (cool gray)
- **DangerDot**: `#FF553D` - Error/warning states (coral red)

### Utility Colors
- **CardHeroBackground**: `#0A1F2C` - Hero card background
- **CyanAccent15**: `#17E8F2` at 15% opacity - Button backgrounds
- **CyanAccent20**: `#17E8F2` at 20% opacity - Icon backgrounds
- **GoldAccent20**: `#FFC545` at 20% opacity - Reward icon background

## Typography

### Font Family
- Primary: System default (Roboto on Android)
- Weights: Normal (400), Medium (500), SemiBold (600), Bold (700), ExtraBold (800)

### Type Scale
| Token | Size | Weight | Line Height | Usage |
|-------|------|--------|-------------|-------|
| displayLarge | 42sp | ExtraBold | - | Safe Now amount |
| displayMedium | 32sp | Bold | - | Large numbers |
| displaySmall | 24sp | Bold | - | Shield power, streaks |
| headlineLarge | 22sp | Bold | - | Brand header |
| headlineMedium | 18sp | SemiBold | - | Card titles |
| headlineSmall | 16sp | SemiBold | - | Section headers |
| titleLarge | 16sp | Medium | - | Month selector |
| titleMedium | 14sp | Medium | - | Button labels |
| titleSmall | 13sp | Normal | - | Subtitle text |
| bodyLarge | 14sp | Normal | - | Transaction names |
| bodyMedium | 12sp | Normal | - | Labels, dates |
| bodySmall | 11sp | Normal | - | Bottom nav labels |
| labelSmall | 11sp | Medium | - | Active nav item |

## Shapes

### Border Radius Scale
| Token | Value | Usage |
|-------|-------|-------|
| small | 8dp | Small buttons, chips |
| medium | 12dp | Calendar button, small cards |
| large | 16dp | Standard cards, stat cards |
| xlarge | 20dp | Hero card |
| circular | 50% | Icon buttons, avatars |

### Card Elevation
- Default: 0dp (flat design with border)
- Hero: Custom border stroke (1dp) with 30% opacity cyan

## Component Tokens

### Bottom Navigation
- Height: Wrap content + navigationBarsPadding + 8dp bottom padding
- Background: `#06121D`
- Top border: 1dp `#14364A`
- Icon size: 22sp (emoji)
- Label size: 11sp
- Active color: `#17E8F2`
- Inactive color: `#A6B1BF`
- Padding: 8dp horizontal, 8dp vertical

### Hero Card (Safe Now)
- Background: `#0A1F2C`
- Border: 1dp stroke at 30% opacity (cyan if positive, red if shortage)
- Border radius: 20dp
- Padding: 20dp
- Amount text: 42sp ExtraBold
- Label text: 16sp Bold (cyan if positive, red if shortage)
- Subtitle: 13sp normal (muted)

### Stat Cards
- Background: `#06121D`
- Border radius: 16dp
- Padding: 16dp
- Icon: 20sp emoji
- Value: 22sp Bold
- Label: 11sp (muted)

### Action Buttons
- Background: 15% opacity CyanAccent
- Shape: Circle (36dp or 56dp)
- Icon: 18sp or 24sp emoji
- Label: 12sp (muted)

### Month Selector Card
- Background: `#06121D`
- Border radius: 16dp
- Padding: 12dp vertical, 8dp horizontal
- Arrow buttons: Text buttons with `‹` and `›`

### Activity Item
- Icon background: Circle (40dp) with 20% opacity
  - Income: GreenAccent
  - Bill: DangerDot
  - Savings: GoldAccent
- Name: 14sp Medium
- Date: 12sp (muted)
- Amount: 14sp SemiBold (color matches type)

## Iconography
Uses emoji as primary icon system:
- 🛡️ Shield/brand icon
- 🗡️ Hero/sword
- 🪙 Rewards/coin
- ☰ Menu
- 📅 Calendar
- 🔥 Streak
- ⚔️ Shield power
- 💰 Add income
- 💳 Pay bill
- 💎 Save money
- 🏠 Home
- 🧰 Treasure
- 📊 Stats
- 🎯 Goals
- ⚙️ Settings

## Spacing Scale
| Token | Value | Usage |
|-------|-------|-------|
| xxSmall | 2dp | Tight spacing |
| xSmall | 4dp | Icon to label |
| small | 8dp | Card padding, horizontal gutters |
| medium | 12dp | Section spacing, card content |
| large | 16dp | Section padding |
| xLarge | 20dp | Screen padding |
| xxLarge | 24dp | Large sections |

## Screen Layout
- Screen horizontal padding: 20dp
- Vertical padding: 16dp top, 24dp bottom
- Card spacing: 16dp
- Internal card spacing: 16dp

## Theme Contract

### Dark Theme (Primary)
| Token | Value |
|-------|-------|
| primary | `#17E8F2` |
| onPrimary | `#000000` |
| primaryContainer | `#004D40` |
| onPrimaryContainer | `#B2DFDB` |
| secondary | `#FFC545` |
| onSecondary | `#000000` |
| secondaryContainer | `#E65100` |
| onSecondaryContainer | `#FFE0B2` |
| background | `#02070D` |
| onBackground | `#F4F7FB` |
| surface | `#06121D` |
| onSurface | `#F4F7FB` |
| surfaceVariant | `#14364A` |
| onSurfaceVariant | `#A6B1BF` |
| outline | `#14364A` |

### Status Colors
- Success: `#2FE6A7`
- Warning: `#FF553D`
- Info: `#17E8F2`

## References
- `/screenshots/home_final.png` - Home screen reference
- `/screenshots/setup_quest.png` - Setup quest reference
- `/screenshots/chapter1_cash_on_hand.png` - Chapter 1 reference
- `/qa/task3/screenshots/home.png` - Task 3 home
- `/qa/task3/screenshots/treasure.png` - Treasure screen
- `/qa/task3/screenshots/bill-protected.png` - Bill protected screen
