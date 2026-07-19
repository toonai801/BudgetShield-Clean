package com.toonai.budgetshield.util

/**
 * Exact money parsing for decimal currency input to Long cents.
 * No floating-point conversion. Exact integer arithmetic only.
 */
object MoneyParser {

    /**
     * Parse a decimal currency string to cents.
     * Accepts: "$950.00", "950.00", "950", ".50", "0.29"
     * Rejects: negative, overflow, more than 2 decimal places, malformed
     *
     * @param input User-entered amount string
     * @return Result.success(cents) or Result.failure(IllegalArgumentException)
     */
    fun parseToCents(input: String): Result<Long> {
        val trimmed = input.trim()

        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Amount is required"))
        }

        // Remove optional leading $
        val withoutDollar = if (trimmed.startsWith("$")) trimmed.substring(1) else trimmed

        // Validate characters: digits, optional single decimal point
        if (!withoutDollar.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            // Check if it has more than 2 decimal places
            if (withoutDollar.contains(".") && withoutDollar.substringAfter(".").length > 2) {
                return Result.failure(IllegalArgumentException("Amount cannot have more than 2 decimal places"))
            }
            return Result.failure(IllegalArgumentException("Invalid amount format"))
        }

        return parseExactCents(withoutDollar)
    }

    /**
     * Internal: parse validated string to cents using exact integer arithmetic.
     */
    private fun parseExactCents(amountStr: String): Result<Long> {
        return try {
            val parts = amountStr.split(".")
            val dollarsPart = parts[0].ifEmpty { "0" }

            // Parse dollars
            val dollars = dollarsPart.toLongOrNull()
                ?: return Result.failure(IllegalArgumentException("Invalid dollar amount"))

            if (dollars < 0) {
                return Result.failure(IllegalArgumentException("Amount cannot be negative"))
            }

            // Parse cents (0-99)
            val centsPart = if (parts.size > 1) {
                val rawCents = parts[1]
                when (rawCents.length) {
                    0 -> 0L
                    1 -> rawCents.toLong() * 10  // "5" -> 50 cents
                    2 -> rawCents.toLong()       // "50" -> 50 cents
                    else -> return Result.failure(IllegalArgumentException("Too many decimal places"))
                }
            } else {
                0L
            }

            // Check for overflow before combining
            if (dollars > Long.MAX_VALUE / 100) {
                return Result.failure(IllegalArgumentException("Amount too large"))
            }

            val totalCents = dollars * 100 + centsPart

            if (totalCents < 0) {
                return Result.failure(IllegalArgumentException("Amount overflow"))
            }

            Result.success(totalCents)
        } catch (e: NumberFormatException) {
            Result.failure(IllegalArgumentException("Invalid number format"))
        }
    }

    /**
     * Format cents to display string: "$X.YY"
     */
    fun formatCents(cents: Long): String {
        val dollars = cents / 100
        val remainder = kotlin.math.abs(cents % 100)
        return String.format("$%d.%02d", dollars, remainder)
    }

    /**
     * Validate that input only contains allowed characters as user types.
     * Allows: digits, at most one decimal point, at most 2 digits after decimal.
     */
    fun isValidInputPattern(input: String): Boolean {
        val withoutDollar = if (input.startsWith("$")) input.substring(1) else input
        return withoutDollar.matches(Regex("^\\d*\\.?\\d{0,2}$"))
    }
}
