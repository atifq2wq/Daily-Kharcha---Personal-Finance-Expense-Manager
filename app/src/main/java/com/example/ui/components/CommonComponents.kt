package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintLight

@Composable
fun DailyKharchaHeader(
    title: String = "Daily Kharcha",
    subtitle: String = "Smart • Simple • Useful",
    isUrdu: Boolean = false,
    onLanguageToggle: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = EmeraldDark,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(MintGreen, EmeraldMedium))
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Wallet Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PKR",
                                color = MintLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right: Language, Notification, Profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Urdu / English Switcher Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onLanguageToggle() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("lang_toggle_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "UR",
                            color = if (isUrdu) MintGreen else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (isUrdu) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = "|",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "EN",
                            color = if (!isUrdu) MintGreen else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (!isUrdu) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Notification Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                modifier = Modifier.size(6.dp),
                                containerColor = Color(0xFFEF4444)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Profile Avatar "AK"
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldMedium)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { onProfileClick() }
                        .testTag("profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AK",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    onBackClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

fun getCategoryIconAndColor(category: String, isExpense: Boolean = true): Pair<ImageVector, Color> {
    return when (category.lowercase()) {
        "food", "food & dining" -> Pair(Icons.Default.Restaurant, Color(0xFFEF4444))
        "grocery" -> Pair(Icons.Default.ShoppingCart, Color(0xFFF97316))
        "transport", "transport & fuel" -> Pair(Icons.Default.DirectionsCar, Color(0xFF3B82F6))
        "fuel" -> Pair(Icons.Default.LocalGasStation, Color(0xFF06B6D4))
        "shopping", "shopping & personal" -> Pair(Icons.Default.ShoppingBag, Color(0xFFA855F7))
        "bills", "bills & utilities", "utilities & bills" -> Pair(Icons.Default.Lightbulb, Color(0xFFEAB308))
        "mobile" -> Pair(Icons.Default.Smartphone, Color(0xFF10B981))
        "internet" -> Pair(Icons.Default.Wifi, Color(0xFF0284C7))
        "education" -> Pair(Icons.Default.School, Color(0xFF6366F1))
        "health" -> Pair(Icons.Default.Favorite, Color(0xFFEC4899))
        "fun", "entertainment" -> Pair(Icons.Default.SportsEsports, Color(0xFF8B5CF6))
        "salary" -> Pair(Icons.Default.Work, Color(0xFF16A34A))
        "business" -> Pair(Icons.Default.Receipt, Color(0xFF0D9488))
        "freelance" -> Pair(Icons.Default.Work, Color(0xFF059669))
        "online" -> Pair(Icons.Default.Wifi, Color(0xFF2563EB))
        "gift" -> Pair(Icons.Default.Favorite, Color(0xFFF59E0B))
        "udhaar", "udhaar return" -> Pair(Icons.Default.Person, Color(0xFF6366F1))
        else -> Pair(Icons.Default.Category, if (isExpense) Color(0xFF64748B) else Color(0xFF10B981))
    }
}
