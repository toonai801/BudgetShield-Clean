package com.toonai.budgetshield.di

import android.content.Context
import com.toonai.budgetshield.data.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database and DAO dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BudgetShieldDatabase {
        return BudgetShieldDatabase.getDatabase(context)
    }

    @Provides
    fun provideBillDao(database: BudgetShieldDatabase): BillDao {
        return database.billDao()
    }

    @Provides
    fun provideUserSettingsDao(database: BudgetShieldDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }

    @Provides
    fun provideAccountDao(database: BudgetShieldDatabase): AccountDao {
        return database.accountDao()
    }

    @Provides
    fun provideIncomeScheduleDao(database: BudgetShieldDatabase): IncomeScheduleDao {
        return database.incomeScheduleDao()
    }

    @Provides
    fun provideSavingsBalanceDao(database: BudgetShieldDatabase): SavingsBalanceDao {
        return database.savingsBalanceDao()
    }

    @Provides
    fun provideBudgetCategoryDao(database: BudgetShieldDatabase): BudgetCategoryDao {
        return database.budgetCategoryDao()
    }

    @Provides
    fun provideSetupDraftDao(database: BudgetShieldDatabase): SetupDraftDao {
        return database.setupDraftDao()
    }
}
