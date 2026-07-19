package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.toonai.budgetshield.data.model.Bill

/**
 * Room database for BudgetShield app.
 * Contains all persisted entities.
 */
@Database(
    entities = [Bill::class],
    version = 1,
    exportSchema = false
)
abstract class BudgetShieldDatabase : RoomDatabase() {
    
    abstract fun billDao(): BillDao
    
    companion object {
        @Volatile
        private var INSTANCE: BudgetShieldDatabase? = null
        
        fun getDatabase(context: Context): BudgetShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetShieldDatabase::class.java,
                    "budget_shield_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
