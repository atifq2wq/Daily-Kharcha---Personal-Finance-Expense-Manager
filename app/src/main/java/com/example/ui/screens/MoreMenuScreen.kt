package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.DailyKharchaHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MintGreen
import com.example.ui.theme.ShoppingAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UdhaarIndigo
import com.example.ui.util.FinanceUtils

@Composable
fun MoreMenuScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateToUdhaar: () -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToVehicle: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val shoppingItems by viewModel.allShoppingItems.collectAsState()
    val fuelRecords by viewModel.allFuelRecords.collectAsState()
    val bills by viewModel.allBills.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        DailyKharchaHeader(
            title = "More Features",
            subtitle = "Smart • Simple • Useful",
            onProfileClick = onNavigateToSettings
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Profile Mini Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSettings() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AK",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreen
                                )
                            }
                            Column {
                                Text(
                                    text = "Atif Khan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "atif@gmail.com",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = MintGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Offline DB • Sync Ready",
                                        fontSize = 10.sp,
                                        color = MintGreen
                                    )
                                }
                            }
                        }
                        Text("Edit >", fontSize = 12.sp, color = MintGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Financial Modules Grid / List
            item {
                Text(
                    text = "FINANCIAL MANAGEMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.6.sp
                )
            }

            item {
                MoreFeatureItem(
                    title = "Udhaar Management (Khata)",
                    subtitle = "Lend & borrow ledger • Receivable: ${FinanceUtils.formatCurrency(metrics.udhaarReceivable)}",
                    icon = Icons.Default.Group,
                    iconBg = Color(0xFFE0E7FF),
                    iconColor = UdhaarIndigo,
                    badge = "${metrics.activeUdhaarContactsCount} Active",
                    onClick = onNavigateToUdhaar,
                    testTag = "more_nav_udhaar"
                )
            }

            item {
                MoreFeatureItem(
                    title = "Bills & Reminders",
                    subtitle = "Track utility dues • Pending: ${FinanceUtils.formatCurrency(metrics.unpaidBillsTotal)}",
                    icon = Icons.Default.Receipt,
                    iconBg = Color(0xFFEDE9FE),
                    iconColor = Color(0xFF8B5CF6),
                    badge = "${bills.count { !it.isPaid }} Due",
                    onClick = onNavigateToBills,
                    testTag = "more_nav_bills"
                )
            }

            item {
                MoreFeatureItem(
                    title = "Shopping Checklist",
                    subtitle = "Smart grocery list with estimated costs • ${shoppingItems.size} items",
                    icon = Icons.Default.ShoppingCart,
                    iconBg = Color(0xFFFEF3C7),
                    iconColor = ShoppingAmber,
                    badge = "${shoppingItems.count { !it.isPurchased }} Remaining",
                    onClick = onNavigateToShopping,
                    testTag = "more_nav_shopping"
                )
            }

            item {
                MoreFeatureItem(
                    title = "Vehicle & Fuel Tracker",
                    subtitle = "Honda Civic (LEA-1234) • ${fuelRecords.size} fuel entries",
                    icon = Icons.Default.DirectionsCar,
                    iconBg = Color(0xFFE0F2FE),
                    iconColor = Color(0xFF0284C7),
                    badge = "LEA-1234",
                    onClick = onNavigateToVehicle,
                    testTag = "more_nav_vehicle"
                )
            }

            item {
                MoreFeatureItem(
                    title = "Reports & Analytics",
                    subtitle = "Charts, spending breakdown & monthly trends",
                    icon = Icons.Default.BarChart,
                    iconBg = Color(0xFFECFDF5),
                    iconColor = EmeraldPrimary,
                    badge = "Analytics",
                    onClick = onNavigateToReports,
                    testTag = "more_nav_reports"
                )
            }

            item {
                MoreFeatureItem(
                    title = "App Settings & Security",
                    subtitle = "Language, Dark Mode, Backup, Biometrics & PIN",
                    icon = Icons.Default.Settings,
                    iconBg = Color(0xFFF1F5F9),
                    iconColor = TextSecondary,
                    badge = "Settings",
                    onClick = onNavigateToSettings,
                    testTag = "more_nav_settings"
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MoreFeatureItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    badge: String?,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(">", fontSize = 14.sp, color = TextMuted)
            }
        }
    }
}
