package com.toonai.budgetshield.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Budget Shield Shape System
// Fantasy-finance themed corner radius and shapes

// ============================================
// CORNER RADIUS VALUES
// ============================================

/**
 * Small corner radius - 4dp
 * Usage: Chips, small badges
 */
val CornerSmall = 4.dp

/**
 * Medium corner radius - 8dp
 * Usage: Small buttons, input fields
 */
val CornerMedium = 8.dp

/**
 * Large corner radius - 12dp
 * Usage: Calendar button, small cards, dialog buttons
 */
val CornerLarge = 12.dp

/**
 * Extra large corner radius - 16dp
 * Usage: Standard cards, stat cards, panels
 */
val CornerXLarge = 16.dp

/**
 * Extra extra large corner radius - 20dp
 * Usage: Hero cards, prominent cards
 */
val CornerXXLarge = 20.dp

/**
 * Full corner radius - 24dp
 * Usage: Large dialogs, bottom sheets
 */
val CornerFull = 24.dp

// ============================================
// SHAPE TOKENS
// ============================================

/**
 * Small shape - Rounded 4dp
 * Usage: Chips, tags, small badges
 */
val ShapeSmall = RoundedCornerShape(CornerSmall)

/**
 * Medium shape - Rounded 8dp
 * Usage: Small buttons, input fields
 */
val ShapeMedium = RoundedCornerShape(CornerMedium)

/**
 * Large shape - Rounded 12dp
 * Usage: Calendar button, action buttons
 */
val ShapeLarge = RoundedCornerShape(CornerLarge)

/**
 * Extra large shape - Rounded 16dp
 * Usage: Standard cards, stat cards, panels
 */
val ShapeXLarge = RoundedCornerShape(CornerXLarge)

/**
 * Extra extra large shape - Rounded 20dp
 * Usage: Hero cards, Safe Now card
 */
val ShapeXXLarge = RoundedCornerShape(CornerXXLarge)

/**
 * Full shape - Rounded 24dp
 * Usage: Large dialogs, prominent cards
 */
val ShapeFull = RoundedCornerShape(CornerFull)

/**
 * Circular shape
 * Usage: Icon buttons, avatars, FABs
 */
val ShapeCircular = CircleShape

// ============================================
// MATERIAL 3 SHAPES
// ============================================

/**
 * Complete Material 3 Shapes for Budget Shield
 * Maps custom shape tokens to Material 3 shape categories
 */
val BudgetShieldShapes = Shapes(
    small = ShapeMedium,      // 8dp - For small components like buttons
    medium = ShapeXLarge,     // 16dp - For medium components like cards
    large = ShapeXXLarge      // 20dp - For large components like dialogs
)

// ============================================
// COMPONENT-SPECIFIC SHAPES
// ============================================

/**
 * Hero card shape - 20dp
 * Usage: Safe Now card
 */
val HeroCardShape = ShapeXXLarge

/**
 * Stat card shape - 16dp
 * Usage: Streak, Shield Power, Shielded cards
 */
val StatCardShape = ShapeXLarge

/**
 * Action card shape - 16dp
 * Usage: Daily Actions card
 */
val ActionCardShape = ShapeXLarge

/**
 * Month selector shape - 16dp
 * Usage: Month selector card
 */
val MonthSelectorShape = ShapeXLarge

/**
 * Activity card shape - 16dp
 * Usage: Recent Activity card
 */
val ActivityCardShape = ShapeXLarge

/**
 * Calendar button shape - 12dp
 * Usage: Calendar icon button
 */
val CalendarButtonShape = ShapeLarge

/**
 * Action button shape - Circular
 * Usage: Add Income, Pay Bill, Save Money buttons
 */
val ActionButtonShape = ShapeCircular

/**
 * Icon container shape - Circular
 * Usage: Small icon backgrounds
 */
val IconContainerShape = ShapeCircular

/**
 * Bottom navigation shape - 0dp (top border only)
 * Usage: Bottom nav bar (square with top border)
 */
val BottomNavShape = RoundedCornerShape(0.dp)

// ============================================
// BORDER STROKES
// ============================================

/**
 * Thin border - 1dp
 * Usage: Subtle borders, dividers
 */
val BorderThin = 1.dp

/**
 * Medium border - 2dp
 * Usage: Emphasized borders
 */
val BorderMedium = 2.dp

/**
 * Thick border - 3dp
 * Usage: Hero borders, prominent outlines
 */
val BorderThick = 3.dp

// ============================================
// CARD DIMENSIONS
// ============================================

/**
 * Standard card padding - 16dp
 * Usage: Internal card content padding
 */
val CardPadding = 16.dp

/**
 * Large card padding - 20dp
 * Usage: Hero card padding
 */
val CardPaddingLarge = 20.dp

/**
 * Small card padding - 12dp
 * Usage: Compact cards
 */
val CardPaddingSmall = 12.dp

/**
 * Card horizontal margin - 20dp
 * Usage: Screen edge to card spacing
 */
val CardHorizontalMargin = 20.dp

/**
 * Card vertical spacing - 16dp
 * Usage: Space between stacked cards
 */
val CardVerticalSpacing = 16.dp

// ============================================
// ICON CONTAINER SIZES
// ============================================

/**
 * Small icon container - 32dp
 * Usage: Header icons
 */
val IconContainerSmall = 32.dp

/**
 * Medium icon container - 36dp
 * Usage: Reward button icon
 */
val IconContainerMedium = 36.dp

/**
 * Standard icon container - 40dp
 * Usage: Activity item icons
 */
val IconContainerStandard = 40.dp

/**
 * Large icon container - 56dp
 * Usage: Action buttons
 */
val IconContainerLarge = 56.dp

/**
 * Hero icon container - 100dp
 * Usage: Hero card decorative icon
 */
val IconContainerHero = 100.dp
