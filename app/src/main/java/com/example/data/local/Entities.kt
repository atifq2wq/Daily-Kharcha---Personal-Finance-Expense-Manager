package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "EXPENSE" or "INCOME"
    val amount: Double,
    val category: String,
    val dateEpochMillis: Long,
    val paymentMethod: String,
    val notes: String = "",
    val photoUri: String? = null
)

@Entity(tableName = "udhaar_records")
data class UdhaarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "TO_RECEIVE" or "TO_PAY"
    val personName: String,
    val phone: String = "",
    val amount: Double,
    val settledAmount: Double = 0.0,
    val dueDateMillis: Long,
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isSettled: Boolean = false
)

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDateMillis: Long,
    val frequency: String = "Monthly",
    val category: String = "Bills",
    val isPaid: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // e.g., "OVERALL", "Food & Dining", "Transport & Fuel", "Shopping", "Bills & Utilities"
    val monthYearKey: String, // e.g., "2026-09" (YYYY-MM)
    val budgetLimit: Double
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: String = "1 pc",
    val estimatedPrice: Double = 0.0,
    val isPurchased: Boolean = false
)

@Entity(tableName = "fuel_records")
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleName: String = "Honda Civic",
    val vehiclePlate: String = "LEA-1234",
    val liters: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
)
