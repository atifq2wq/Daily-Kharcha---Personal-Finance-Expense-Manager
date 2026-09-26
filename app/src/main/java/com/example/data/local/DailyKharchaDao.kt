package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyKharchaDao {

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY dateEpochMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    // Udhaar
    @Query("SELECT * FROM udhaar_records ORDER BY createdDateMillis DESC")
    fun getAllUdhaar(): Flow<List<UdhaarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUdhaar(udhaar: UdhaarEntity): Long

    @Update
    suspend fun updateUdhaar(udhaar: UdhaarEntity)

    @Delete
    suspend fun deleteUdhaar(udhaar: UdhaarEntity)

    @Query("DELETE FROM udhaar_records WHERE id = :id")
    suspend fun deleteUdhaarById(id: Long)

    // Bills
    @Query("SELECT * FROM bills ORDER BY dueDateMillis ASC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)

    // Budgets
    @Query("SELECT * FROM budgets WHERE monthYearKey = :monthYearKey")
    fun getBudgetsForMonth(monthYearKey: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // Shopping Items
    @Query("SELECT * FROM shopping_items ORDER BY id DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItemEntity): Long

    @Update
    suspend fun updateShoppingItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItemEntity)

    // Fuel Records
    @Query("SELECT * FROM fuel_records ORDER BY dateEpochMillis DESC")
    fun getAllFuelRecords(): Flow<List<FuelRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelRecord(record: FuelRecordEntity): Long

    @Delete
    suspend fun deleteFuelRecord(record: FuelRecordEntity)
}
