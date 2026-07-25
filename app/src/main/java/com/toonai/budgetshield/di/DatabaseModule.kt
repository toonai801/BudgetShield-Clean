package com.toonai.budgetshield.di

import android.content.Context
import com.toonai.budgetshield.data.database.*
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database, DAO, and repository dependencies.
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
    fun provideIncomeScheduleDao(database: BudgetShieldDatabase): IncomeScheduleDao {
        return database.incomeScheduleDao()
    }

    @Provides
    fun provideBudgetCategoryDao(database: BudgetShieldDatabase): BudgetCategoryDao {
        return database.budgetCategoryDao()
    }

    @Provides
    fun provideSetupDraftDao(database: BudgetShieldDatabase): SetupDraftDao {
        return database.setupDraftDao()
    }

    @Provides
    fun provideTransactionDao(database: BudgetShieldDatabase): TransactionDao {
        return database.transactionDao()
    }

    // Repository providers
    @Provides
    @Singleton
    fun provideBillRepository(billDao: BillDao): BillRepository {
        return BillRepository(billDao)
    }

    @Provides
    @Singleton
    fun provideUserSettingsRepository(userSettingsDao: UserSettingsDao): UserSettingsRepository {
        return UserSettingsRepository(userSettingsDao)
    }

    @Provides
    @Singleton
    fun provideIncomeRepository(incomeScheduleDao: IncomeScheduleDao): IncomeRepository {
        return IncomeRepository(incomeScheduleDao)
    }

    @Provides
    @Singleton
    fun provideBudgetRepository(budgetCategoryDao: BudgetCategoryDao): BudgetRepository {
        return BudgetRepository(budgetCategoryDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepository(transactionDao)
    }
}
