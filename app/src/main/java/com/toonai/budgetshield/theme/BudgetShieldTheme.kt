package com.toonai.budgetshield.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Budget Shield Theme
// Premium dark fantasy-finance aesthetic
// Primary: Cyan/Teal, Secondary: Gold, Background: Deep Navy-Black

// ============================================
// DARK COLOR SCHEME (PRIMARY)
// ============================================
private val DarkColorScheme = darkColorScheme(
    // Primary colors (Cyan/Teal)
    primary = CyanAccent,
    onPrimary = BackgroundDark,
    primaryContainer = CyanDark,
    onPrimaryContainer = CyanLight,

    // Secondary colors (Gold)
    secondary = GoldAccent,
    onSecondary = BackgroundDark,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,

    // Tertiary colors (Blue)
    tertiary = BlueAccent,
    onTertiary = TextPrimary,
    tertiaryContainer = BlueAccent.copy(alpha = 0.2f),
    onTertiaryContainer = TextPrimary,

    // Background colors
    background = BackgroundDark,
    onBackground = TextPrimary,

    // Surface colors
    surface = PanelDark,
    onSurface = TextPrimary,
    surfaceVariant = BorderSubtle,
    onSurfaceVariant = TextMuted,

    // Error colors
    error = DangerDot,
    onError = TextPrimary,
    errorContainer = DangerDot.copy(alpha = 0.2f),
    onErrorContainer = DangerDot,

    // Outline
    outline = PanelBorder,
    outlineVariant = BorderSubtle,

    // Surface tints
    surfaceTint = CyanAccent,
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
    inversePrimary = CyanDark,

    // Scrims
    scrim = BackgroundDark.copy(alpha = 0.8f)
)

// ============================================
// LIGHT COLOR SCHEME (SECONDARY)
// ============================================
// Light theme is available but dark is the primary aesthetic
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = Color(0xFF00695C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF004D40),

    // Secondary colors
    secondary = Color(0xFFE65100),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFBF360C),

    // Background colors
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF212121),

    // Surface colors
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF616161),

    // Error colors
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Outline
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0)
)

// ============================================
// THEME COMPOSABLE
// ============================================

/**
 * Budget Shield Theme
 *
 * A premium dark fantasy-finance theme featuring:
 * - Deep navy-black background (#02070D)
 * - Bright cyan primary accent (#17E8F2)
 * - Rich gold secondary accent (#FFC545)
 * - Emerald green for positive states (#2FE6A7)
 * - Coral red for error/warning states (#FF553D)
 *
 * @param darkTheme Whether to use dark theme (default: true or system setting)
 * @param dynamicColor Whether to use dynamic colors on Android 12+ (default: false)
 * @param content The composable content to theme
 */
@Composable
fun BudgetShieldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is disabled to maintain consistent fantasy-finance aesthetic
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BudgetShieldTypography,
        shapes = BudgetShieldShapes,
        content = content
    )
}

// ============================================
// LEGACY THEME COMPOSABLE
// ============================================

/**
 * Legacy BudgetShieldTheme for backward compatibility
 * Always uses dark theme with fantasy-finance colors
 */
@Composable
fun BudgetShieldThemeLegacy(
    content: @Composable () -> Unit
) {
    BudgetShieldTheme(darkTheme = true, content = content)
}

// ============================================
// COMPOSABLE UTILITIES
// ============================================

/**
 * Determines text color based on surface
 * Returns TextPrimary for dark surfaces, dark text for light
 */
@Composable
fun surfaceTextColor(isDarkSurface: Boolean = true) = if (isDarkSurface) {
    TextPrimary
} else {
    BackgroundDark
}

/**
 * Determines accent color for state
 * Returns CyanAccent for positive, DangerDot for negative
 */
@Composable
fun stateAccentColor(isPositive: Boolean) = if (isPositive) {
    CyanAccent
} else {
    DangerDot
}

// ============================================
// COLOR EXTENSIONS
// ============================================

/**
 * Transaction type tokens for theming
 */
enum class TransactionTypeToken {
    INCOME,
    BILL_PAYMENT,
    SAVINGS
}

/**
 * Extension to get the appropriate icon background color
 * for a given transaction type
 */
fun transactionIconBackgroundColor(type: TransactionTypeToken): Color {
    return when (type) {
        TransactionTypeToken.INCOME -> GreenAccent20
        TransactionTypeToken.BILL_PAYMENT -> DangerDot20
        TransactionTypeToken.SAVINGS -> GoldAccent20
    }
}

/**
 * Extension to get the appropriate text color
 * for a given transaction type
 */
fun transactionTextColor(type: TransactionTypeToken): Color {
    return when (type) {
        TransactionTypeToken.INCOME -> GreenAccent
        TransactionTypeToken.BILL_PAYMENT -> DangerDot
        TransactionTypeToken.SAVINGS -> GoldAccent
    }
}
