package com.toonai.budgetshield.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Budget Shield Design System
// Component tokens, patterns, and preview gallery

// ============================================
// COMPONENT TOKENS
// ============================================

/**
 * Border strokes for different prominence levels
 */
object BorderStrokes {
    /** Subtle border - 1dp with 30% opacity cyan */
    val subtleCyan = BorderStroke(
        width = BorderThin,
        color = CyanAccent30
    )

    /** Subtle border - 1dp with 30% opacity danger */
    val subtleDanger = BorderStroke(
        width = BorderThin,
        color = DangerDot.copy(alpha = 0.3f)
    )

    /** Standard border - 1dp panel border */
    val standard = BorderStroke(
        width = BorderThin,
        color = PanelBorder
    )

    /** No border for filled cards */
    val none = null
}

/**
 * Icon sizes for different contexts
 */
object IconSizes {
    /** Header icon size */
    val header = 18.sp

    /** Card icon size */
    val card = 20.sp

    /** Navigation icon size */
    val nav = 22.sp

    /** Action button icon size */
    val action = 24.sp

    /** Hero icon size */
    val hero = 48.sp
}

/**
 * Spacing values for consistent layout
 */
object Spacing {
    /** Tight spacing */
    val xxSmall = 2.dp

    /** Small spacing */
    val xSmall = 4.dp

    /** Standard small spacing */
    val small = 8.dp

    /** Medium spacing */
    val medium = 12.dp

    /** Large spacing */
    val large = 16.dp

    /** Extra large spacing */
    val xLarge = 20.dp

    /** Maximum spacing */
    val xxLarge = 24.dp

    /** Screen horizontal padding */
    val screenHorizontal = 20.dp

    /** Screen top padding */
    val screenTop = 16.dp

    /** Screen bottom padding */
    val screenBottom = 24.dp
}

// ============================================
// DESIGN SYSTEM PREVIEWS
// ============================================

@Preview(
    name = "Color Palette",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun ColorPalettePreview() {
    BudgetShieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Budget Shield Color Palette",
                    style = HeadlineLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Background colors
                SectionTitle("Background Colors")
                ColorSwatch("BackgroundDark", BackgroundDark)
                ColorSwatch("PanelDark", PanelDark)
                ColorSwatch("CardHeroBackground", CardHeroBackground)
                Spacer(modifier = Modifier.height(16.dp))

                // Accent colors
                SectionTitle("Accent Colors")
                ColorSwatch("CyanAccent", CyanAccent)
                ColorSwatch("CyanSoft", CyanSoft)
                ColorSwatch("GoldAccent", GoldAccent)
                ColorSwatch("GreenAccent", GreenAccent)
                ColorSwatch("BlueAccent", BlueAccent)
                Spacer(modifier = Modifier.height(16.dp))

                // Text colors
                SectionTitle("Text Colors")
                ColorSwatch("TextPrimary", TextPrimary)
                ColorSwatch("TextMuted", TextMuted)
                ColorSwatch("TextDisabled", TextDisabled)
                Spacer(modifier = Modifier.height(16.dp))

                // Status colors
                SectionTitle("Status Colors")
                ColorSwatch("DangerDot", DangerDot)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = HeadlineSmall,
        color = CyanAccent,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ColorSwatch(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = BodyLarge,
                color = TextPrimary
            )
            Text(
                text = color.toHexString(),
                style = BodyMedium,
                color = TextMuted
            )
        }
    }
}

private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

@Preview(
    name = "Typography Scale",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun TypographyPreview() {
    BudgetShieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Typography Scale",
                    style = HeadlineLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))

                TypeSample("Display Large", DisplayLarge, "$2,450.00")
                TypeSample("Display Medium", DisplayMedium, "85%")
                TypeSample("Display Small", DisplaySmall, "12 Days")
                TypeSample("Headline Large", HeadlineLarge, "Budget Shield")
                TypeSample("Headline Medium", HeadlineMedium, "Safe Now")
                TypeSample("Headline Small", HeadlineSmall, "Daily Actions")
                TypeSample("Title Large", TitleLarge, "January 2026")
                TypeSample("Title Medium", TitleMedium, "View All")
                TypeSample("Title Small", TitleSmall, "Safe to spend right now")
                TypeSample("Body Large", BodyLarge, "Transaction Name")
                TypeSample("Body Medium", BodyMedium, "Jan 15, 2026")
                TypeSample("Body Small", BodySmall, "Label text")
            }
        }
    }
}

@Composable
private fun TypeSample(name: String, style: androidx.compose.ui.text.TextStyle, sample: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = name,
            style = BodyMedium,
            color = TextMuted,
            modifier = Modifier.width(140.dp)
        )
        Text(
            text = sample,
            style = style,
            color = TextPrimary
        )
    }
}

@Preview(
    name = "Cards",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun CardsPreview() {
    BudgetShieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Card Components",
                    style = HeadlineLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Hero Card
                HeroCardPreview()
                Spacer(modifier = Modifier.height(16.dp))

                // Stat Card
                StatCardPreview()
                Spacer(modifier = Modifier.height(16.dp))

                // Action Card
                ActionCardPreview()
            }
        }
    }
}

@Composable
private fun HeroCardPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeXXLarge,
        colors = CardDefaults.cardColors(containerColor = CardHeroBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardPaddingLarge)
        ) {
            // Decorative icon
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(IconContainerHero)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.2f),
                                CyanAccent.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗡️", fontSize = IconSizes.hero)
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🛡️", fontSize = 14.sp)
                    Text(
                        text = "Safe Now",
                        color = CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$2,450.00",
                    color = TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Safe to spend right now",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StatCardPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("🔥", "12", "Day Streak", GoldAccent, Modifier.weight(1f))
        StatCard("⚔️", "85%", "Shield Power", CyanAccent, Modifier.weight(1f))
        StatCard("🛡️", "$1.2k", "Shielded", GreenAccent, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(icon: String, value: String, label: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = IconSizes.card)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ActionCardPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(modifier = Modifier.padding(CardPadding)) {
            Text(
                text = "Daily Actions",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButtonPreview("💰", "Add Income")
                ActionButtonPreview("💳", "Pay Bill")
                ActionButtonPreview("💎", "Save Money")
            }
        }
    }
}

@Composable
private fun ActionButtonPreview(icon: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(IconContainerLarge)
                .clip(CircleShape)
                .background(CyanAccent15)
        ) {
            Text(text = icon, fontSize = IconSizes.action)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Preview(
    name = "Full Design System",
    showBackground = true,
    backgroundColor = 0xFF02070D,
    device = "id:pixel_5"
)
@Composable
fun DesignSystemFullPreview() {
    BudgetShieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.screenHorizontal)
                        .padding(top = Spacing.screenTop),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(IconContainerSmall)
                                .clip(CircleShape)
                                .background(CyanAccent20),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = IconSizes.header)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row {
                            Text(
                                text = "Budget ",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Shield",
                                color = CyanAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    TextButton(onClick = {}) {
                        Text(text = "☰", color = TextPrimary, fontSize = 20.sp)
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal)
                        .padding(vertical = Spacing.large),
                    verticalArrangement = Arrangement.spacedBy(Spacing.large)
                ) {
                    HeroCardPreview()
                    StatCardPreview()
                    ActionCardPreview()
                }
            }
        }
    }
}
