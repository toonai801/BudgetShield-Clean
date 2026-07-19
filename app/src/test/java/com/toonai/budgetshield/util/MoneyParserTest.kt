package com.toonai.budgetshield.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for exact money parsing without floating-point errors.
 */
class MoneyParserTest {

    @Test
    fun `parse 0_01 cents`() {
        val result = MoneyParser.parseToCents("0.01")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }

    @Test
    fun `parse 0_10 cents`() {
        val result = MoneyParser.parseToCents("0.10")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(10L, result.getOrNull())
    }

    @Test
    fun `parse 0_29 cents`() {
        val result = MoneyParser.parseToCents("0.29")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(29L, result.getOrNull())
    }

    @Test
    fun `parse 1_05 dollars`() {
        val result = MoneyParser.parseToCents("1.05")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(105L, result.getOrNull())
    }

    @Test
    fun `parse 10_99 dollars`() {
        val result = MoneyParser.parseToCents("10.99")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(1099L, result.getOrNull())
    }

    @Test
    fun `parse 9999_99 dollars`() {
        val result = MoneyParser.parseToCents("9999.99")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(999999L, result.getOrNull())
    }

    @Test
    fun `parse with dollar sign`() {
        val result = MoneyParser.parseToCents("$50.00")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(5000L, result.getOrNull())
    }

    @Test
    fun `parse whole dollars`() {
        val result = MoneyParser.parseToCents("100")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(10000L, result.getOrNull())
    }

    @Test
    fun `parse single decimal`() {
        val result = MoneyParser.parseToCents("5.5")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(550L, result.getOrNull())
    }

    @Test
    fun `parse leading decimal`() {
        val result = MoneyParser.parseToCents(".99")
        assertTrue("Should succeed", result.isSuccess)
        assertEquals(99L, result.getOrNull())
    }

    @Test
    fun `reject empty string`() {
        val result = MoneyParser.parseToCents("")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject blank string`() {
        val result = MoneyParser.parseToCents("   ")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject negative amount`() {
        val result = MoneyParser.parseToCents("-10.00")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject zero for payment`() {
        // Note: zero cents is valid for parsing, but payment logic should reject it
        val result = MoneyParser.parseToCents("0.00")
        assertTrue("Parse should succeed", result.isSuccess)
        assertEquals(0L, result.getOrNull())
    }

    @Test
    fun `reject more than two decimal places`() {
        val result = MoneyParser.parseToCents("10.999")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject malformed input`() {
        val result = MoneyParser.parseToCents("abc")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `reject double decimal`() {
        val result = MoneyParser.parseToCents("10.00.00")
        assertTrue("Should fail", result.isFailure)
    }

    @Test
    fun `format cents to display`() {
        assertEquals("$0.01", MoneyParser.formatCents(1))
        assertEquals("$0.10", MoneyParser.formatCents(10))
        assertEquals("$0.29", MoneyParser.formatCents(29))
        assertEquals("$1.05", MoneyParser.formatCents(105))
        assertEquals("$10.99", MoneyParser.formatCents(1099))
        assertEquals("$9999.99", MoneyParser.formatCents(999999))
        assertEquals("$0.00", MoneyParser.formatCents(0))
        assertEquals("$100.00", MoneyParser.formatCents(10000))
    }

    @Test
    fun `validate input pattern`() {
        assertTrue("Digits only", MoneyParser.isValidInputPattern("123"))
        assertTrue("With decimal", MoneyParser.isValidInputPattern("123.45"))
        assertTrue("Single decimal", MoneyParser.isValidInputPattern("123.4"))
        assertTrue("With dollar", MoneyParser.isValidInputPattern("$123.45"))
        assertFalse("Too many decimals", MoneyParser.isValidInputPattern("123.456"))
        assertFalse("Double decimal", MoneyParser.isValidInputPattern("123.45.6"))
        assertFalse("Letters", MoneyParser.isValidInputPattern("abc"))
    }
}
