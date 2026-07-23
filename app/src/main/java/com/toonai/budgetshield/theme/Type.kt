package com.toonai.budgetshield.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Budget Shield Typography System
// Fantasy-finance themed type scale

// ============================================
// FONT WEIGHTS
// ============================================
val FontWeightNormal = FontWeight.Normal       // 400
val FontWeightMedium = FontWeight.Medium     // 500
val FontWeightSemiBold = FontWeight.SemiBold // 600
val FontWeightBold = FontWeight.Bold         // 700
val FontWeightExtraBold = FontWeight.ExtraBold // 800

// ============================================
// TYPE SCALE
// ============================================

/**
 * Display Large - Safe Now amount, hero numbers
 * Size: 42sp, Weight: ExtraBold
 * Usage: Primary numeric displays
 */
val DisplayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightExtraBold,
    fontSize = 42.sp,
    lineHeight = 48.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Display Medium - Large numbers
 * Size: 32sp, Weight: Bold
 * Usage: Shield power, streak counts
 */
val DisplayMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightBold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.25).sp
)

/**
 * Display Small - Medium numbers
 * Size: 24sp, Weight: Bold
 * Usage: Secondary large values
 */
val DisplaySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightBold,
    fontSize = 24.sp,
    lineHeight = 30.sp
)

/**
 * Headline Large - Brand header
 * Size: 22sp, Weight: Bold
 * Usage: App title "Budget Shield"
 */
val HeadlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightBold,
    fontSize = 22.sp,
    lineHeight = 28.sp
)

/**
 * Headline Medium - Card titles
 * Size: 18sp, Weight: SemiBold
 * Usage: Section headers, card titles
 */
val HeadlineMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightSemiBold,
    fontSize = 18.sp,
    lineHeight = 24.sp
)

/**
 * Headline Small - Subsection headers
 * Size: 16sp, Weight: SemiBold
 * Usage: Card headers, section labels
 */
val HeadlineSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightSemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp
)

/**
 * Title Large - Navigation labels
 * Size: 16sp, Weight: Medium
 * Usage: Month selector, primary labels
 */
val TitleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightMedium,
    fontSize = 16.sp,
    lineHeight = 22.sp
)

/**
 * Title Medium - Button text
 * Size: 14sp, Weight: Medium
 * Usage: Button labels, action text
 */
val TitleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightMedium,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

/**
 * Title Small - Subtitle text
 * Size: 13sp, Weight: Normal
 * Usage: Secondary labels, hints
 */
val TitleSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightNormal,
    fontSize = 13.sp,
    lineHeight = 18.sp
)

/**
 * Body Large - Primary content
 * Size: 14sp, Weight: Normal
 * Usage: Transaction names, item titles
 */
val BodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightNormal,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

/**
 * Body Medium - Secondary content
 * Size: 12sp, Weight: Normal
 * Usage: Dates, descriptions, labels
 */
val BodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightNormal,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

/**
 * Body Small - Fine print
 * Size: 11sp, Weight: Normal
 * Usage: Bottom nav labels, stat labels
 */
val BodySmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightNormal,
    fontSize = 11.sp,
    lineHeight = 14.sp
)

/**
 * Label Large - Emphasized labels
 * Size: 12sp, Weight: Medium
 * Usage: Active nav items, emphasized labels
 */
val LabelLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightMedium,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

/**
 * Label Medium - Small labels
 * Size: 11sp, Weight: Medium
 * Usage: Small emphasized text
 */
val LabelMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightMedium,
    fontSize = 11.sp,
    lineHeight = 14.sp
)

/**
 * Label Small - Smallest labels
 * Size: 10sp, Weight: Medium
 * Usage: Badges, tags
 */
val LabelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeightMedium,
    fontSize = 10.sp,
    lineHeight = 12.sp
)

// ============================================
// MATERIAL 3 TYPOGRAPHY
// ============================================

/**
 * Complete Material 3 Typography for Budget Shield
 * Maps custom type scale to Material 3 tokens
 */
val BudgetShieldTypography = Typography(
    displayLarge = DisplayLarge,
    displayMedium = DisplayMedium,
    displaySmall = DisplaySmall,
    headlineLarge = HeadlineLarge,
    headlineMedium = HeadlineMedium,
    headlineSmall = HeadlineSmall,
    titleLarge = TitleLarge,
    titleMedium = TitleMedium,
    titleSmall = TitleSmall,
    bodyLarge = BodyLarge,
    bodyMedium = BodyMedium,
    bodySmall = BodySmall,
    labelLarge = LabelLarge,
    labelMedium = LabelMedium,
    labelSmall = LabelSmall
)

// ============================================
// ICON SIZES
// ============================================

/**
 * Small icon - 16sp
 * Usage: Inline icons, small indicators
 */
val IconSmall = 16.sp

/**
 * Medium icon - 18sp
 * Usage: Navigation icons, buttons
 */
val IconMedium = 18.sp

/**
 * Large icon - 20sp
 * Usage: Card icons, stat icons
 */
val IconLarge = 20.sp

/**
 * Extra large icon - 22sp
 * Usage: Bottom nav icons
 */
val IconXLarge = 22.sp

/**
 * Hero icon - 24sp
 * Usage: Action buttons
 */
val IconHero = 24.sp

/**
 * Display icon - 48sp
 * Usage: Hero card decorative icon
 */
val IconDisplay = 48.sp
