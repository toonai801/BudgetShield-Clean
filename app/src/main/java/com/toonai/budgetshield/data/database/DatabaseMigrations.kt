package com.toonai.budgetshield.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations.
 * All migrations must preserve existing user data.
 */

/**
 * Migration from version 1 to 2:
 * - Adds UserSettings table
 * - Adds IncomeSchedule table
 * - Adds BudgetCategory table
 * - Preserves all existing Bill data
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create UserSettings table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_settings (
                id INTEGER PRIMARY KEY NOT NULL,
                isFirstRunComplete INTEGER NOT NULL DEFAULT 0,
                currency TEXT NOT NULL DEFAULT 'USD',
                timezone TEXT NOT NULL DEFAULT 'America/Phoenix',
                cashOnHandCents INTEGER NOT NULL DEFAULT 0,
                savingsBalanceCents INTEGER NOT NULL DEFAULT 0,
                selectedMonth TEXT NOT NULL DEFAULT '',
                setupChapter INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """)

        // Insert default user settings row
        database.execSQL("""
            INSERT INTO user_settings (id, isFirstRunComplete, currency, timezone, 
                cashOnHandCents, savingsBalanceCents, selectedMonth, setupChapter,
                createdAt, updatedAt)
            VALUES (1, 0, 'USD', 'America/Phoenix', 0, 0, '', 0, 
                ${System.currentTimeMillis()}, ${System.currentTimeMillis()})
        """)

        // Create IncomeSchedule table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS income_schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                amountCents INTEGER NOT NULL,
                nextPaydayDate TEXT NOT NULL,
                frequency TEXT NOT NULL,
                isConfirmed INTEGER NOT NULL DEFAULT 0,
                isActive INTEGER NOT NULL DEFAULT 1,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """)

        // Create index on income_schedules for active schedules
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_income_schedules_active 
            ON income_schedules(isActive)
        """)

        // Create BudgetCategory table
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

        // Create unique index on budget_categories for name + monthKey
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_budget_categories_name_month 
            ON budget_categories(name, monthKey)
        """)

        // Note: The existing 'bills' table from version 1 is preserved
        // No modifications to bills table - data remains intact
    }
}
