package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BudgetEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.components.getCategoryIconAndColor
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.ShoppingAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.FinanceUtils
import java.util.Calendar

@Composable
fun BudgetScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedCal by viewModel.selectedMonthCalendar.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val budgets by viewModel.currentMonthBudgets.collectAsState()

    val monthName = FinanceUtils.getMonthYearDisplay(selectedCal)
    val monthKey = FinanceUtils.getMonthYearKey(selectedCal)

    // Calculate actual expenses for this month per category
    val currentMonthExpenses = allTransactions.filter {
        it.type.equals("EXPENSE", ignoreCase = true) &&
                FinanceUtils.isSameMonth(it.dateEpochMillis, monthKey)
    }

    val expenseByCategory = currentMonthExpenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }

    var showSetOverallBudgetDialog by remember { mutableStateOf(false) }
    var showAddCategoryBudgetDialog by remember { mutableStateOf(false) }
    var overallBudgetInput by remember { mutableStateOf("") }
    var selectedCategoryForBudget by remember { mutableStateOf("Food") }
    var categoryBudgetInput by remember { mutableStateOf("") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val percentUsed = if (metrics.totalBudget > 0) {
        ((metrics.budgetUsed / metrics.totalBudget) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = "Budget Planner",
            onBackClick = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Month Switcher Strip
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { viewModel.previousMonth() },
                            modifier = Modifier.testTag("budget_prev_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Month",
                                tint = TextSecondary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = monthName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.testTag("budget_next_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Month",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            // Hero Progress Overview Card (Circular Radial Progress Gauge)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_overview_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Monthly Cap Track",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Active Month",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Circular Radial Gauge
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .testTag("budget_radial_gauge"),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFFF1F5F9),
                                    strokeWidth = 10.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                CircularProgressIndicator(
                                    progress = { if (metrics.totalBudget > 0) (percentUsed / 100f) else 0f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (percentUsed > 80) ExpenseRed else EmeraldPrimary,
                                    strokeWidth = 10.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$percentUsed%",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "USED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Breakup Numbers
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Total Budget
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .clickable {
                                            overallBudgetInput = if (metrics.totalBudget > 0) metrics.totalBudget.toInt().toString() else ""
                                            showSetOverallBudgetDialog = true
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Total Budget",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                            Text(
                                                text = FinanceUtils.formatCurrency(metrics.totalBudget),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = "Set",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }

                                // Used So Far
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ExpenseRed))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Used", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = FinanceUtils.formatCurrency(metrics.budgetUsed),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseRed
                                    )
                                }

                                // Remaining Safe
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldPrimary))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Remaining", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = FinanceUtils.formatCurrency(metrics.remainingBudget),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Health Pace Indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF0FDF4))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (percentUsed <= 80) "Spending pace is on target" else "High spending pace this month",
                                    fontSize = 11.sp,
                                    color = if (percentUsed <= 80) IncomeGreen else ExpenseRed
                                )
                            }
                            Text(
                                text = if (percentUsed <= 80) "Safe" else "Alert",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (percentUsed <= 80) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }
            }

            // Smart Saving Tip Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SMART SAVING TIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintLight,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${FinanceUtils.formatCurrency(metrics.remainingBudget)} remains safe",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (metrics.budgetUsed == 0.0) "Your budget starts fresh and clean at zero." else "Track your expenses consistently to maintain savings goals.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = MintLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Category Budgets Section Header
            val categoryBudgetsList = budgets.filter { it.category != "OVERALL" }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Category Budgets",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE2E8F0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${categoryBudgetsList.size} Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "+ Add Budget",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier
                            .clickable { showAddCategoryBudgetDialog = true }
                            .testTag("add_category_budget_button")
                    )
                }
            }

            // Category Budgets List
            if (categoryBudgetsList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No category budgets set for $monthName",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showAddCategoryBudgetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Set Category Budget", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(categoryBudgetsList) { catBudget ->
                    val spent = expenseByCategory[catBudget.category] ?: 0.0
                    val catPercent = if (catBudget.budgetLimit > 0) {
                        ((spent / catBudget.budgetLimit) * 100).toInt().coerceIn(0, 100)
                    } else 0
                    val (catIcon, catColor) = getCategoryIconAndColor(catBudget.category)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(catColor.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = catIcon,
                                            contentDescription = null,
                                            tint = catColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = catBudget.category,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${FinanceUtils.formatCurrency(spent)} / ${FinanceUtils.formatCurrency(catBudget.budgetLimit)}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (catPercent >= 80) Color(0xFFFEF2F2) else Color(0xFFF0FDF4))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$catPercent%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (catPercent >= 80) ExpenseRed else IncomeGreen
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteBudget(catBudget) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { (catPercent / 100f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (catPercent >= 80) ExpenseRed else catColor,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }

            // Bottom Add Category Budget CTA
            item {
                Button(
                    onClick = { showAddCategoryBudgetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bottom_add_category_budget_cta"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Category Budget",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Set Overall Budget Dialog
    if (showSetOverallBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showSetOverallBudgetDialog = false },
            title = { Text("Set Total Monthly Budget") },
            text = {
                Column {
                    Text(
                        text = "Enter overall spending cap for $monthName:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = overallBudgetInput,
                        onValueChange = { overallBudgetInput = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("e.g. 50000") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = overallBudgetInput.toDoubleOrNull()
                        if (amount != null && amount >= 0) {
                            viewModel.setMonthlyOverallBudget(amount)
                            showSetOverallBudgetDialog = false
                            Toast.makeText(context, "Monthly budget set to Rs. $amount", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetOverallBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Category Budget Dialog
    if (showAddCategoryBudgetDialog) {
        val categoryOptions = listOf(
            "Food", "Grocery", "Transport", "Fuel",
            "Shopping", "Bills", "Mobile", "Internet",
            "Education", "Health", "Fun", "Other"
        )

        AlertDialog(
            onDismissRequest = { showAddCategoryBudgetDialog = false },
            title = { Text("Add Category Budget") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Category:", fontSize = 12.sp, color = TextSecondary)
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showCategoryDropdown = true }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedCategoryForBudget, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("▼", fontSize = 10.sp)
                        }

                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categoryOptions.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategoryForBudget = cat
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Budget Limit (PKR):", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = categoryBudgetInput,
                        onValueChange = { categoryBudgetInput = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("e.g. 10000") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = categoryBudgetInput.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.setCategoryBudget(selectedCategoryForBudget, amount)
                            showAddCategoryBudgetDialog = false
                            categoryBudgetInput = ""
                            Toast.makeText(context, "$selectedCategoryForBudget budget saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
