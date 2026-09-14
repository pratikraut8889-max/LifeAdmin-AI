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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExtractedItemEntity
import com.example.data.model.ItemType
import com.example.ui.components.ItemCard
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: LifeAdminViewModel
) {
    val items by viewModel.activeItems.collectAsState()

    val overdueItems = items.filter { it.dueDateString?.contains("overdue", ignoreCase = true) == true }
    val todayItems = items.filter { it.dueDateString?.contains("today", ignoreCase = true) == true }
    val tomorrowItems = items.filter { it.dueDateString?.contains("tomorrow", ignoreCase = true) == true }
    val thisWeekItems = items.filter {
        val s = it.dueDateString?.lowercase() ?: ""
        s.contains("friday") || s.contains("thursday") || s.contains("weekend") || s.contains("days") || s.contains("sep 18") || s.contains("september 18")
    }
    val upcomingItems = items.filter {
        it !in overdueItems && it !in todayItems && it !in tomorrowItems && it !in thisWeekItems
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue600.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timeline,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Life Timeline",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tasks, appointments, renewals & deadlines",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.DASHBOARD) },
                        modifier = Modifier.testTag("btn_back_dashboard")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (overdueItems.isNotEmpty()) {
                item {
                    TimelineSectionHeader(title = "OVERDUE", color = Color(0xFFE11D48), count = overdueItems.size)
                }
                items(overdueItems.size) { index ->
                    val item = overdueItems[index]
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            if (todayItems.isNotEmpty()) {
                item {
                    TimelineSectionHeader(title = "TODAY", color = RoyalBlue600, count = todayItems.size)
                }
                items(todayItems.size) { index ->
                    val item = todayItems[index]
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            if (tomorrowItems.isNotEmpty()) {
                item {
                    TimelineSectionHeader(title = "TOMORROW", color = Color(0xFFD97706), count = tomorrowItems.size)
                }
                items(tomorrowItems.size) { index ->
                    val item = tomorrowItems[index]
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            if (thisWeekItems.isNotEmpty()) {
                item {
                    TimelineSectionHeader(title = "THIS WEEK", color = Color(0xFF6366F1), count = thisWeekItems.size)
                }
                items(thisWeekItems.size) { index ->
                    val item = thisWeekItems[index]
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }

            if (upcomingItems.isNotEmpty()) {
                item {
                    TimelineSectionHeader(title = "NEXT WEEK & UPCOMING", color = Color(0xFF475569), count = upcomingItems.size)
                }
                items(upcomingItems.size) { index ->
                    val item = upcomingItems[index]
                    ItemCard(
                        item = item,
                        onToggleComplete = { viewModel.toggleItemCompletion(item) },
                        onClick = { viewModel.selectItemForDetail(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineSectionHeader(
    title: String,
    color: Color,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
            color = color.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
