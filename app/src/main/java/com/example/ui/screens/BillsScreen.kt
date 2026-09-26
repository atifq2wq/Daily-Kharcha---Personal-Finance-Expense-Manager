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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BillEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.ShoppingAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.FinanceUtils

@Composable
fun BillsScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val bills by viewModel.allBills.collectAsState()
    var selectedFilter by remember { mutableStateOf("Upcoming") } // "Upcoming" or "All"
    var showAddBillDialog by remember { mutableStateOf(false) }

    var billTitle by remember { mutableStateOf("") }
    var billAmount by remember { mutableStateOf("") }
    var billCategory by remember { mutableStateOf("Electricity") }

    val displayedBills = if (selectedFilter == "Upcoming") {
        bills.filter { !it.isPaid }
    } else {
        bills
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = "Bills & Reminders",
            onBackClick = onNavigateBack,
            trailingContent = {
                IconButton(
                    onClick = { showAddBillDialog = true },
                    modifier = Modifier.testTag("add_bill_header_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Bill", tint = EmeraldPrimary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Segmented Filter: Upcoming / All Bills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(3.dp)
                ) {
                    listOf("Upcoming", "All Bills").forEach { tab ->
                        val isSelected = selectedFilter == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                                .clickable { selectedFilter = tab }
                                .padding(vertical = 8.dp)
                                .testTag("bills_tab_${tab.lowercase().replace(" ", "_")}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            if (displayedBills.isEmpty()) {
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
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No bills found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Keep track of electricity, rent, internet & utilities.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddBillDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+ Add Bill")
                            }
                        }
                    }
                }
            } else {
                items(displayedBills) { bill ->
                    val (icon, color) = when (bill.title.lowercase()) {
                        "electricity", "lesco electricity" -> Pair(Icons.Default.Lightbulb, ShoppingAmber)
                        "internet", "fiber internet", "ptcl" -> Pair(Icons.Default.Wifi, Color(0xFF0284C7))
                        "mobile" -> Pair(Icons.Default.Smartphone, Color(0xFFF97316))
                        "rent", "house rent" -> Pair(Icons.Default.Home, IncomeGreen)
                        "gas" -> Pair(Icons.Default.LocalFireDepartment, Color(0xFFEA580C))
                        else -> Pair(Icons.Default.Receipt, EmeraldPrimary)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
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
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(color.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = bill.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = FinanceUtils.formatCurrency(bill.amount),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Due: ${FinanceUtils.formatDate(bill.dueDateMillis)} • ${bill.frequency}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (bill.isPaid) IncomeGreen else ExpenseRed)
                                        .clickable {
                                            viewModel.toggleBillPaid(bill)
                                            Toast.makeText(
                                                context,
                                                if (!bill.isPaid) "Marked as Paid!" else "Marked as Pending",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (bill.isPaid) "Paid" else "Due",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteBill(bill) },
                                    modifier = Modifier.size(32.dp)
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
                    }
                }
            }

            // Bottom Add Bill Action CTA
            item {
                Button(
                    onClick = { showAddBillDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_bill_bottom_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Bill", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddBillDialog) {
        val quickTitles = listOf("Electricity", "Internet", "Mobile", "Rent", "Gas", "Water")

        AlertDialog(
            onDismissRequest = { showAddBillDialog = false },
            title = { Text("Add Bill Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select or Type Title:", fontSize = 12.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickTitles.take(3).forEach { title ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (billTitle == title) EmeraldPrimary else Color(0xFFF1F5F9))
                                    .clickable { billTitle = title }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    title,
                                    fontSize = 11.sp,
                                    color = if (billTitle == title) Color.White else TextPrimary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = billTitle,
                        onValueChange = { billTitle = it },
                        label = { Text("Bill Title") },
                        placeholder = { Text("e.g. Electricity") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = billAmount,
                        onValueChange = { billAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount (PKR)") },
                        placeholder = { Text("e.g. 3500") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = billAmount.toDoubleOrNull()
                        if (billTitle.isNotBlank() && amount != null && amount > 0) {
                            val dueDate = System.currentTimeMillis() + (5L * 24 * 60 * 60 * 1000)
                            viewModel.addBill(
                                title = billTitle.trim(),
                                amount = amount,
                                dueDateMillis = dueDate
                            )
                            showAddBillDialog = false
                            billTitle = ""
                            billAmount = ""
                            Toast.makeText(context, "Bill added!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBillDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
