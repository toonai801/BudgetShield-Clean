package com.toonai.budgetshield.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

/**
 * Strict date parsing with real calendar validation.
 * Rejects impossible dates (Feb 30, Sept 31, etc.).
 * Normalizes to ISO YYYY-MM-DD format.
 */
object DateParser {

    private val ISO_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
        .withResolverStyle(ResolverStyle.STRICT)

    /**
     * Parse user-entered date string to ISO YYYY-MM-DD format.
     *
     * Supported input formats:
     * - YYYY-MM-DD (ISO, accepted directly)
     * - MM/DD/YYYY (US format)
     * - M/D/YYYY (single digit month/day)
     * - M-D-YYYY (dash separator)
     *
     * Rejects:
     * - Invalid dates (Feb 30, Sept 31, etc.)
     * - Invalid months (>12)
     * - Invalid days for month
     * - Malformed input
     *
     * @param input User-entered date string
     * @return Result.success(YYYY-MM-DD) or Result.failure(IllegalArgumentException)
     */
    fun parseToIsoDate(input: String): Result<String> {
        val trimmed = input.trim()

        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Date is required"))
        }

        return when {
            // Already ISO format: YYYY-MM-DD
            trimmed.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> {
                parseIsoDate(trimmed)
            }
            // US format: MM/DD/YYYY or M/D/YYYY
            trimmed.matches(Regex("^\\d{1,2}/\\d{1,2}/\\d{4}$")) -> {
                parseUsDate(trimmed, "/")
            }
            // Dash format: M-D-YYYY or MM-DD-YYYY
            trimmed.matches(Regex("^\\d{1,2}-\\d{1,2}-\\d{4}$")) -> {
                parseUsDate(trimmed, "-")
            }
            else -> {
                Result.failure(IllegalArgumentException("Invalid date format. Use MM/DD/YYYY or YYYY-MM-DD"))
            }
        }
    }

    /**
     * Parse strict ISO date (YYYY-MM-DD) with calendar validation.
     */
    private fun parseIsoDate(isoDate: String): Result<String> {
        return try {
            // STRICT mode validates real calendar dates
            val date = LocalDate.parse(isoDate, ISO_FORMATTER)
            Result.success(date.format(ISO_FORMATTER))
        } catch (e: DateTimeParseException) {
            Result.failure(IllegalArgumentException("Invalid date: ${e.message}"))
        }
    }

    /**
     * Parse US format date (MM/DD/YYYY or M-D-YYYY) with calendar validation.
     */
    private fun parseUsDate(usDate: String, separator: String): Result<String> {
        return try {
            val parts = usDate.split(separator)
            if (parts.size != 3) {
                return Result.failure(IllegalArgumentException("Invalid date format"))
            }

            val month = parts[0].toInt()
            val day = parts[1].toInt()
            val year = parts[2].toInt()

            // Validate ranges
            if (month < 1 || month > 12) {
                return Result.failure(IllegalArgumentException("Month must be between 1 and 12"))
            }

            if (day < 1 || day > 31) {
                return Result.failure(IllegalArgumentException("Day must be between 1 and 31"))
            }

            if (year < 1900 || year > 2100) {
                return Result.failure(IllegalArgumentException("Year must be between 1900 and 2100"))
            }

            // Use LocalDate for strict validation (rejects Feb 30, etc.)
            val date = LocalDate.of(year, month, day)
            Result.success(date.format(ISO_FORMATTER))
        } catch (e: java.time.DateTimeException) {
            Result.failure(IllegalArgumentException("Invalid date for calendar: ${e.message}"))
        } catch (e: NumberFormatException) {
            Result.failure(IllegalArgumentException("Invalid date numbers"))
        }
    }

    /**
     * Check if a year is a leap year.
     */
    fun isLeapYear(year: Int): Boolean {
        return java.time.Year.of(year).isLeap
    }

    /**
     * Get days in month for a given year and month.
     */
    fun daysInMonth(year: Int, month: Int): Int {
        return java.time.YearMonth.of(year, month).lengthOfMonth()
    }

    /**
     * Get current month as key (YYYY-MM format).
     */
    fun currentMonthKey(): String {
        return java.time.YearMonth.now().toString()
    }

    /**
     * Parse ISO date string to LocalDate.
     * Returns null if parsing fails.
     */
    fun parseDate(isoDate: String?): java.time.LocalDate? {
        if (isoDate.isNullOrEmpty()) return null
        return try {
            java.time.LocalDate.parse(isoDate, ISO_FORMATTER)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format LocalDate to ISO date string.
     */
    fun formatDate(date: java.time.LocalDate): String {
        return date.format(ISO_FORMATTER)
    }

    /**
     * Format YearMonth to display string (e.g., "July 2025").
     */
    fun formatMonthYear(yearMonth: java.time.YearMonth): String {
        return yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    /**
     * Get today's date as ISO string (YYYY-MM-DD).
     */
    fun today(): String {
        return java.time.LocalDate.now().toString()
    }

    /**
     * Format an ISO date string to a short display format (e.g., "Jul 6").
     */
    fun formatShortDate(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return ""
        return try {
            val date = java.time.LocalDate.parse(isoDate, ISO_FORMATTER)
            date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d"))
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Get last day of month for a month key (YYYY-MM).
     * Returns "YYYY-MM-DD" format.
     */
    fun getLastDayOfMonth(monthKey: String): String {
        val parts = monthKey.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth()
        return "$monthKey-$daysInMonth"
    }
}
