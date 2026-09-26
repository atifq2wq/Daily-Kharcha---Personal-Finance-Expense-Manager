package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.ui.DailyKharchaViewModel
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.BillsScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreMenuScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.ShoppingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.UdhaarScreen
import com.example.ui.screens.VehicleScreen
import com.example.ui.theme.DailyKharchaTheme
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppScreen {
    HOME,
    TRANSACTIONS,
    BUDGET,
    REPORTS,
    MORE,
    ADD_TRANSACTION,
    UDHAAR,
    BILLS,
    SHOPPING,
    VEHICLE,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: DailyKharchaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DailyKharchaTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
                var screenHistory by remember { mutableStateOf(listOf<AppScreen>()) }
                var addTransactionIsExpense by remember { mutableStateOf(true) }
                var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

                fun navigateTo(screen: AppScreen) {
                    if (currentScreen != screen) {
                        screenHistory = screenHistory + currentScreen
                        currentScreen = screen
                    }
                }

                fun navigateBack() {
                    if (screenHistory.isNotEmpty()) {
                        val prev = screenHistory.last()
                        screenHistory = screenHistory.dropLast(1)
                        currentScreen = prev
                    } else if (currentScreen != AppScreen.HOME) {
                        currentScreen = AppScreen.HOME
                    }
                }

                // Handle back press
                BackHandler(enabled = currentScreen != AppScreen.HOME || screenHistory.isNotEmpty()) {
                    navigateBack()
                }

                val isMainTab = currentScreen in listOf(
                    AppScreen.HOME,
                    AppScreen.TRANSACTIONS,
                    AppScreen.BUDGET,
                    AppScreen.REPORTS,
                    AppScreen.MORE
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isMainTab) {
                            BottomNavigationBar(
                                currentScreen = currentScreen,
                                onTabSelected = { selectedTab ->
                                    currentScreen = selectedTab
                                    screenHistory = emptyList() // clear stack when switching main tabs
                                },
                                onCenterAddClick = {
                                    editingTransaction = null
                                    addTransactionIsExpense = true
                                    navigateTo(AppScreen.ADD_TRANSACTION)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToAddTransaction = { isExpense ->
                                        editingTransaction = null
                                        addTransactionIsExpense = isExpense
                                        navigateTo(AppScreen.ADD_TRANSACTION)
                                    },
                                    onNavigateToTransactions = { navigateTo(AppScreen.TRANSACTIONS) },
                                    onNavigateToBudget = { navigateTo(AppScreen.BUDGET) },
                                    onNavigateToUdhaar = { navigateTo(AppScreen.UDHAAR) },
                                    onNavigateToBills = { navigateTo(AppScreen.BILLS) },
                                    onNavigateToShopping = { navigateTo(AppScreen.SHOPPING) },
                                    onNavigateToSettings = { navigateTo(AppScreen.SETTINGS) }
                                )
                            }

                            AppScreen.TRANSACTIONS -> {
                                TransactionsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() },
                                    onAddTransaction = { isExpense ->
                                        editingTransaction = null
                                        addTransactionIsExpense = isExpense
                                        navigateTo(AppScreen.ADD_TRANSACTION)
                                    },
                                    onEditTransaction = { tx ->
                                        editingTransaction = tx
                                        addTransactionIsExpense = tx.type.equals("EXPENSE", ignoreCase = true)
                                        navigateTo(AppScreen.ADD_TRANSACTION)
                                    }
                                )
                            }

                            AppScreen.BUDGET -> {
                                BudgetScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.REPORTS -> {
                                ReportsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.MORE -> {
                                MoreMenuScreen(
                                    viewModel = viewModel,
                                    onNavigateToUdhaar = { navigateTo(AppScreen.UDHAAR) },
                                    onNavigateToBills = { navigateTo(AppScreen.BILLS) },
                                    onNavigateToShopping = { navigateTo(AppScreen.SHOPPING) },
                                    onNavigateToVehicle = { navigateTo(AppScreen.VEHICLE) },
                                    onNavigateToReports = { navigateTo(AppScreen.REPORTS) },
                                    onNavigateToSettings = { navigateTo(AppScreen.SETTINGS) }
                                )
                            }

                            AppScreen.ADD_TRANSACTION -> {
                                AddTransactionScreen(
                                    viewModel = viewModel,
                                    initialIsExpense = addTransactionIsExpense,
                                    editingTransaction = editingTransaction,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.UDHAAR -> {
                                UdhaarScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.BILLS -> {
                                BillsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.SHOPPING -> {
                                ShoppingScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.VEHICLE -> {
                                VehicleScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }

                            AppScreen.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navigateBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit,
    onCenterAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bottom_navigation_bar"),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Home
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onTabSelected(AppScreen.HOME) },
                testTag = "nav_tab_home"
            )

            // Transactions (Kharcha)
            BottomNavItem(
                icon = Icons.Default.ReceiptLong,
                label = "Kharcha",
                isSelected = currentScreen == AppScreen.TRANSACTIONS,
                onClick = { onTabSelected(AppScreen.TRANSACTIONS) },
                testTag = "nav_tab_transactions"
            )

            // Center Elevated Add (+) Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .offset(y = (-6).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF004D40), Color(0xFF10B981))
                        )
                    )
                    .clickable { onCenterAddClick() }
                    .border(3.dp, Color.White, CircleShape)
                    .shadow(6.dp, CircleShape)
                    .testTag("center_fab_add"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Budget
            BottomNavItem(
                icon = Icons.Default.PieChart,
                label = "Budget",
                isSelected = currentScreen == AppScreen.BUDGET,
                onClick = { onTabSelected(AppScreen.BUDGET) },
                testTag = "nav_tab_budget"
            )

            // More Hub
            BottomNavItem(
                icon = Icons.Default.MoreHoriz,
                label = "More",
                isSelected = currentScreen == AppScreen.MORE,
                onClick = { onTabSelected(AppScreen.MORE) },
                testTag = "nav_tab_more"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) EmeraldPrimary else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) EmeraldPrimary else TextMuted
        )
    }
}
