package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DailyKharchaDao
import com.example.data.local.DailyKharchaDatabase
import com.example.data.local.TransactionEntity
import com.example.data.local.UdhaarEntity
import com.example.data.local.BillEntity
import com.example.data.local.BudgetEntity
import com.example.data.repository.DailyKharchaRepository
import com.example.ui.util.FinanceUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DailyKharchaAccountingTest {

    private lateinit var db: DailyKharchaDatabase
    private lateinit var dao: DailyKharchaDao
    private lateinit var repository: DailyKharchaRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DailyKharchaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dao()
        repository = DailyKharchaRepository(dao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // CUJ 1: New user starts strictly from ZERO
    @Test
    fun testNewUserStartsWithZero() = runBlocking {
        val transactions = repository.allTransactions.first()
        val udhaars = repository.allUdhaar.first()
        val bills = repository.allBills.first()
        val shopping = repository.allShoppingItems.first()
        val fuel = repository.allFuelRecords.first()

        assertEquals(0, transactions.size)
        assertEquals(0, udhaars.size)
        assertEquals(0, bills.size)
        assertEquals(0, shopping.size)
        assertEquals(0, fuel.size)

        val totalIncome = transactions.filter { it.type.equals("INCOME", true) }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        val totalBalance = totalIncome - totalExpense

        assertEquals(0.0, totalBalance, 0.001)
        assertEquals(0.0, totalIncome, 0.001)
        assertEquals(0.0, totalExpense, 0.001)
        assertEquals("Rs. 0", FinanceUtils.formatCurrency(totalBalance))
    }

    // CUJ 2 & 3: Add Income Rs. 10,000 -> Balance Rs. 10,000, Add Expense Rs. 2,500 -> Balance Rs. 7,500
    @Test
    fun testAddIncomeAndExpense() = runBlocking {
        val now = System.currentTimeMillis()
        val txId1 = repository.insertTransaction(
            TransactionEntity(
                type = "INCOME",
                amount = 10000.0,
                category = "Salary",
                dateEpochMillis = now,
                paymentMethod = "Bank Transfer"
            )
        )

        var txs = repository.allTransactions.first()
        var income = txs.filter { it.type.equals("INCOME", true) }.sumOf { it.amount }
        var expense = txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        assertEquals(10000.0, income - expense, 0.001)
        assertEquals("Rs. 10,000", FinanceUtils.formatCurrency(income - expense))

        val txId2 = repository.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 2500.0,
                category = "Food",
                dateEpochMillis = now,
                paymentMethod = "Cash (Wallet)"
            )
        )

        txs = repository.allTransactions.first()
        income = txs.filter { it.type.equals("INCOME", true) }.sumOf { it.amount }
        expense = txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        assertEquals(7500.0, income - expense, 0.001)
        assertEquals("Rs. 7,500", FinanceUtils.formatCurrency(income - expense))
    }

    // CUJ 4 & 5: Delete expense -> balance Rs. 10,000; Delete income -> balance Rs. 0
    @Test
    fun testDeleteExpenseAndIncome() = runBlocking {
        val now = System.currentTimeMillis()
        val incomeEntity = TransactionEntity(
            type = "INCOME",
            amount = 10000.0,
            category = "Salary",
            dateEpochMillis = now,
            paymentMethod = "Bank Transfer"
        )
        val incId = repository.insertTransaction(incomeEntity)

        val expenseEntity = TransactionEntity(
            type = "EXPENSE",
            amount = 2500.0,
            category = "Food",
            dateEpochMillis = now,
            paymentMethod = "Cash"
        )
        val expId = repository.insertTransaction(expenseEntity)

        // Delete expense
        repository.deleteTransactionById(expId)
        var txs = repository.allTransactions.first()
        var balance = txs.filter { it.type.equals("INCOME", true) }.sumOf { it.amount } -
                txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        assertEquals(10000.0, balance, 0.001)

        // Delete income
        repository.deleteTransactionById(incId)
        txs = repository.allTransactions.first()
        balance = txs.filter { it.type.equals("INCOME", true) }.sumOf { it.amount } -
                txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        assertEquals(0.0, balance, 0.001)
        assertEquals("Rs. 0", FinanceUtils.formatCurrency(balance))
    }

    // Negative Balance test: expense of Rs. 500 without income -> balance becomes -Rs. 500
    @Test
    fun testNegativeBalanceIndication() = runBlocking {
        val now = System.currentTimeMillis()
        repository.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 500.0,
                category = "Food",
                dateEpochMillis = now,
                paymentMethod = "Cash"
            )
        )

        val txs = repository.allTransactions.first()
        val income = txs.filter { it.type.equals("INCOME", true) }.sumOf { it.amount }
        val expense = txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        val balance = income - expense

        assertEquals(-500.0, balance, 0.001)
        assertEquals("- Rs. 500", FinanceUtils.formatCurrency(balance))
    }

    // CUJ 6: Edit transactions updates calculations immediately
    @Test
    fun testEditTransactionUpdatesBalance() = runBlocking {
        val now = System.currentTimeMillis()
        val txId = repository.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 2500.0,
                category = "Food",
                dateEpochMillis = now,
                paymentMethod = "Cash"
            )
        )

        // Edit amount to 3,000
        val updatedTx = TransactionEntity(
            id = txId,
            type = "EXPENSE",
            amount = 3000.0,
            category = "Food",
            dateEpochMillis = now,
            paymentMethod = "Cash"
        )
        repository.updateTransaction(updatedTx)

        val txs = repository.allTransactions.first()
        val expense = txs.filter { it.type.equals("EXPENSE", true) }.sumOf { it.amount }
        assertEquals(3000.0, expense, 0.001)
        assertEquals("- Rs. 3,000", FinanceUtils.formatCurrency(-expense))
    }

    // CUJ 8: Month isolation: transactions in another month do not mix with current month
    @Test
    fun testMonthlyCalculationsIsolation() = runBlocking {
        val juneCal = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 15)
        }
        val julyCal = Calendar.getInstance().apply {
            set(2025, Calendar.JULY, 10)
        }

        val juneMonthKey = FinanceUtils.getMonthYearKey(juneCal)
        val julyMonthKey = FinanceUtils.getMonthYearKey(julyCal)

        // Add expense in June
        repository.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 1200.0,
                category = "Internet",
                dateEpochMillis = juneCal.timeInMillis,
                paymentMethod = "Card"
            )
        )

        // Add expense in July
        repository.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 5000.0,
                category = "Shopping",
                dateEpochMillis = julyCal.timeInMillis,
                paymentMethod = "Card"
            )
        )

        val allTxs = repository.allTransactions.first()

        val juneExpenses = allTxs.filter {
            it.type.equals("EXPENSE", true) && FinanceUtils.isSameMonth(it.dateEpochMillis, juneMonthKey)
        }.sumOf { it.amount }

        val julyExpenses = allTxs.filter {
            it.type.equals("EXPENSE", true) && FinanceUtils.isSameMonth(it.dateEpochMillis, julyMonthKey)
        }.sumOf { it.amount }

        assertEquals(1200.0, juneExpenses, 0.001)
        assertEquals(5000.0, julyExpenses, 0.001)
    }
}
