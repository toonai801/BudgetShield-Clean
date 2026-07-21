package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings

/**
 * Room database for BudgetShield app.
 * Version 2 adds: UserSettings, IncomeSchedule, BudgetCategory
 * Migration preserves all existing bills.
 */
@Database(
    entities = [
        Bill::class,
        UserSettings::class,
        IncomeSchedule::class,
        BudgetCategory::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BudgetShieldDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun incomeScheduleDao(): IncomeScheduleDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: BudgetShieldDatabase? = null

        /**
         * Migration from version 1 to 2.
         * Adds setup/profile tables while preserving existing bills.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
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

        fun getDatabase(context: Context): BudgetShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetShieldDatabase::class.java,
                    "budget_shield_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
