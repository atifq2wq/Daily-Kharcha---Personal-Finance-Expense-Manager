package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BillEntity
import com.example.data.local.BudgetEntity
import com.example.data.local.DailyKharchaDatabase
import com.example.data.local.FuelRecordEntity
import com.example.data.local.ShoppingItemEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UdhaarEntity
import com.example.data.repository.DailyKharchaRepository
import com.example.ui.util.FinanceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardMetrics(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val monthlyCreditsCount: Int = 0,
    val monthlyDebitsCount: Int = 0,
    val totalBudget: Double = 0.0,
    val budgetUsed: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val savings: Double = 0.0,
    val udhaarReceivable: Double = 0.0,
    val udhaarPayable: Double = 0.0,
    val udhaarReceived: Double = 0.0,
    val unpaidBillsTotal: Double = 0.0,
    val activeUdhaarContactsCount: Int = 0
)

class DailyKharchaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DailyKharchaRepository

    init {
        val db = DailyKharchaDatabase.getDatabase(application)
        repository = DailyKharchaRepository(db.dao())
    }

    // Selected Month for viewing (defaults to current month)
    private val _selectedMonthCalendar = MutableStateFlow<Calendar>(Calendar.getInstance())
    val selectedMonthCalendar: StateFlow<Calendar> = _selectedMonthCalendar.asStateFlow()

    // Language toggle ("EN" or "UR")
    private val _language = MutableStateFlow("EN")
    val language: StateFlow<String> = _language.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == "EN") "UR" else "EN"
    }

    // Welcome banner visibility
    private val _isWelcomeDismissed = MutableStateFlow(false)
    val isWelcomeDismissed: StateFlow<Boolean> = _isWelcomeDismissed.asStateFlow()

    fun dismissWelcome() {
        _isWelcomeDismissed.value = true
    }

    // Month Navigation
    fun previousMonth() {
        val newCal = (_selectedMonthCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _selectedMonthCalendar.value = newCal
    }

    fun nextMonth() {
        val newCal = (_selectedMonthCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _selectedMonthCalendar.value = newCal
    }

    fun setMonth(calendar: Calendar) {
        _selectedMonthCalendar.value = calendar
    }

    // Data streams from Room
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUdhaars: StateFlow<List<UdhaarEntity>> = repository.allUdhaar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBills: StateFlow<List<BillEntity>> = repository.allBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShoppingItems: StateFlow<List<ShoppingItemEntity>> = repository.allShoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFuelRecords: StateFlow<List<FuelRecordEntity>> = repository.allFuelRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Month Budgets stream
    val currentMonthBudgets: StateFlow<List<BudgetEntity>> = _selectedMonthCalendar
        .flatMapLatest { cal ->
            val monthKey = FinanceUtils.getMonthYearKey(cal)
            repository.getBudgetsForMonth(monthKey)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Dashboard Calculations derived strictly from database
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        allTransactions,
        allUdhaars,
        allBills,
        currentMonthBudgets,
        _selectedMonthCalendar
    ) { txs, udhaars, bills, budgets, cal ->
        val monthKey = FinanceUtils.getMonthYearKey(cal)

        var totalIncome = 0.0
        var totalExpense = 0.0
        var todayIncome = 0.0
        var todayExpense = 0.0
        var monthlyIncome = 0.0
        var monthlyExpense = 0.0
        var monthlyCredits = 0
        var monthlyDebits = 0

        for (tx in txs) {
            val isIncome = tx.type.equals("INCOME", ignoreCase = true)
            val isTxToday = FinanceUtils.isToday(tx.dateEpochMillis)
            val isTxThisMonth = FinanceUtils.isSameMonth(tx.dateEpochMillis, monthKey)

            if (isIncome) {
                totalIncome += tx.amount
                if (isTxToday) todayIncome += tx.amount
                if (isTxThisMonth) {
                    monthlyIncome += tx.amount
                    monthlyCredits++
                }
            } else {
                totalExpense += tx.amount
                if (isTxToday) todayExpense += tx.amount
                if (isTxThisMonth) {
                    monthlyExpense += tx.amount
                    monthlyDebits++
                }
            }
        }

        // Current Balance = Total Income - Total Expense (strictly from zero)
        val totalBalance = totalIncome - totalExpense
        val monthlyBalance = monthlyIncome - monthlyExpense

        // Overall budget for this month
        val overallBudgetEntity = budgets.firstOrNull { it.category == "OVERALL" }
        val categoryBudgetsSum = budgets.filter { it.category != "OVERALL" }.sumOf { it.budgetLimit }
        val totalBudget = overallBudgetEntity?.budgetLimit ?: if (categoryBudgetsSum > 0) categoryBudgetsSum else 0.0

        val budgetUsed = monthlyExpense
        val remainingBudget = if (totalBudget > 0) {
            (totalBudget - monthlyExpense).coerceAtLeast(0.0)
        } else {
            0.0
        }

        // Savings: strictly 0.0 if no transactions. If monthly net is positive, it's savings.
        val savings = if (monthlyIncome > monthlyExpense) {
            monthlyIncome - monthlyExpense
        } else {
            0.0
        }

        // Udhaar totals
        var receivable = 0.0
        var payable = 0.0
        var receivedSoFar = 0.0
        var activeContacts = 0

        for (u in udhaars) {
            if (!u.isSettled) {
                activeContacts++
                val pending = (u.amount - u.settledAmount).coerceAtLeast(0.0)
                if (u.type.equals("TO_RECEIVE", ignoreCase = true)) {
                    receivable += pending
                    receivedSoFar += u.settledAmount
                } else {
                    payable += pending
                }
            }
        }

        // Bills
        val unpaidBills = bills.filter { !it.isPaid }.sumOf { it.amount }

        DashboardMetrics(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            todayIncome = todayIncome,
            todayExpense = todayExpense,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            monthlyBalance = monthlyBalance,
            monthlyCreditsCount = monthlyCredits,
            monthlyDebitsCount = monthlyDebits,
            totalBudget = totalBudget,
            budgetUsed = budgetUsed,
            remainingBudget = remainingBudget,
            savings = savings,
            udhaarReceivable = receivable,
            udhaarPayable = payable,
            udhaarReceived = receivedSoFar,
            unpaidBillsTotal = unpaidBills,
            activeUdhaarContactsCount = activeContacts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    // Transactions Operations
    fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        dateEpochMillis: Long,
        paymentMethod: String,
        notes: String = "",
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    dateEpochMillis = dateEpochMillis,
                    paymentMethod = paymentMethod,
                    notes = notes,
                    photoUri = photoUri
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // Udhaar Operations
    fun addUdhaar(
        type: String,
        personName: String,
        phone: String,
        amount: Double,
        dueDateMillis: Long,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertUdhaar(
                UdhaarEntity(
                    type = type,
                    personName = personName,
                    phone = phone,
                    amount = amount,
                    dueDateMillis = dueDateMillis,
                    notes = notes
                )
            )
        }
    }

    fun markUdhaarSettled(udhaar: UdhaarEntity, receivedOrPaid: Double = udhaar.amount) {
        viewModelScope.launch {
            repository.updateUdhaar(
                udhaar.copy(
                    settledAmount = udhaar.amount,
                    isSettled = true
                )
            )
        }
    }

    fun deleteUdhaar(udhaar: UdhaarEntity) {
        viewModelScope.launch {
            repository.deleteUdhaar(udhaar)
        }
    }

    // Bill Operations
    fun addBill(title: String, amount: Double, dueDateMillis: Long, frequency: String = "Monthly", category: String = "Bills") {
        viewModelScope.launch {
            repository.insertBill(
                BillEntity(
                    title = title,
                    amount = amount,
                    dueDateMillis = dueDateMillis,
                    frequency = frequency,
                    category = category
                )
            )
        }
    }

    fun toggleBillPaid(bill: BillEntity) {
        viewModelScope.launch {
            repository.updateBill(bill.copy(isPaid = !bill.isPaid))
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }

    // Budget Operations
    fun setMonthlyOverallBudget(budgetAmount: Double) {
        val monthKey = FinanceUtils.getMonthYearKey(_selectedMonthCalendar.value)
        viewModelScope.launch {
            repository.insertBudget(
                BudgetEntity(
                    category = "OVERALL",
                    monthYearKey = monthKey,
                    budgetLimit = budgetAmount
                )
            )
        }
    }

    fun setCategoryBudget(category: String, budgetAmount: Double) {
        val monthKey = FinanceUtils.getMonthYearKey(_selectedMonthCalendar.value)
        viewModelScope.launch {
            repository.insertBudget(
                BudgetEntity(
                    category = category,
                    monthYearKey = monthKey,
                    budgetLimit = budgetAmount
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // Shopping List Operations
    fun addShoppingItem(name: String, quantity: String, estimatedPrice: Double) {
        viewModelScope.launch {
            repository.insertShoppingItem(
                ShoppingItemEntity(
                    name = name,
                    quantity = quantity,
                    estimatedPrice = estimatedPrice
                )
            )
        }
    }

    fun toggleShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isPurchased = !item.isPurchased))
        }
    }

    fun deleteShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    // Fuel Operations
    fun addFuelRecord(liters: Double, pricePerLiter: Double, notes: String = "") {
        viewModelScope.launch {
            val totalCost = liters * pricePerLiter
            repository.insertFuelRecord(
                FuelRecordEntity(
                    liters = liters,
                    pricePerLiter = pricePerLiter,
                    totalCost = totalCost,
                    notes = notes
                )
            )
        }
    }

    fun deleteFuelRecord(record: FuelRecordEntity) {
        viewModelScope.launch {
            repository.deleteFuelRecord(record)
        }
    }
}
