package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.ui.components.CustomBottomNav
import com.example.ui.components.ItemCard
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson500
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
    val documents by viewModel.scannedDocuments.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isSpeakingAudio by viewModel.isSpeakingAudioBrief.collectAsState()

    val unreadNotificationsCount = notifications.count { !it.isRead }

    // Section Filtering according to Product Scope
    val attentionNowItems = allItems.filter {
        !it.isCompleted && (it.priority == ItemPriority.HIGH || it.isTopPriority || it.dueDateString?.contains("tomorrow", ignoreCase = true) == true)
    }

    val waitingForItems = allItems.filter {
        !it.isCompleted && it.isWaitingFor
    }

    val renewalItems = allItems.filter {
        !it.isCompleted && (it.type == ItemType.RENEWAL || it.renewalDateString != null)
    }

    val appointmentItems = allItems.filter {
        !it.isCompleted && it.type == ItemType.APPOINTMENT
    }

    val purchaseItems = allItems.filter {
        !it.isCompleted && (it.type == ItemType.PURCHASE_ADMIN || it.returnDeadlineString != null)
    }

    val comingUpItems = allItems.filter {
        !it.isCompleted && it !in attentionNowItems && it !in waitingForItems
    }

    val dailyBriefingSummary = "You have ${attentionNowItems.size} items needing immediate attention today, including car insurance renewal and electricity bill."

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LIFEADMIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = RoyalBlue600.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "AI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalBlue600,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "What needs your attention?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    // Ask AI button
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.ASK_AI) },
                        modifier = Modifier.testTag("btn_top_ask_ai")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = "Ask LifeAdmin",
                            tint = RoyalBlue600
                        )
                    }

                    // Document Memory button
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.DOCUMENT_MEMORY) },
                        modifier = Modifier.testTag("btn_top_documents")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FolderShared,
                            contentDescription = "Document Memory",
                            tint = Color(0xFF475569)
                        )
                    }

                    // Notifications button with unread count
                    Box {
                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenRoute.NOTIFICATIONS) },
                            modifier = Modifier.testTag("btn_top_notifications")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF475569)
                            )
                        }
                        if (unreadNotificationsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Crimson500)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 12: "WHAT AM I FORGETTING?" Smart Assistant Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.askLifeAdmin("What am I forgetting?") }
                        .testTag("card_what_am_i_forgetting"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "What am I forgetting?",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap for instant AI analysis of your documents & upcoming deadlines",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = RoyalBlue600,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Ask AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Quick Category Shortcuts Strip
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        QuickActionPill(
                            label = "Timeline",
                            icon = Icons.Filled.Timeline,
                            count = allItems.count { !it.isCompleted },
                            onClick = { viewModel.navigateTo(ScreenRoute.TIMELINE) }
                        )
                    }
                    item {
                        QuickActionPill(
                            label = "Renewals",
                            icon = Icons.Filled.PublishedWithChanges,
                            count = renewalItems.size,
                            onClick = { viewModel.navigateTo(ScreenRoute.RENEWALS) }
                        )
                    }
                    item {
                        QuickActionPill(
                            label = "Workflows",
                            icon = Icons.Filled.Assignment,
                            count = 6,
                            onClick = { viewModel.navigateTo(ScreenRoute.WORKFLOWS) }
                        )
                    }
                    item {
                        QuickActionPill(
                            label = "Doc Memory",
                            icon = Icons.Filled.FolderShared,
                            count = documents.size,
                            onClick = { viewModel.navigateTo(ScreenRoute.DOCUMENT_MEMORY) }
                        )
                    }
                }
            }

            // SECTION 1: ATTENTION NOW (Critical obligations & urgent deadlines)
            if (attentionNowItems.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "ATTENTION NOW",
                        subtitle = "Critical obligations requiring decision or action",
                        badgeCount = attentionNowItems.size,
                        badgeColor = Crimson500
                    )
                }
                items(attentionNowItems, key = { "attention_${it.id}" }) { item ->
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            // SECTION 2: WAITING FOR (Delegated / Pending feedback)
            if (waitingForItems.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "WAITING FOR",
                        subtitle = "Pending responses from companies or agencies",
                        badgeCount = waitingForItems.size,
                        badgeColor = Amber500
                    )
                }
                items(waitingForItems, key = { "waiting_${it.id}" }) { item ->
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            // SECTION 3: COMING UP (Rest of active obligations)
            if (comingUpItems.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "COMING UP",
                        subtitle = "Scheduled tasks, appointments and purchases",
                        badgeCount = comingUpItems.size,
                        badgeColor = RoyalBlue600
                    )
                }
                items(comingUpItems, key = { "coming_${it.id}" }) { item ->
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    badgeCount: Int? = null,
    badgeColor: Color = RoyalBlue600
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                letterSpacing = 1.sp
            )

            if (badgeCount != null && badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B),
            fontSize = 11.sp
        )
    }
}

@Composable
fun QuickActionPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RoyalBlue600,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(5.dp))
                Surface(
                    color = RoyalBlue600.copy(alpha = 0.12f),
                    shape = CircleShape
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue600,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}
