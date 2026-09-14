package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.local.ExtractedItemEntity
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.CustomBottomNav
import com.example.ui.components.ItemCard
import com.example.ui.components.StatProgressHeader
import com.example.ui.components.VoiceBriefingCard
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: LifeAdminViewModel
) {
    val items by viewModel.filteredActiveItems.collectAsState()
    val allItems by viewModel.activeItems.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val isSpeakingAudio by viewModel.isSpeakingAudioBrief.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val productivityScore by viewModel.productivityScore.collectAsState()

    val topPriorities = allItems.filter { !it.isCompleted && (it.isTopPriority || it.priority == ItemPriority.HIGH) }
    val upcomingBills = allItems.filter { !it.isCompleted && it.type == ItemType.BILL }
    val completedCount = allItems.count { it.isCompleted }

    val dailyBriefingSummary = "You have ${upcomingBills.size} upcoming bills due, ${topPriorities.size} high-priority actions today, and 5 days active streak."

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "LIFEADMIN AI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue600,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Monday, June 12",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.PROFILE) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_top_profile")
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.NOTIFICATIONS) },
                        modifier = Modifier.testTag("btn_top_notifications")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            CustomBottomNav(
                currentRoute = ScreenRoute.DASHBOARD,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // AI Voice Briefing Card (Morning Briefing)
            item {
                VoiceBriefingCard(
                    summaryText = dailyBriefingSummary,
                    isPlaying = isSpeakingAudio,
                    onTogglePlay = { viewModel.toggleVoiceBriefingPlayback(dailyBriefingSummary) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Stat Header
            item {
                StatProgressHeader(
                    totalItems = allItems.size,
                    completedItems = completedCount,
                    streakDays = streakDays,
                    productivityScore = productivityScore,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Action Pipeline Cards (Horizontal Cards from Design)
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "ACTION PIPELINE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            PipelineCard(
                                title = "Electric Bill",
                                subtitle = "$84.20 Due June 14",
                                icon = Icons.Filled.Payments,
                                containerColor = Color(0xFFFEE2E2),
                                contentColor = Color(0xFF991B1B),
                                iconColor = Color(0xFFDC2626),
                                borderColor = Color(0xFFFCA5A5)
                            )
                        }
                        item {
                            PipelineCard(
                                title = "Dentist Appt",
                                subtitle = "3:00 PM • Main St",
                                icon = Icons.Filled.CalendarToday,
                                containerColor = Color(0xFFFEF3C7),
                                contentColor = Color(0xFF92400E),
                                iconColor = Color(0xFFD97706),
                                borderColor = Color(0xFFFDE68A)
                            )
                        }
                        item {
                            PipelineCard(
                                title = "Review Rent Invoice",
                                subtitle = "Scanned from Gmail",
                                icon = Icons.Filled.ReceiptLong,
                                containerColor = Color(0xFFDBEAFE),
                                contentColor = Color(0xFF1E40AF),
                                iconColor = Color(0xFF2563EB),
                                borderColor = Color(0xFFBFDBFE)
                            )
                        }
                    }
                }
            }

            // Quick Input Actions Grid
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK INPUT CAPTURE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            title = "Paste Email/Chat",
                            icon = Icons.Filled.ContentPaste,
                            color = RoyalBlue600,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(ScreenRoute.SCAN_UPLOAD) }
                        )
                        QuickActionButton(
                            title = "Scan Bill / Doc",
                            icon = Icons.Filled.ReceiptLong,
                            color = Cyan500,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(ScreenRoute.SCAN_UPLOAD) }
                        )
                        QuickActionButton(
                            title = "Voice Input",
                            icon = Icons.Filled.Mic,
                            color = Emerald500,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(ScreenRoute.SCAN_UPLOAD) }
                        )
                    }
                }
            }

            // Category Filter Row
            item {
                CategoryChipRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.setCategoryFilter(it) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Top Priorities Section Title
            if (topPriorities.isNotEmpty() && selectedCategory == null) {
                item {
                    Text(
                        text = "Top Priorities Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(topPriorities, key = { "top_${it.id}" }) { priorityItem ->
                    ItemCard(
                        item = priorityItem,
                        onToggleComplete = { viewModel.toggleItemCompletion(priorityItem) },
                        onClick = { viewModel.selectItemForDetail(priorityItem) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // All Actions Section Title
            item {
                Text(
                    text = if (selectedCategory == null) "All Daily Actions" else "${selectedCategory?.label} Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                )
            }

            // List of Filtered Active Items
            if (items.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(items, key = { "item_${it.id}" }) { itemEntity ->
                    ItemCard(
                        item = itemEntity,
                        onToggleComplete = { viewModel.toggleItemCompletion(itemEntity) },
                        onClick = { viewModel.selectItemForDetail(itemEntity) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PipelineCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconColor: Color,
    borderColor: Color
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Empty",
                tint = RoyalBlue600,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No active actions in this category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Forward or scan a bill, email, or screenshot to add tasks automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
