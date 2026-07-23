package com.toonai.budgetshield.theme

import androidx.compose.ui.graphics.Color

// Budget Shield Fantasy-Finance Color System
// Premium dark theme with cyan/teal primary and gold secondary

// ============================================
// BACKGROUND COLORS
// ============================================

/**
 * Main app background - Deep navy-black
 * Usage: Root surface background
 */
val BackgroundDark = Color(0xFF02070D)

/**
 * Panel/card backgrounds - Slightly lighter navy
 * Usage: Cards, panels, bottom nav
 */
val PanelDark = Color(0xFF06121D)

/**
 * Hero card background
 * Usage: Safe Now card, prominent cards
 */
val CardHeroBackground = Color(0xFF0A1F2C)

// ============================================
// BORDER & DIVIDER COLORS
// ============================================

/**
 * Border lines and dividers
 * Usage: Card borders, section dividers, nav borders
 */
val PanelBorder = Color(0xFF14364A)

/**
 * Subtle borders
 * Usage: Less prominent borders
 */
val BorderSubtle = Color(0xFF1A3A4E)

// ============================================
// PRIMARY ACCENT COLORS (CYAN/TEAL)
// ============================================

/**
 * Primary accent - Bright cyan
 * Usage: Active states, primary buttons, hero text
 */
val CyanAccent = Color(0xFF17E8F2)

/**
 * Secondary cyan variant
 * Usage: Gradients, hover states
 */
val CyanSoft = Color(0xFF10CDD9)

/**
 * Darker cyan for backgrounds
 * Usage: Container backgrounds
 */
val CyanDark = Color(0xFF004D40)

/**
 * Light cyan for text on dark backgrounds
 * Usage: Text on primary containers
 */
val CyanLight = Color(0xFFB2DFDB)

// ============================================
// SECONDARY ACCENT COLORS (GOLD)
// ============================================

/**
 * Secondary accent - Rich gold
 * Usage: Rewards, streaks, achievements, highlights
 */
val GoldAccent = Color(0xFFFFC545)

/**
 * Darker gold for backgrounds
 * Usage: Container backgrounds
 */
val GoldDark = Color(0xFFE65100)

/**
 * Light gold for text on dark backgrounds
 * Usage: Text on secondary containers
 */
val GoldLight = Color(0xFFFFE0B2)

// ============================================
// TERTIARY ACCENT COLORS
// ============================================

/**
 * Tertiary accent - Royal blue
 * Usage: Tertiary actions, links
 */
val BlueAccent = Color(0xFF1678B9)

/**
 * Purple accent - Used in goals, achievements
 */
val PurpleAccent = Color(0xFF9D4EDD)

/**
 * Orange accent - Used in streaks, warnings
 */
val OrangeAccent = Color(0xFFFF8C42)

/**
 * Emerald green - Positive/success
 * Usage: Income, positive values, success states
 */
val GreenAccent = Color(0xFF2FE6A7)

// ============================================
// TEXT COLORS
// ============================================

/**
 * Primary text - Off-white
 * Usage: Headlines, important text, values
 */
val TextPrimary = Color(0xFFF4F7FB)

/**
 * Secondary text - Cool gray
 * Usage: Labels, hints, inactive text
 */
val TextMuted = Color(0xFFA6B1BF)

/**
 * Disabled text
 * Usage: Disabled states
 */
val TextDisabled = Color(0xFF6B7A8A)

// ============================================
// STATUS COLORS
// ============================================

/**
 * Error/Danger - Coral red
 * Usage: Errors, shortages, negative values, warnings
 */
val DangerDot = Color(0xFFFF553D)

/**
 * Warning - Amber
 * Usage: Caution states
 */
val Warning = Color(0xFFFFA726)

/**
 * Success - Emerald (alias for GreenAccent)
 */
val Success = GreenAccent

/**
 * Info - Cyan (alias for CyanAccent)
 */
val Info = CyanAccent

// ============================================
// OPACITY VARIANTS
// ============================================

/**
 * Cyan at 15% opacity - Button backgrounds
 */
val CyanAccent15 = CyanAccent.copy(alpha = 0.15f)

/**
 * Cyan at 20% opacity - Icon backgrounds
 */
val CyanAccent20 = CyanAccent.copy(alpha = 0.20f)

/**
 * Cyan at 30% opacity - Borders
 */
val CyanAccent30 = CyanAccent.copy(alpha = 0.30f)

/**
 * Cyan at 5% opacity - Subtle backgrounds
 */
val CyanAccent05 = CyanAccent.copy(alpha = 0.05f)

/**
 * Gold at 20% opacity - Icon backgrounds
 */
val GoldAccent20 = GoldAccent.copy(alpha = 0.20f)

/**
 * Green at 20% opacity - Icon backgrounds
 */
val GreenAccent20 = GreenAccent.copy(alpha = 0.20f)

/**
 * Danger at 20% opacity - Icon backgrounds
 */
val DangerDot20 = DangerDot.copy(alpha = 0.20f)

// ============================================
// GRADIENT STOPS
// ============================================

/**
 * Hero card gradient start
 */
val GradientCyanStart = CyanAccent.copy(alpha = 0.20f)

/**
 * Hero card gradient end
 */
val GradientCyanEnd = CyanAccent.copy(alpha = 0.05f)

// ============================================
// LEGACY COMPATIBILITY
// ============================================

// These ensure backward compatibility with existing code
@Deprecated("Use CyanAccent instead", ReplaceWith("CyanAccent"))
val PrimaryCyan = CyanAccent

@Deprecated("Use GoldAccent instead", ReplaceWith("GoldAccent"))
val PrimaryGold = GoldAccent

@Deprecated("Use GreenAccent instead", ReplaceWith("GreenAccent"))
val PrimaryGreen = GreenAccent

@Deprecated("Use PanelDark instead", ReplaceWith("PanelDark"))
val SurfaceDark = PanelDark
