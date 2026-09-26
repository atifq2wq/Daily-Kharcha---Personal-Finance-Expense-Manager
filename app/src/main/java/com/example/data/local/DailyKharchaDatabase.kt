package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TransactionEntity::class,
        UdhaarEntity::class,
        BillEntity::class,
        BudgetEntity::class,
        ShoppingItemEntity::class,
        FuelRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DailyKharchaDatabase : RoomDatabase() {

    abstract fun dao(): DailyKharchaDao

    companion object {
        @Volatile
        private var INSTANCE: DailyKharchaDatabase? = null

        fun getDatabase(context: Context): DailyKharchaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DailyKharchaDatabase::class.java,
                    "daily_kharcha_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
