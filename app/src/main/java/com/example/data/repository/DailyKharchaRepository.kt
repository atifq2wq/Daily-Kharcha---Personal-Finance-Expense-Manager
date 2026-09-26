package com.example.data.repository

import com.example.data.local.BillEntity
import com.example.data.local.BudgetEntity
import com.example.data.local.DailyKharchaDao
import com.example.data.local.FuelRecordEntity
import com.example.data.local.ShoppingItemEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UdhaarEntity
import kotlinx.coroutines.flow.Flow

class DailyKharchaRepository(private val dao: DailyKharchaDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    suspend fun insertTransaction(transaction: TransactionEntity): Long = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = dao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = dao.deleteTransaction(transaction)
    suspend fun deleteTransactionById(id: Long) = dao.deleteTransactionById(id)

    val allUdhaar: Flow<List<UdhaarEntity>> = dao.getAllUdhaar()
    suspend fun insertUdhaar(udhaar: UdhaarEntity): Long = dao.insertUdhaar(udhaar)
    suspend fun updateUdhaar(udhaar: UdhaarEntity) = dao.updateUdhaar(udhaar)
    suspend fun deleteUdhaar(udhaar: UdhaarEntity) = dao.deleteUdhaar(udhaar)

    val allBills: Flow<List<BillEntity>> = dao.getAllBills()
    suspend fun insertBill(bill: BillEntity): Long = dao.insertBill(bill)
    suspend fun updateBill(bill: BillEntity) = dao.updateBill(bill)
    suspend fun deleteBill(bill: BillEntity) = dao.deleteBill(bill)

    fun getBudgetsForMonth(monthKey: String): Flow<List<BudgetEntity>> = dao.getBudgetsForMonth(monthKey)
    suspend fun insertBudget(budget: BudgetEntity): Long = dao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = dao.deleteBudget(budget)

    val allShoppingItems: Flow<List<ShoppingItemEntity>> = dao.getAllShoppingItems()
    suspend fun insertShoppingItem(item: ShoppingItemEntity): Long = dao.insertShoppingItem(item)
    suspend fun updateShoppingItem(item: ShoppingItemEntity) = dao.updateShoppingItem(item)
    suspend fun deleteShoppingItem(item: ShoppingItemEntity) = dao.deleteShoppingItem(item)

    val allFuelRecords: Flow<List<FuelRecordEntity>> = dao.getAllFuelRecords()
    suspend fun insertFuelRecord(record: FuelRecordEntity): Long = dao.insertFuelRecord(record)
    suspend fun deleteFuelRecord(record: FuelRecordEntity) = dao.deleteFuelRecord(record)
}
