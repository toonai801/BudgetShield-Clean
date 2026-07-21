package com.toonai.budgetshield.di

import com.toonai.budgetshield.data.calculation.SafeNowCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing calculation engine dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object CalculationModule {

    @Provides
    @Singleton
    fun provideSafeNowCalculator(): SafeNowCalculator {
        return SafeNowCalculator()
    }
}
