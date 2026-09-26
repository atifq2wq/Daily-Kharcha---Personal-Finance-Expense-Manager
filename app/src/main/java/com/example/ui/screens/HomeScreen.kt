package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.DailyKharchaHeader
import com.example.ui.components.getCategoryIconAndColor
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.MintDark
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.ShoppingAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UdhaarIndigo
import com.example.ui.util.FinanceUtils

@Composable
fun HomeScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateToAddTransaction: (isExpense: Boolean) -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToUdhaar: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val isWelcomeDismissed by viewModel.isWelcomeDismissed.collectAsState()
    val selectedCal by viewModel.selectedMonthCalendar.collectAsState()
    val currentLang by viewModel.language.collectAsState()

    val monthName = FinanceUtils.getMonthShortDisplay(selectedCal)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // App Top Bar
        DailyKharchaHeader(
            isUrdu = currentLang == "UR",
            onLanguageToggle = { viewModel.toggleLanguage() },
            onProfileClick = onNavigateToSettings
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Welcome Message for First-Time Users starting from zero
            if (!isWelcomeDismissed && transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen.copy(alpha = 0.4f))
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
                                    text = if (currentLang == "UR") "ڈیلی خرچہ میں خوش آمدید" else "Welcome to Daily Kharcha",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (currentLang == "UR") "اپنے اخراجات کا حساب صفر سے شروع کریں۔" else "Start tracking your money from zero.",
                                    fontSize = 12.sp,
                                    color = EmeraldMedium
                                )
                            }
                            IconButton(
                                onClick = { viewModel.dismissWelcome() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = EmeraldMedium,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Total Net Balance Emerald Hero Card
            item {
                TotalBalanceHeroCard(
                    totalBalance = metrics.totalBalance,
                    monthlyBalance = metrics.monthlyBalance,
                    remainingBudget = metrics.remainingBudget,
                    savings = metrics.savings,
                    monthName = monthName
                )
            }

            // 2x2 Financial Quick Overview Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Today's Income
                    MetricTile(
                        modifier = Modifier.weight(1f),
                        title = "Today's Income",
                        amount = metrics.todayIncome,
                        icon = Icons.Default.ArrowUpward,
                        iconColor = IncomeGreen,
                        iconBg = IncomeGreenLight,
                        borderColor = IncomeGreen.copy(alpha = 0.2f),
                        bgColor = Color(0xFFF0FDF4),
                        testTag = "today_income_metric"
                    )
                    // Today's Expense
                    MetricTile(
                        modifier = Modifier.weight(1f),
                        title = "Today's Expense",
                        amount = metrics.todayExpense,
                        icon = Icons.Default.ArrowDownward,
                        iconColor = ExpenseRed,
                        iconBg = ExpenseRedLight,
                        borderColor = ExpenseRed.copy(alpha = 0.2f),
                        bgColor = Color(0xFFFEF2F2),
                        testTag = "today_expense_metric"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Monthly Income
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("monthly_income_metric"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monthly Income",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(IncomeGreen)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = FinanceUtils.formatCurrency(metrics.monthlyIncome),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${metrics.monthlyCreditsCount} credits recorded",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Monthly Expense
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("monthly_expense_metric"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monthly Expense",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ExpenseRed)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = FinanceUtils.formatCurrency(metrics.monthlyExpense),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${metrics.monthlyDebitsCount} debits recorded",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Quick Actions (Expense, Income, Udhaar, Bills, Shopping)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "QUICK ACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionButton(
                                label = "Expense",
                                color = ExpenseRed,
                                icon = Icons.Default.Add,
                                onClick = { onNavigateToAddTransaction(true) },
                                testTag = "quick_action_expense"
                            )
                            QuickActionButton(
                                label = "Income",
                                color = IncomeGreen,
                                icon = Icons.Default.Add,
                                onClick = { onNavigateToAddTransaction(false) },
                                testTag = "quick_action_income"
                            )
                            QuickActionButton(
                                label = "Udhaar",
                                color = UdhaarIndigo,
                                icon = Icons.Default.Group,
                                onClick = onNavigateToUdhaar,
                                testTag = "quick_action_udhaar"
                            )
                            QuickActionButton(
                                label = "Bills",
                                color = Color(0xFF8B5CF6),
                                icon = Icons.Default.Receipt,
                                onClick = onNavigateToBills,
                                testTag = "quick_action_bills"
                            )
                            QuickActionButton(
                                label = "Shopping",
                                color = ShoppingAmber,
                                icon = Icons.Default.ShoppingCart,
                                onClick = onNavigateToShopping,
                                testTag = "quick_action_shopping"
                            )
                        }
                    }
                }
            }

            // Recent Transactions Preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transactions",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "View All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldMedium,
                                modifier = Modifier
                                    .clickable { onNavigateToTransactions() }
                                    .testTag("view_all_transactions_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (transactions.isEmpty()) {
                            // Explicit Zero State Requirement: "No transactions yet"
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .testTag("empty_transactions_state"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No transactions yet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Start by adding your first expense or income.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                // Prominent Add Expense Button requested by user
                                Button(
                                    onClick = { onNavigateToAddTransaction(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("prominent_add_expense_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Add Expense",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        } else {
                            val recentList = transactions.take(4)
                            recentList.forEachIndexed { index, tx ->
                                TransactionRowItem(
                                    transaction = tx,
                                    onClick = onNavigateToTransactions
                                )
                                if (index < recentList.size - 1) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFFF1F5F9))
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Monthly Budget Banner
            item {
                MonthlyBudgetCallout(
                    monthName = monthName,
                    totalBudget = metrics.totalBudget,
                    budgetUsed = metrics.budgetUsed,
                    remainingBudget = metrics.remainingBudget,
                    onDetailsClick = onNavigateToBudget
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TotalBalanceHeroCard(
    totalBalance: Double,
    monthlyBalance: Double,
    remainingBudget: Double,
    savings: Double,
    monthName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("total_balance_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF004D40), Color(0xFF00695C), Color(0xFF00796B))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL NET BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 0.8.sp
                    )

                    // Month badge
                    val balanceMonthText = if (monthlyBalance >= 0) {
                        "+ ${FinanceUtils.formatCurrency(monthlyBalance)} this mo"
                    } else {
                        "${FinanceUtils.formatCurrency(monthlyBalance)} this mo"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = balanceMonthText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (monthlyBalance >= 0) MintLight else Color(0xFFFFCDD2)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Balance display: clearly handles negative e.g. - Rs. 500
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = FinanceUtils.formatCurrency(totalBalance),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (totalBalance < 0) Color(0xFFFF8A80) else Color.White
                    )
                    Text(
                        text = "PKR",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                if (totalBalance < 0) {
                    Text(
                        text = "Negative Balance",
                        color = Color(0xFFFF8A80),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider line with mini stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MintGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Remaining: ",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = FinanceUtils.formatCurrency(remainingBudget),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(ShoppingAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Savings: ",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = FinanceUtils.formatCurrency(savings),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricTile(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBg: Color,
    borderColor: Color,
    bgColor: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = FinanceUtils.formatCurrency(amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = iconColor
            )
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val isExpense = transaction.type.equals("EXPENSE", ignoreCase = true)
    val (icon, color) = getCategoryIconAndColor(transaction.category, isExpense)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = transaction.category,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.notes.isNotBlank()) "${transaction.category} - ${transaction.notes}" else transaction.category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                val dateLabel = if (FinanceUtils.isToday(transaction.dateEpochMillis)) {
                    "Today ${FinanceUtils.formatTime(transaction.dateEpochMillis)}"
                } else {
                    "${FinanceUtils.formatDate(transaction.dateEpochMillis)} ${FinanceUtils.formatTime(transaction.dateEpochMillis)}"
                }
                Text(
                    text = "$dateLabel • ${transaction.paymentMethod}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            val amountFormatted = if (isExpense) {
                "- ${FinanceUtils.formatCurrency(transaction.amount)}"
            } else {
                "+ ${FinanceUtils.formatCurrency(transaction.amount)}"
            }
            Text(
                text = amountFormatted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) ExpenseRed else IncomeGreen
            )
            Text(
                text = if (isExpense) "Expense" else "Income",
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun MonthlyBudgetCallout(
    monthName: String,
    totalBudget: Double,
    budgetUsed: Double,
    remainingBudget: Double,
    onDetailsClick: () -> Unit
) {
    val percentUsed = if (totalBudget > 0) ((budgetUsed / totalBudget) * 100).toInt() else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_budget_callout"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF10B981), Color(0xFF0D9488))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$monthName MONTHLY BUDGET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = FinanceUtils.formatCurrency(budgetUsed),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " / ${FinanceUtils.formatCurrency(totalBudget)}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$percentUsed% used • ${FinanceUtils.formatCurrency(remainingBudget)} left",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Button(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF065F46)
                    ),
                    modifier = Modifier.testTag("budget_details_button")
                ) {
                    Text(
                        text = "Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
