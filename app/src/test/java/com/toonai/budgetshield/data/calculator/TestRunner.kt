package com.toonai.budgetshield.data.calculator

fun main() {
    val test = SafeNowCalculationRecalculationTest()
    try {
        test.`adding new protected bill reduces safe now when income insufficient`()
        println("TEST 1: PASSED")
    } catch (e: AssertionError) {
        println("TEST 1: FAILED - ${e.message}")
    }
}
