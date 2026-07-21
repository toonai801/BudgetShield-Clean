package com.toonai.budgetshield.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for calculation engine.
 * SafeNowCalculator is a Kotlin object (singleton) and does not need DI provision.
 */
@Module
@InstallIn(SingletonComponent::class)
object CalculationModule {
    // SafeNowCalculator is a Kotlin object, accessed directly via SafeNowCalculator.calculate()
}
