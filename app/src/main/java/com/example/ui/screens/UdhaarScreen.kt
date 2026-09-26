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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import com.example.data.local.UdhaarEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.FinanceUtils

@Composable
fun UdhaarScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val udhaars by viewModel.allUdhaars.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()

    var activeTab by remember { mutableStateOf("TO_RECEIVE") } // "TO_RECEIVE" or "TO_PAY"
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog input states
    var newPersonName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }
    var newNotes by remember { mutableStateOf("") }

    val filteredList = udhaars.filter {
        val matchesTab = it.type.equals(activeTab, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                it.personName.contains(searchQuery, ignoreCase = true) ||
                it.notes.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = "Udhaar Ledger",
            onBackClick = onNavigateBack,
            trailingContent = {
                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("add_udhaar_header_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Udhaar", tint = EmeraldPrimary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Segmented Control (To Receive / To Pay)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == "TO_RECEIVE") EmeraldPrimary else Color.Transparent)
                            .clickable { activeTab = "TO_RECEIVE" }
                            .padding(vertical = 10.dp)
                            .testTag("tab_to_receive"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "To Receive (لینا ہے)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == "TO_RECEIVE") Color.White else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == "TO_PAY") ExpenseRed else Color.Transparent)
                            .clickable { activeTab = "TO_PAY" }
                            .padding(vertical = 10.dp)
                            .testTag("tab_to_pay"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "To Pay (دینا ہے)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == "TO_PAY") Color.White else TextSecondary
                        )
                    }
                }
            }

            // Dual Flow Split Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Receivable
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total Receivable", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = FinanceUtils.formatCurrency(metrics.udhaarReceivable),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = IncomeGreen
                            )
                        }
                    }

                    // Total Payable
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total Payable", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = FinanceUtils.formatCurrency(metrics.udhaarPayable),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search person, phone, note...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = EmeraldMedium,
                        unfocusedIndicatorColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )
            }

            // Active Ledgers Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (activeTab == "TO_RECEIVE") "Receivable Ledgers" else "Payable Ledgers",
                            fontSize = 15.sp,
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
                                text = "${filteredList.size} Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Ledger List
            if (filteredList.isEmpty()) {
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
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (activeTab == "TO_RECEIVE") "No receivables recorded" else "No payables recorded",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Keep track of lent or borrowed money with auto balances.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+ Add Entry")
                            }
                        }
                    }
                }
            } else {
                items(filteredList) { udhaar ->
                    val pendingAmount = (udhaar.amount - udhaar.settledAmount).coerceAtLeast(0.0)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Tactile Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (activeTab == "TO_RECEIVE") IncomeGreenLight else ExpenseRedLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = udhaar.personName.firstOrNull()?.uppercase() ?: "U",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (activeTab == "TO_RECEIVE") IncomeGreen else ExpenseRed
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = udhaar.personName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Event,
                                                contentDescription = null,
                                                tint = TextMuted,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Due: ${FinanceUtils.formatDate(udhaar.dueDateMillis)}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (activeTab == "TO_RECEIVE") "+ ${FinanceUtils.formatCurrency(pendingAmount)}" else "- ${FinanceUtils.formatCurrency(pendingAmount)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeTab == "TO_RECEIVE") IncomeGreen else ExpenseRed
                                    )
                                    Text(
                                        text = if (udhaar.isSettled) "Settled" else "Pending",
                                        fontSize = 10.sp,
                                        color = if (udhaar.isSettled) IncomeGreen else TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Footer Strip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "WhatsApp reminder prepared for ${udhaar.personName}", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = null,
                                            tint = IncomeGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remind", fontSize = 11.sp, color = TextPrimary)
                                    }

                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Calling ${udhaar.personName}...", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF1F5F9))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.markUdhaarSettled(udhaar)
                                            Toast.makeText(context, "Marked as settled!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (activeTab == "TO_RECEIVE") IncomeGreen else ExpenseRed
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (activeTab == "TO_RECEIVE") "Receive" else "Pay",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteUdhaar(udhaar) },
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
            }

            // Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Auto SMS & WhatsApp Receipts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Free 1-tap ledger statements in Urdu & English keep both parties stress-free.",
                                fontSize = 11.sp,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }
                }
            }

            // Primary Sticky Button
            item {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_udhaar_entry_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "+ Add Udhaar Entry", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add Udhaar Dialog
    if (showAddDialog) {
        var dialogType by remember { mutableStateOf(activeTab) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Udhaar Record") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dialogType == "TO_RECEIVE") IncomeGreen else Color.Transparent)
                                .clickable { dialogType = "TO_RECEIVE" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("To Receive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (dialogType == "TO_RECEIVE") Color.White else TextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (dialogType == "TO_PAY") ExpenseRed else Color.Transparent)
                                .clickable { dialogType = "TO_PAY" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("To Pay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (dialogType == "TO_PAY") Color.White else TextSecondary)
                        }
                    }

                    OutlinedTextField(
                        value = newPersonName,
                        onValueChange = { newPersonName = it },
                        label = { Text("Person Name") },
                        placeholder = { Text("e.g. Ali Khan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Phone Number (Optional)") },
                        placeholder = { Text("e.g. 0300-1234567") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newAmount,
                        onValueChange = { newAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount (PKR)") },
                        placeholder = { Text("e.g. 10000") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("e.g. Due next month") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newAmount.toDoubleOrNull()
                        if (newPersonName.isNotBlank() && amount != null && amount > 0) {
                            val due = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000) // 7 days default
                            viewModel.addUdhaar(
                                type = dialogType,
                                personName = newPersonName.trim(),
                                phone = newPhone.trim(),
                                amount = amount,
                                dueDateMillis = due,
                                notes = newNotes.trim()
                            )
                            showAddDialog = false
                            newPersonName = ""
                            newPhone = ""
                            newAmount = ""
                            newNotes = ""
                            Toast.makeText(context, "Udhaar entry added!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
