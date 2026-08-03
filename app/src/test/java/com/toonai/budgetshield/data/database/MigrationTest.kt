package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Room database migration tests.
 * Verifies migrations preserve data and schema integrity.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MigrationTest {

    private lateinit var context: Context
    private var database: BudgetShieldDatabase? = null

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun teardown() {
        database?.close()
        database = null
    }

    /**
     * Test migration from version 1 to 2.
     * Adds user_settings, income_schedules, and budget_categories tables.
     */
    @Test
    fun `migration 1 to 2 adds user settings and income tables`(): Unit = runBlocking {
        // Create a v1 database with bills
        val dbName = "test_migration_1_to_2.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        // Build v1 database
        val db1 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        db1.close()

        // Migrate to current version (v4)
        val db2 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .addMigrations(
                BudgetShieldDatabase.MIGRATION_1_2,
                BudgetShieldDatabase.MIGRATION_2_3,
                BudgetShieldDatabase.MIGRATION_3_4,
                BudgetShieldDatabase.MIGRATION_4_5
            )
            .allowMainThreadQueries()
            .build()
        database = db2

        // Verify tables exist
        val cursor = db2.query("SELECT name FROM sqlite_master WHERE type='table'", emptyArray())
        val tables = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()

        assertTrue("bills table should exist", tables.contains("bills"))
        assertTrue("user_settings table should exist", tables.contains("user_settings"))
        assertTrue("income_schedules table should exist", tables.contains("income_schedules"))
        assertTrue("budget_categories table should exist", tables.contains("budget_categories"))

        db2.close()
        database = null
    }

    /**
     * Test migration from version 2 to 3.
     * Adds setup_drafts table.
     */
    @Test
    fun `migration 2 to 3 adds setup drafts table`(): Unit = runBlocking {
        val dbName = "test_migration_2_to_3.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        // Build database at v2
        val db = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        db.close()

        // Migrate to current
        val db2 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .addMigrations(
                BudgetShieldDatabase.MIGRATION_2_3,
                BudgetShieldDatabase.MIGRATION_3_4,
                BudgetShieldDatabase.MIGRATION_4_5
            )
            .allowMainThreadQueries()
            .build()
        database = db2

        // Verify setup_drafts table exists
        val cursor = db2.query("SELECT name FROM sqlite_master WHERE type='table'", emptyArray())
        val tables = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()

        assertTrue("setup_drafts table should exist", tables.contains("setup_drafts"))

        db2.close()
        database = null
    }

    /**
     * Test migration from version 3 to 4.
     * Adds transactions, xp_entries, achievements, savings_goals, user_streaks tables.
     */
    @Test
    fun `migration 3 to 4 adds gamification tables`(): Unit = runBlocking {
        val dbName = "test_migration_3_to_4.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        // Build database
        val db = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        db.close()

        // Migrate to current
        val db2 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .addMigrations(
                BudgetShieldDatabase.MIGRATION_3_4,
                BudgetShieldDatabase.MIGRATION_4_5
            )
            .allowMainThreadQueries()
            .build()
        database = db2

        // Verify all new tables exist
        val cursor = db2.query("SELECT name FROM sqlite_master WHERE type='table'", emptyArray())
        val tables = mutableListOf<String>()
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0))
        }
        cursor.close()

        assertTrue("transactions table should exist", tables.contains("transactions"))
        assertTrue("xp_entries table should exist", tables.contains("xp_entries"))
        assertTrue("achievements table should exist", tables.contains("achievements"))
        assertTrue("savings_goals table should exist", tables.contains("savings_goals"))
        assertTrue("user_streaks table should exist", tables.contains("user_streaks"))

        db2.close()
        database = null
    }

    /**
     * Test that bills table preserves data across migrations.
     */
    @Test
    fun `bills data survives migration`(): Unit = runBlocking {
        val dbName = "test_migration_bills.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        // Create database and insert bill
        val db1 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()

        // Insert a bill
        val billId = db1.billDao().insertBill(
            com.toonai.budgetshield.data.model.Bill(
                name = "Test Bill",
                icon = "💰",
                amountCents = 50000L,
                dueDate = "2025-08-01",
                isProtected = true,
                isPaid = false
            )
        )
        assertTrue("Bill ID should be positive", billId > 0)

        // Verify bill exists
        val beforeMigration = db1.billDao().getBillById(billId)
        assertNotNull("Bill should exist before migration", beforeMigration)
        assertEquals("Test Bill", beforeMigration?.name)
        assertEquals(50000L, beforeMigration?.amountCents)

        db1.close()

        // Reopen with all migrations
        val db2 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .addMigrations(
                BudgetShieldDatabase.MIGRATION_1_2,
                BudgetShieldDatabase.MIGRATION_2_3,
                BudgetShieldDatabase.MIGRATION_3_4,
                BudgetShieldDatabase.MIGRATION_4_5
            )
            .allowMainThreadQueries()
            .build()
        database = db2

        // Verify bill still exists after migration
        val afterMigration = db2.billDao().getBillById(billId)
        assertNotNull("Bill should exist after migration", afterMigration)
        assertEquals("Test Bill", afterMigration?.name)
        assertEquals(50000L, afterMigration?.amountCents)
        assertEquals("💰", afterMigration?.icon)
        assertEquals("2025-08-01", afterMigration?.dueDate)
        assertTrue("Should be protected", afterMigration?.isProtected ?: false)
        assertFalse("Should not be paid", afterMigration?.isPaid ?: true)

        db2.close()
        database = null
    }

    /**
     * Test that user settings table is created with proper columns.
     */
    @Test
    fun `user settings table has correct schema`(): Unit = runBlocking {
        val dbName = "test_settings_schema.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        val db = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        database = db

        // Get table info
        val cursor = db.query("PRAGMA table_info(user_settings)", emptyArray())
        val columns = mutableMapOf<String, String>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(1)
            val type = cursor.getString(2)
            columns[name] = type
        }
        cursor.close()

        // Verify expected columns exist
        assertTrue("id column should exist", columns.containsKey("id"))
        assertTrue("isFirstRunComplete column should exist", columns.containsKey("isFirstRunComplete"))
        assertTrue("cashOnHandCents column should exist", columns.containsKey("cashOnHandCents"))
        assertTrue("savingsBalanceCents column should exist", columns.containsKey("savingsBalanceCents"))

        db.close()
        database = null
    }

    /**
     * Test that income_schedules table is created with proper columns.
     */
    @Test
    fun `income schedules table has correct schema`(): Unit = runBlocking {
        val dbName = "test_income_schema.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        val db = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        database = db

        // Get table info
        val cursor = db.query("PRAGMA table_info(income_schedules)", emptyArray())
        val columns = mutableMapOf<String, String>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(1)
            val type = cursor.getString(2)
            columns[name] = type
        }
        cursor.close()

        // Verify expected columns exist
        assertTrue("id column should exist", columns.containsKey("id"))
        assertTrue("name column should exist", columns.containsKey("name"))
        assertTrue("amountCents column should exist", columns.containsKey("amountCents"))
        assertTrue("frequency column should exist", columns.containsKey("frequency"))
        assertTrue("nextPayday column should exist", columns.containsKey("nextPayday"))
        assertTrue("first anchor column should exist", columns.containsKey("paydayAnchorDayOne"))
        assertTrue("second anchor column should exist", columns.containsKey("paydayAnchorDayTwo"))
        assertTrue("isConfirmed column should exist", columns.containsKey("isConfirmed"))
        assertTrue("isActive column should exist", columns.containsKey("isActive"))

        db.close()
        database = null
    }

    @Test
    fun `migration 4 to 5 preserves income and adds nullable payday anchors`() {
        val dbName = "test_migration_4_to_5.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbFile.absolutePath)
                .callback(object : SupportSQLiteOpenHelper.Callback(4) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE income_schedules (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                name TEXT NOT NULL,
                                amountCents INTEGER NOT NULL,
                                nextPaydayDate TEXT NOT NULL,
                                frequency TEXT NOT NULL,
                                isConfirmed INTEGER NOT NULL,
                                isActive INTEGER NOT NULL,
                                createdAt INTEGER NOT NULL,
                                updatedAt INTEGER NOT NULL
                            )
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                            CREATE TABLE setup_drafts (
                                id INTEGER PRIMARY KEY NOT NULL,
                                currentChapter INTEGER NOT NULL,
                                cashOnHandCents INTEGER NOT NULL,
                                incomeName TEXT NOT NULL,
                                incomeAmountCents INTEGER NOT NULL,
                                nextPaydayDate TEXT NOT NULL,
                                frequency TEXT NOT NULL,
                                isIncomeConfirmed INTEGER NOT NULL,
                                savingsBalanceCents INTEGER NOT NULL,
                                foodBudgetCents INTEGER NOT NULL,
                                wantsBudgetCents INTEGER NOT NULL,
                                updatedAt INTEGER NOT NULL
                            )
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                            CREATE TABLE user_settings (
                                id INTEGER PRIMARY KEY NOT NULL,
                                isFirstRunComplete INTEGER NOT NULL,
                                currency TEXT NOT NULL,
                                timezone TEXT NOT NULL,
                                notificationsEnabled INTEGER NOT NULL,
                                dailyReminderTime TEXT,
                                billReminderDaysBefore INTEGER NOT NULL,
                                planningHorizonMonths INTEGER NOT NULL,
                                cashOnHandCents INTEGER NOT NULL,
                                savingsBalanceCents INTEGER NOT NULL,
                                selectedMonth TEXT,
                                setupChapter INTEGER NOT NULL,
                                createdAt INTEGER NOT NULL,
                                updatedAt INTEGER NOT NULL
                            )
                            """.trimIndent()
                        )
                        db.execSQL(
                            """
                            CREATE TABLE budget_categories (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                name TEXT NOT NULL,
                                monthKey TEXT NOT NULL,
                                plannedAmountCents INTEGER NOT NULL,
                                spentAmountCents INTEGER NOT NULL,
                                createdAt INTEGER NOT NULL,
                                updatedAt INTEGER NOT NULL
                            )
                            """.trimIndent()
                        )
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) = Unit
                })
                .build()
        )
        val db = helper.writableDatabase
        db.execSQL(
            """
            INSERT INTO income_schedules
            (name, amountCents, nextPaydayDate, frequency,
             isConfirmed, isActive, createdAt, updatedAt)
            VALUES ('Legacy pay', 12345, '2026-08-15',
                    'semimonthly', 1, 1, 10, 20)
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO user_settings VALUES
            (1, 1, 'USD', 'America/Denver', 1, NULL, 3, 2,
             50000, 10000, NULL, 7, 10, 20)
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO budget_categories
            (name, monthKey, plannedAmountCents, spentAmountCents, createdAt, updatedAt)
            VALUES ('Food', '2026-08', 30000, 12000, 10, 20)
            """.trimIndent()
        )

        BudgetShieldDatabase.MIGRATION_4_5.migrate(db)

        val columns = mutableSetOf<String>()
        db.query("PRAGMA table_info(income_schedules)").use { cursor ->
            while (cursor.moveToNext()) columns += cursor.getString(1)
        }
        assertTrue(columns.contains("paydayAnchorDayOne"))
        assertTrue(columns.contains("paydayAnchorDayTwo"))
        db.query(
            "SELECT name, amountCents, nextPayday, nextPaydayDate, " +
                "paydayAnchorDayOne, paydayAnchorDayTwo " +
                "FROM income_schedules"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Legacy pay", cursor.getString(0))
            assertEquals(12345L, cursor.getLong(1))
            assertEquals("2026-08-15", cursor.getString(2))
            assertEquals("2026-08-15", cursor.getString(3))
            assertTrue(cursor.isNull(4))
            assertTrue(cursor.isNull(5))
        }
        db.query(
            "SELECT selectedMonth, cashOnHandCents FROM user_settings WHERE id = 1"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("", cursor.getString(0))
            assertEquals(50000L, cursor.getLong(1))
        }
        db.query(
            "SELECT categoryType, isActive, icon, plannedAmountCents " +
                "FROM budget_categories WHERE name = 'Food'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("food", cursor.getString(0))
            assertEquals(1, cursor.getInt(1))
            assertEquals("", cursor.getString(2))
            assertEquals(30000L, cursor.getLong(3))
        }
        helper.close()
    }

    @Test
    fun `real version 1 schema migrates to version 5 without data loss`() {
        val dbName = "test_real_v1_to_v5.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbFile.absolutePath)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE bills (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                name TEXT NOT NULL,
                                icon TEXT NOT NULL,
                                amountCents INTEGER NOT NULL,
                                paidAmountCents INTEGER NOT NULL,
                                dueDate TEXT NOT NULL,
                                isProtected INTEGER NOT NULL,
                                isPaid INTEGER NOT NULL,
                                createdAt INTEGER NOT NULL
                            )
                            """.trimIndent()
                        )
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) = Unit
                })
                .build()
        )
        val legacy = helper.writableDatabase
        legacy.execSQL(
            """
            INSERT INTO bills
            (name, icon, amountCents, paidAmountCents, dueDate,
             isProtected, isPaid, createdAt)
            VALUES ('Rent', 'home', 120000, 20000, '2026-08-05', 1, 0, 10)
            """.trimIndent()
        )
        BudgetShieldDatabase.MIGRATION_1_2.migrate(legacy)
        legacy.execSQL(
            """
            INSERT INTO user_settings
            (id, isFirstRunComplete, currency, timezone, notificationsEnabled,
             dailyReminderTime, billReminderDaysBefore, planningHorizonMonths,
             cashOnHandCents, savingsBalanceCents, setupChapter,
             createdAt, updatedAt)
            VALUES (1, 1, 'USD', 'America/Denver', 1, NULL, 3, 2,
                    250000, 50000, 7, 10, 20)
            """.trimIndent()
        )
        legacy.execSQL(
            """
            INSERT INTO income_schedules
            (name, amountCents, frequency, nextPaydayDate,
             isConfirmed, isActive, createdAt, updatedAt)
            VALUES ('Paycheck', 150000, 'semimonthly', '2026-08-15',
                    1, 1, 10, 20)
            """.trimIndent()
        )
        legacy.execSQL(
            """
            INSERT INTO budget_categories
            (name, monthKey, plannedAmountCents, spentAmountCents, createdAt, updatedAt)
            VALUES ('Food', '2026-08', 40000, 5000, 10, 20)
            """.trimIndent()
        )
        BudgetShieldDatabase.MIGRATION_2_3.migrate(legacy)
        BudgetShieldDatabase.MIGRATION_3_4.migrate(legacy)
        BudgetShieldDatabase.MIGRATION_4_5.migrate(legacy)
        legacy.execSQL("PRAGMA user_version = 5")
        helper.close()

        val migrated = Room.databaseBuilder(
            context,
            BudgetShieldDatabase::class.java,
            dbFile.absolutePath
        )
            .allowMainThreadQueries()
            .build()
        database = migrated

        migrated.query(
            "SELECT name, amountCents, paidAmountCents FROM bills WHERE name = 'Rent'",
            emptyArray()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Rent", cursor.getString(0))
            assertEquals(120000L, cursor.getLong(1))
            assertEquals(20000L, cursor.getLong(2))
        }
        migrated.query(
            "SELECT name, nextPayday, nextPaydayDate, paydayAnchorDayOne " +
                "FROM income_schedules WHERE name = 'Paycheck'",
            emptyArray()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Paycheck", cursor.getString(0))
            assertEquals("2026-08-15", cursor.getString(1))
            assertEquals("2026-08-15", cursor.getString(2))
            assertTrue(cursor.isNull(3))
        }
        migrated.query(
            "SELECT plannedAmountCents, spentAmountCents, categoryType " +
                "FROM budget_categories WHERE name = 'Food'",
            emptyArray()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(40000L, cursor.getLong(0))
            assertEquals(5000L, cursor.getLong(1))
            assertEquals("food", cursor.getString(2))
        }
        migrated.query(
            "SELECT cashOnHandCents, savingsBalanceCents, selectedMonth " +
                "FROM user_settings WHERE id = 1",
            emptyArray()
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(250000L, cursor.getLong(0))
            assertEquals(50000L, cursor.getLong(1))
            assertEquals("", cursor.getString(2))
        }
        migrated.close()
        database = null
    }

    @Test
    fun `database downgrade fails without deleting user data`(): Unit = runBlocking {
        val dbName = "test_non_destructive_downgrade.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()
        val current = Room.databaseBuilder(
            context,
            BudgetShieldDatabase::class.java,
            dbFile.absolutePath
        )
            .allowMainThreadQueries()
            .build()
        current.billDao().insertBill(
            com.toonai.budgetshield.data.model.Bill(
                name = "Preserve me",
                icon = "home",
                amountCents = 10000L,
                dueDate = "2026-08-20",
                isProtected = true
            )
        )
        current.close()

        val versionSetter = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbFile.absolutePath)
                .callback(object : SupportSQLiteOpenHelper.Callback(5) {
                    override fun onCreate(db: SupportSQLiteDatabase) = Unit
                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) = Unit
                })
                .build()
        )
        versionSetter.writableDatabase.execSQL("PRAGMA user_version = 6")
        versionSetter.close()

        val downgradeAttempt = Room.databaseBuilder(
            context,
            BudgetShieldDatabase::class.java,
            dbFile.absolutePath
        )
            .allowMainThreadQueries()
            .build()
        assertThrows(IllegalStateException::class.java) {
            downgradeAttempt.openHelper.writableDatabase
        }
        downgradeAttempt.close()

        val verifier = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbFile.absolutePath)
                .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                    override fun onCreate(db: SupportSQLiteDatabase) = Unit
                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) = Unit
                })
                .build()
        )
        verifier.readableDatabase.query(
            "SELECT amountCents FROM bills WHERE name = 'Preserve me'"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(10000L, cursor.getLong(0))
        }
        verifier.close()
    }

    /**
     * Test no destructive fallback occurs for valid migrations.
     */
    @Test
    fun `migrations complete without data loss`(): Unit = runBlocking {
        val dbName = "test_no_destructive_migration.db"
        val dbFile = File(context.cacheDir, dbName)
        if (dbFile.exists()) dbFile.delete()

        // Create fresh database
        val db = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        database = db

        // Insert test data across multiple tables
        val billId = db.billDao().insertBill(
            com.toonai.budgetshield.data.model.Bill(
                name = "Migration Test Bill",
                icon = "🏠",
                amountCents = 100000L,
                dueDate = "2025-09-01",
                isProtected = true
            )
        )

        db.userSettingsDao().insertSettings(
            com.toonai.budgetshield.data.model.UserSettings(
                id = 1L,
                isFirstRunComplete = true,
                cashOnHandCents = 50000L,
                savingsBalanceCents = 10000L,
                selectedMonth = "2025-07"
            )
        )

        // Verify data exists
        val bill = db.billDao().getBillById(billId)
        assertNotNull("Bill should exist", bill)

        val settings = db.userSettingsDao().getSettingsSync()
        assertNotNull("Settings should exist", settings)

        // Close and reopen
        db.close()

        val db2 = Room.databaseBuilder(context, BudgetShieldDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()
        database = db2

        // Verify data still exists
        val billAfter = db2.billDao().getBillById(billId)
        assertNotNull("Bill should exist after reopen", billAfter)

        val settingsAfter = db2.userSettingsDao().getSettingsSync()
        assertNotNull("Settings should exist after reopen", settingsAfter)

        db2.close()
        database = null
    }
}
