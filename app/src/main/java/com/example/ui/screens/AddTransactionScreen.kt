package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.components.getCategoryIconAndColor
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.FinanceUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: DailyKharchaViewModel,
    initialIsExpense: Boolean = true,
    editingTransaction: TransactionEntity? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isExpense by remember { mutableStateOf(editingTransaction?.type?.equals("EXPENSE", ignoreCase = true) ?: initialIsExpense) }
    var amountString by remember { mutableStateOf(editingTransaction?.amount?.let { FinanceUtils.formatNumber(it) } ?: "") }
    var selectedCategory by remember {
        mutableStateOf(editingTransaction?.category ?: if (isExpense) "Food" else "Salary")
    }
    var notes by remember { mutableStateOf(editingTransaction?.notes ?: "") }
    var paymentMethod by remember { mutableStateOf(editingTransaction?.paymentMethod ?: "Cash (Wallet)") }
    var dateEpoch by remember { mutableLongStateOf(editingTransaction?.dateEpochMillis ?: System.currentTimeMillis()) }

    var showPaymentDropdown by remember { mutableStateOf(false) }
    var showCustomCategoryDialog by remember { mutableStateOf(false) }
    var customCategoryName by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        "Food", "Grocery", "Transport", "Fuel",
        "Shopping", "Bills", "Mobile", "Internet",
        "Education", "Health", "Fun", "Other"
    )

    val incomeCategories = listOf(
        "Salary", "Business", "Freelance", "Online",
        "Gift", "Rental", "Investment", "Other"
    )

    val currentCategories = if (isExpense) expenseCategories else incomeCategories

    val paymentOptions = listOf(
        "Cash (Wallet)",
        "Bank Transfer",
        "EasyPaisa",
        "JazzCash",
        "Card"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = if (editingTransaction != null) "Edit Transaction" else "Quick Add Transaction",
            onBackClick = onNavigateBack,
            trailingContent = {
                if (editingTransaction != null) {
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("delete_transaction_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ExpenseRed
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Segmented Switcher: Expense / Income
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(4.dp)
                ) {
                    // Expense Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isExpense) ExpenseRed else Color.Transparent)
                            .clickable {
                                isExpense = true
                                if (!expenseCategories.contains(selectedCategory)) {
                                    selectedCategory = "Food"
                                }
                            }
                            .padding(vertical = 10.dp)
                            .testTag("segment_expense_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isExpense) Color.White else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Expense",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isExpense) Color.White else TextSecondary
                            )
                        }
                    }

                    // Income Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isExpense) EmeraldPrimary else Color.Transparent)
                            .clickable {
                                isExpense = false
                                if (!incomeCategories.contains(selectedCategory)) {
                                    selectedCategory = "Salary"
                                }
                            }
                            .padding(vertical = 10.dp)
                            .testTag("segment_income_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (!isExpense) Color.White else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Income",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (!isExpense) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Amount Input Panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isExpense) "ENTER EXPENSE AMOUNT" else "ENTER INCOME AMOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Rs.",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpense) ExpenseRed else IncomeGreen,
                                modifier = Modifier.padding(end = 6.dp)
                            )

                            OutlinedTextField(
                                value = amountString,
                                onValueChange = { newValue ->
                                    // Filter numbers only
                                    val filtered = newValue.filter { it.isDigit() || it == '.' }
                                    amountString = filtered
                                },
                                placeholder = {
                                    Text(
                                        text = "0",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .width(180.dp)
                                    .testTag("amount_input_field")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Preset Chips (+200, +500, +1k, +5k)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(200, 500, 1000, 5000).forEach { preset ->
                                val label = if (preset >= 1000) "+${preset / 1000}k" else "+$preset"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .clickable {
                                            val current = amountString.toDoubleOrNull() ?: 0.0
                                            amountString = FinanceUtils.formatNumber(current + preset)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Selection Section (4-column grid)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Category",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Required",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentCategories.chunked(4).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { cat ->
                                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                                        val (icon, catColor) = getCategoryIconAndColor(cat, isExpense)

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isSelected) {
                                                        if (isExpense) ExpenseRedLight else IncomeGreenLight
                                                    } else {
                                                        Color(0xFFF8FAFC)
                                                    }
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) {
                                                        if (isExpense) ExpenseRed else IncomeGreen
                                                    } else {
                                                        Color(0xFFE2E8F0)
                                                    },
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedCategory = cat }
                                                .padding(vertical = 10.dp)
                                                .testTag("category_${cat.lowercase()}"),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isSelected) {
                                                            if (isExpense) ExpenseRed else IncomeGreen
                                                        } else {
                                                            catColor.copy(alpha = 0.15f)
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = cat,
                                                    tint = if (isSelected) Color.White else catColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = cat,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) {
                                                    if (isExpense) ExpenseRed else IncomeGreen
                                                } else {
                                                    TextPrimary
                                                },
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    // Fill empty slots if last row has fewer than 4 items
                                    if (rowItems.size < 4) {
                                        repeat(4 - rowItems.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Category trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showCustomCategoryDialog = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MintGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Custom Category",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = if (!currentCategories.contains(selectedCategory)) selectedCategory else "+ Add",
                                fontSize = 11.sp,
                                color = EmeraldMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Transaction Metadata Card (Date, Payment Method, Notes)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date & Time pickers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Date
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = EmeraldMedium,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Date", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = FinanceUtils.formatDate(dateEpoch),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            // Time
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = EmeraldMedium,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Time", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = FinanceUtils.formatTime(dateEpoch),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        // Payment Method Selector
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable { showPaymentDropdown = true }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("payment_method_selector"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Payment Method", fontSize = 10.sp, color = TextMuted)
                                        Text(
                                            text = paymentMethod,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                                Text("▼", fontSize = 10.sp, color = TextMuted)
                            }

                            DropdownMenu(
                                expanded = showPaymentDropdown,
                                onDismissRequest = { showPaymentDropdown = false }
                            ) {
                                paymentOptions.forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method, fontSize = 13.sp) },
                                        onClick = {
                                            paymentMethod = method
                                            showPaymentDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Notes Field
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (Optional)", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Lunch Biryani with colleagues", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notes_input_field"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Add Photo Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable {
                                    Toast.makeText(context, "Receipt attached", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Photo (Optional)",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(">", fontSize = 13.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Save Action CTA Button
            item {
                Button(
                    onClick = {
                        val amount = amountString.replace(",", "").toDoubleOrNull()
                        if (amount == null || amount <= 0.0) {
                            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val txType = if (isExpense) "EXPENSE" else "INCOME"

                        if (editingTransaction != null) {
                            viewModel.updateTransaction(
                                editingTransaction.copy(
                                    type = txType,
                                    amount = amount,
                                    category = selectedCategory,
                                    dateEpochMillis = dateEpoch,
                                    paymentMethod = paymentMethod,
                                    notes = notes
                                )
                            )
                            Toast.makeText(context, "Transaction updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addTransaction(
                                type = txType,
                                amount = amount,
                                category = selectedCategory,
                                dateEpochMillis = dateEpoch,
                                paymentMethod = paymentMethod,
                                notes = notes
                            )
                            Toast.makeText(context, "Transaction saved!", Toast.LENGTH_SHORT).show()
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_transaction_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExpense) ExpenseRed else EmeraldPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (editingTransaction != null) {
                            "Update ${if (isExpense) "Expense" else "Income"}"
                        } else {
                            "Save ${if (isExpense) "Expense" else "Income"}"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // Custom Category Dialog
    if (showCustomCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCustomCategoryDialog = false },
            title = { Text("Add Custom Category") },
            text = {
                OutlinedTextField(
                    value = customCategoryName,
                    onValueChange = { customCategoryName = it },
                    placeholder = { Text("e.g. Pet Care, Charity") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customCategoryName.isNotBlank()) {
                            selectedCategory = customCategoryName.trim()
                            showCustomCategoryDialog = false
                            customCategoryName = ""
                        }
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog && editingTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? All balance totals will be immediately updated.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(editingTransaction)
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "Transaction deleted!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
