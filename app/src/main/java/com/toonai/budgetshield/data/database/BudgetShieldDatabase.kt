package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.toonai.budgetshield.data.model.Achievement
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.model.SetupDraft
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.model.UserStreak
import com.toonai.budgetshield.data.model.XpEntry

/**
 * Room database for BudgetShield app.
 * Version 4 adds: Transaction, XP, Achievement, SavingsGoal, Streak tables
 * Migration preserves all existing data.
 */
@Database(
    entities = [
        Bill::class,
        UserSettings::class,
        IncomeSchedule::class,
        BudgetCategory::class,
        SetupDraft::class,
        Transaction::class,
        XpEntry::class,
        Achievement::class,
        SavingsGoal::class,
        UserStreak::class
    ],
    version = 4,
    exportSchema = false
)
abstract class BudgetShieldDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun incomeScheduleDao(): IncomeScheduleDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun setupDraftDao(): SetupDraftDao
    abstract fun transactionDao(): TransactionDao
    abstract fun xpEntryDao(): XpEntryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun userStreakDao(): UserStreakDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetShieldDatabase? = null

        /**
         * Migration from version 1 to 2.
         * Adds setup/profile tables while preserving existing bills.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // UserSettings - first run completion
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_settings (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        isFirstRunComplete INTEGER NOT NULL DEFAULT 0,
                        currency TEXT NOT NULL DEFAULT 'USD',
                        timezone TEXT NOT NULL DEFAULT 'America/Phoenix',
                        notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                        dailyReminderTime TEXT,
                        billReminderDaysBefore INTEGER NOT NULL DEFAULT 3,
                        planningHorizonMonths INTEGER NOT NULL DEFAULT 2,
                        cashOnHandCents INTEGER NOT NULL DEFAULT 0,
                        savingsBalanceCents INTEGER NOT NULL DEFAULT 0,
                        setupChapter INTEGER NOT NULL DEFAULT 0,
                        selectedMonth TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)

                // IncomeSchedule - recurring income
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS income_schedules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        amountCents INTEGER NOT NULL,
                        frequency TEXT NOT NULL,
                        nextPaydayDate TEXT NOT NULL,
                        isConfirmed INTEGER NOT NULL DEFAULT 1,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)

                // BudgetCategory - monthly budgets
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS budget_categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        monthKey TEXT NOT NULL,
                        plannedAmountCents INTEGER NOT NULL,
                        spentAmountCents INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_budget_categories_name_monthKey
                    ON budget_categories(name, monthKey)
                """)
            }
        }

        /**
         * Migration from version 2 to 3.
         * Adds SetupDraft table for process-death resume.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SetupDraft - incomplete setup progress
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS setup_drafts (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        currentChapter INTEGER NOT NULL DEFAULT 1,
                        cashOnHandCents INTEGER NOT NULL DEFAULT 0,
                        incomeName TEXT NOT NULL DEFAULT '',
                        incomeAmountCents INTEGER NOT NULL DEFAULT 0,
                        nextPaydayDate TEXT NOT NULL DEFAULT '',
                        frequency TEXT NOT NULL DEFAULT '',
                        isIncomeConfirmed INTEGER NOT NULL DEFAULT 0,
                        savingsBalanceCents INTEGER NOT NULL DEFAULT 0,
                        foodBudgetCents INTEGER NOT NULL DEFAULT 0,
                        wantsBudgetCents INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL
                    )
                """)
            }
        }

        /**
         * Migration from version 3 to 4.
         * Adds Transaction, XP, Achievement, SavingsGoal, and Streak tables.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Transactions
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        amountCents INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        icon TEXT NOT NULL DEFAULT '💰',
                        relatedBillId INTEGER,
                        relatedIncomeId INTEGER,
                        earnsXp INTEGER NOT NULL DEFAULT 1,
                        xpEarned INTEGER NOT NULL DEFAULT 0,
                        isProtected INTEGER NOT NULL DEFAULT 0,
                        transactionDate TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(transactionDate)
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_transactions_category ON transactions(category)
                """)

                // XP Entries
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS xp_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount INTEGER NOT NULL,
                        activityType TEXT NOT NULL,
                        description TEXT NOT NULL,
                        relatedId INTEGER,
                        entryDate TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_xp_entries_date ON xp_entries(entryDate)
                """)

                // Achievements
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS achievements (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        xpReward INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        isUnlocked INTEGER NOT NULL DEFAULT 0,
                        unlockedAt INTEGER,
                        progress INTEGER NOT NULL DEFAULT 0,
                        targetValue INTEGER NOT NULL DEFAULT 1
                    )
                """)

                // Savings Goals
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS savings_goals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        icon TEXT NOT NULL DEFAULT '🎯',
                        targetAmountCents INTEGER NOT NULL,
                        currentAmountCents INTEGER NOT NULL DEFAULT 0,
                        deadlineDate TEXT,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER,
                        priority INTEGER NOT NULL DEFAULT 1,
                        isEmergencyFund INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL
                    )
                """)

                // User Streaks
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_streaks (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        currentStreak INTEGER NOT NULL DEFAULT 0,
                        bestStreak INTEGER NOT NULL DEFAULT 0,
                        lastActivityDate TEXT,
                        isActiveToday INTEGER NOT NULL DEFAULT 0,
                        totalActiveDays INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL
                    )
                """)
            }
        }

        fun getDatabase(context: Context): BudgetShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetShieldDatabase::class.java,
                    "budget_shield_db_v4"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
