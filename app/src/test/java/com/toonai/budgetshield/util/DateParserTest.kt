package com.toonai.budgetshield.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for strict date parsing with calendar validation.
 */
class DateParserTest {

    // Valid dates

    @Test
    fun `parse ISO date YYYY-MM-DD`() {
        val result = DateParser.parseToIsoDate("2026-07-18")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals("2026-07-18", result.getOrNull())
    }

    @Test
    fun `parse US date MM-DD-YYYY`() {
        val result = DateParser.parseToIsoDate("07/18/2026")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals("2026-07-18", result.getOrNull())
    }

    @Test
    fun `parse US date single digit M-D-YYYY`() {
        val result = DateParser.parseToIsoDate("7/8/2026")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals("2026-07-08", result.getOrNull())
    }

    @Test
    fun `parse dash date MM-DD-YYYY`() {
        val result = DateParser.parseToIsoDate("12-25-2026")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals("2026-12-25", result.getOrNull())
    }

    // Leap year tests

    @Test
    fun `leap year Feb 29 valid`() {
        val result = DateParser.parseToIsoDate("02/29/2024") // 2024 is a leap year
        assertTrue("Should succeed", result.isSuccess)
        assertEquals("2024-02-29", result.getOrNull())
    }

    @Test
    fun `non-leap year Feb 29 invalid`() {
        val result = DateParser.parseToIsoDate("02/29/2025") // 2025 is NOT a leap year
        assertTrue("Should fail", result.isFailure)
    }

    // Invalid dates

    @Test
    fun `reject Feb 30`() {
        val result = DateParser.parseToIsoDate("02/30/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject Sept 31`() {
        val result = DateParser.parseToIsoDate("09/31/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject month 13`() {
        val result = DateParser.parseToIsoDate("13/01/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject month 0`() {
        val result = DateParser.parseToIsoDate("00/01/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject day 32`() {
        val result = DateParser.parseToIsoDate("01/32/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject day 0`() {
        val result = DateParser.parseToIsoDate("01/00/2026")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject invalid ISO 2026-99-99`() {
        val result = DateParser.parseToIsoDate("2026-99-99")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject empty date`() {
        val result = DateParser.parseToIsoDate("")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject malformed date`() {
        val result = DateParser.parseToIsoDate("abc")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject year out of range`() {
        val result = DateParser.parseToIsoDate("01/01/1899")
        assertTrue("Should fail", result.isFailure)
    }

    // Helper tests

    @Test
    fun `leap year detection`() {
        assertTrue("2024 is leap", DateParser.isLeapYear(2024))
        assertFalse("2025 is not leap", DateParser.isLeapYear(2025))
        assertTrue("2000 is leap", DateParser.isLeapYear(2000))
        assertFalse("1900 is not leap", DateParser.isLeapYear(1900))
    }

    @Test
    fun `days in month`() {
        assertEquals(31, DateParser.daysInMonth(2026, 1))  // Jan
        assertEquals(28, DateParser.daysInMonth(2025, 2))  // Feb non-leap
        assertEquals(29, DateParser.daysInMonth(2024, 2)) // Feb leap
        assertEquals(31, DateParser.daysInMonth(2026, 3))  // Mar
        assertEquals(30, DateParser.daysInMonth(2026, 4))  // Apr
        assertEquals(31, DateParser.daysInMonth(2026, 5))  // May
        assertEquals(30, DateParser.daysInMonth(2026, 6))  // Jun
        assertEquals(31, DateParser.daysInMonth(2026, 7))  // Jul
        assertEquals(31, DateParser.daysInMonth(2026, 8))  // Aug
        assertEquals(30, DateParser.daysInMonth(2026, 9))  // Sep
        assertEquals(31, DateParser.daysInMonth(2026, 10)) // Oct
        assertEquals(30, DateParser.daysInMonth(2026, 11)) // Nov
        assertEquals(31, DateParser.daysInMonth(2026, 12)) // Dec
    }

    @Test
    fun `edge case end of month`() {
        // Test various valid end-of-month dates
        assertTrue("Jan 31", DateParser.parseToIsoDate("01/31/2026").isSuccess)
        assertTrue("Mar 31", DateParser.parseToIsoDate("03/31/2026").isSuccess)
        assertTrue("Apr 30", DateParser.parseToIsoDate("04/30/2026").isSuccess)
        assertFalse("Apr 31 invalid", DateParser.parseToIsoDate("04/31/2026").isSuccess)
        assertTrue("Dec 31", DateParser.parseToIsoDate("12/31/2026").isSuccess)
    }
}
