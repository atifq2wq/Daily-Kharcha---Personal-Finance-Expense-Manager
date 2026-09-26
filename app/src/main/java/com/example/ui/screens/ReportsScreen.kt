package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.components.getCategoryIconAndColor
import com.example.ui.theme.EmeraldDark
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
import java.util.Calendar

@Composable
fun ReportsScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCal by viewModel.selectedMonthCalendar.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()

    val monthName = FinanceUtils.getMonthShortDisplay(selectedCal)
    val monthKey = FinanceUtils.getMonthYearKey(selectedCal)

    var selectedTab by remember { mutableStateOf("Overview") } // "Overview", "Category", "Monthly", "Yearly"

    // Filter current month transactions
    val monthTransactions = allTransactions.filter {
        FinanceUtils.isSameMonth(it.dateEpochMillis, monthKey)
    }

    val monthExpenses = monthTransactions.filter { it.type.equals("EXPENSE", ignoreCase = true) }
    val monthIncomes = monthTransactions.filter { it.type.equals("INCOME", ignoreCase = true) }

    val totalMonthExpense = monthExpenses.sumOf { it.amount }
    val totalMonthIncome = monthIncomes.sumOf { it.amount }

    // Group expenses by category
    val categoryTotals = monthExpenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    // Breakdown into 4 weeks of the month for bar chart
    val weeklyData = remember(monthTransactions) {
        val weekIncomes = DoubleArray(5) { 0.0 }
        val weekExpenses = DoubleArray(5) { 0.0 }

        monthTransactions.forEach { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.dateEpochMillis }
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val weekIndex = ((day - 1) / 7).coerceIn(0, 4)
            if (tx.type.equals("INCOME", ignoreCase = true)) {
                weekIncomes[weekIndex] += tx.amount
            } else {
                weekExpenses[weekIndex] += tx.amount
            }
        }
        Pair(weekIncomes, weekExpenses)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = "Reports & Analytics",
            onBackClick = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Month Selector Pill
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.nextMonth() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "This Month ($monthName)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Timeframe Tabs (Overview | Category | Monthly | Yearly)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(3.dp)
                ) {
                    listOf("Overview", "Category", "Monthly", "Yearly").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 8.dp)
                                .testTag("reports_tab_${tab.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Income vs Expense Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Income vs Expense",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Summary Cards Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Income Box
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IncomeGreenLight)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = IncomeGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Income", fontSize = 10.sp, color = TextSecondary)
                                    Text(
                                        text = FinanceUtils.formatCurrency(totalMonthIncome),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IncomeGreen
                                    )
                                }
                            }

                            // Expense Box
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ExpenseRedLight)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = ExpenseRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Expense", fontSize = 10.sp, color = TextSecondary)
                                    Text(
                                        text = FinanceUtils.formatCurrency(totalMonthExpense),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Weekly Bar Chart based on REAL transactions
                        val (incomes, expenses) = weeklyData
                        val maxVal = (incomes.maxOrNull() ?: 0.0).coerceAtLeast(expenses.maxOrNull() ?: 0.0).coerceAtLeast(100.0)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val weekLabels = listOf("1 $monthName", "7 $monthName", "14 $monthName", "21 $monthName", "28 $monthName")

                                for (i in 0..4) {
                                    val incHeightRatio = (incomes[i] / maxVal).toFloat().coerceIn(0.04f, 1f)
                                    val expHeightRatio = (expenses[i] / maxVal).toFloat().coerceIn(0.04f, 1f)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier = Modifier.height(110.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            // Income bar
                                            Box(
                                                modifier = Modifier
                                                    .width(10.dp)
                                                    .height((100 * incHeightRatio).dp)
                                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                    .background(if (incomes[i] > 0) IncomeGreen else Color(0xFFE2E8F0))
                                            )
                                            // Expense bar
                                            Box(
                                                modifier = Modifier
                                                    .width(10.dp)
                                                    .height((100 * expHeightRatio).dp)
                                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                    .background(if (expenses[i] > 0) ExpenseRed else Color(0xFFE2E8F0))
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "${i * 7 + 1} ${monthName.take(3)}",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Income", fontSize = 11.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Expense", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Top Spending Categories (Donut chart & Legend)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Top Spending Categories",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (totalMonthExpense == 0.0) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PieChart,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No expenses recorded this month",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Charts will automatically populate when you add transactions.",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Donut Chart Canvas
                                Box(
                                    modifier = Modifier.size(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        var startAngle = -90f
                                        val strokeWidth = 24f

                                        categoryTotals.forEach { (cat, amount) ->
                                            val sweepAngle = ((amount / totalMonthExpense) * 360f).toFloat()
                                            val (_, color) = getCategoryIconAndColor(cat)

                                            drawArc(
                                                color = color,
                                                startAngle = startAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                            )
                                            startAngle += sweepAngle
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = FinanceUtils.formatCurrency(totalMonthExpense),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Total Spent",
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Category Legend
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoryTotals.take(5).forEach { (cat, amount) ->
                                        val percent = ((amount / totalMonthExpense) * 100).toInt()
                                        val (_, color) = getCategoryIconAndColor(cat)

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
                                                        .background(color)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = cat,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextPrimary
                                                )
                                            }
                                            Text(
                                                text = "$percent%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
