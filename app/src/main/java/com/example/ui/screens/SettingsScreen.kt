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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewModule
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DailyKharchaViewModel
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: DailyKharchaViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.language.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        ScreenHeader(
            title = "Settings",
            onBackClick = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // User Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                                    .clip(CircleShape)
                                    .background(Color(0xFFEBF7F1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Atif Khan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "atif@gmail.com",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                        Text(">", fontSize = 14.sp, color = TextMuted)
                    }
                }
            }

            // Settings List Container
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        SettingsRowItem(
                            icon = Icons.Default.Language,
                            title = "Language",
                            value = if (currentLang == "EN") "English / اردو" else "اردو / English",
                            onClick = {
                                viewModel.toggleLanguage()
                                Toast.makeText(context, "Language toggled to ${if (currentLang == "EN") "Urdu" else "English"}", Toast.LENGTH_SHORT).show()
                            }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Palette,
                            title = "Theme",
                            value = "System Default",
                            onClick = { Toast.makeText(context, "Material 3 Emerald Theme Active", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Payments,
                            title = "Currency",
                            value = "PKR (Rs.)",
                            onClick = { Toast.makeText(context, "Currency set to Pakistani Rupee (PKR)", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            value = "Daily Reminders On",
                            onClick = { Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Lock,
                            title = "Security",
                            value = "PIN & Biometric",
                            onClick = { Toast.makeText(context, "Biometric security active", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.CloudDownload,
                            title = "Backup & Restore",
                            value = "Local DB Active",
                            onClick = { Toast.makeText(context, "Data stored securely offline in Room database", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.FileUpload,
                            title = "Export / Import",
                            value = "Excel / PDF",
                            onClick = { Toast.makeText(context, "Export ready in Excel / PDF format", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.ViewModule,
                            title = "Manage Categories",
                            value = null,
                            onClick = { Toast.makeText(context, "12 Default + Custom categories enabled", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Payments,
                            title = "Payment Methods",
                            value = "Cash, Bank, EasyPaisa...",
                            onClick = { Toast.makeText(context, "All payment methods enabled", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Star,
                            title = "Ad Preferences",
                            value = null,
                            onClick = { Toast.makeText(context, "Ad-free experience active", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Info,
                            title = "About",
                            value = "v1.0 (Zero Accounting)",
                            onClick = { Toast.makeText(context, "Daily Kharcha v1.0 • Pure Zero-start Financial Accounting", Toast.LENGTH_LONG).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            value = null,
                            onClick = { Toast.makeText(context, "100% offline & local data privacy", Toast.LENGTH_SHORT).show() }
                        )
                        SettingsDivider()
                        SettingsRowItem(
                            icon = Icons.Default.Description,
                            title = "Terms & Conditions",
                            value = null,
                            onClick = { Toast.makeText(context, "Daily Kharcha terms applied", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    value: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text(">", fontSize = 13.sp, color = TextMuted)
        }
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF1F5F9))
    )
}
