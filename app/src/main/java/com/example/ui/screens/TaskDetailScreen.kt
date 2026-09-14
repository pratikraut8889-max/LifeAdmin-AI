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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.ui.components.CategoryBadge
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: LifeAdminViewModel
) {
    val selectedItem by viewModel.selectedItemForDetail.collectAsState()

    if (selectedItem == null) {
        viewModel.navigateTo(ScreenRoute.DASHBOARD)
        return
    }

    val item = selectedItem!!
    val prepChecklistStates = remember(item.id) { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Obligation Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.DASHBOARD) },
                        modifier = Modifier.testTag("btn_back_detail")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.deleteItem(item.id) },
                        modifier = Modifier.testTag("btn_delete_item")
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Crimson500)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("task_detail_screen")
        ) {
            // Category & Status Badges Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryBadge(category = item.category)
                Surface(
                    color = if (item.isCompleted) Emerald500.copy(alpha = 0.15f) else RoyalBlue600.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (item.isCompleted) "COMPLETED" else "ACTION REQUIRED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCompleted) Emerald500 else RoyalBlue600,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (item.priority == ItemPriority.HIGH && !item.isCompleted) {
                    Surface(
                        color = Crimson500.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "HIGH PRIORITY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Crimson500,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            if (!item.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
            }

            // Priority Reasoning Callout Card (Section 11)
            if (!item.priorityReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE4E6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Flag,
                            contentDescription = null,
                            tint = Crimson500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WHY THIS NEEDS ATTENTION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Crimson500,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.priorityReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF881337),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Amount Box if available
            if (item.amount != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Obligation Amount",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF64748B)
                            )
                            val formattedAmount = if (item.amount >= 1000.0) {
                                "₹${String.format(Locale.US, "%,.0f", item.amount)}"
                            } else {
                                "$${String.format(Locale.US, "%.2f", item.amount)}"
                            }
                            Text(
                                text = formattedAmount,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (item.type == ItemType.BILL) Crimson500 else Emerald500
                            )
                        }
                        Surface(
                            color = if (item.type == ItemType.BILL) Crimson500.copy(alpha = 0.12f) else Emerald500.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (item.type == ItemType.BILL) "Bill Payment" else "Purchase / Fee",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.type == ItemType.BILL) Crimson500 else Emerald500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Connected Document Memory Link (Section 18)
            if (item.linkedDocumentName != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(ScreenRoute.DOCUMENT_MEMORY) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalBlue600.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CONNECTED DOCUMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue600,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = item.linkedDocumentName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                        Text(
                            text = "View →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue600
                        )
                    }
                }
            }

            // Required Documents Checklist (Section 9: Appointments & Deadlines)
            if (!item.requiredDocuments.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REQUIRED PHYSICAL DOCUMENTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val docList = item.requiredDocuments.split(",", "\n").map { it.trim() }.filter { it.isNotBlank() }
                        docList.forEach { docName ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF7C3AED))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = docName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            // Preparation Checklist
            if (!item.preparationChecklist.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Emerald500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PREPARATION CHECKLIST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald500,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val steps = item.preparationChecklist.split("\n").filter { it.isNotBlank() }
                        steps.forEachIndexed { index, step ->
                            val isChecked = prepChecklistStates[index] ?: false
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { prepChecklistStates[index] = !isChecked }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (isChecked) Emerald500 else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) Emerald500 else Color(0xFFCBD5E1)),
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = step,
                                    fontSize = 13.sp,
                                    color = if (isChecked) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            // Purchase Administration (Return window & warranty) (Section 10)
            if (item.returnDeadlineString != null || item.warrantyExpiryString != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PURCHASE ADMINISTRATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (item.merchant != null) DetailInfoRow(Icons.Filled.Person, "Merchant", item.merchant)
                        if (item.productName != null) DetailInfoRow(Icons.Filled.ShoppingBag, "Product", item.productName)
                        if (item.orderNumber != null) DetailInfoRow(Icons.Filled.Description, "Order #", item.orderNumber)
                        if (item.returnDeadlineString != null) DetailInfoRow(Icons.Filled.Restore, "Return Window Closes", item.returnDeadlineString)
                        if (item.warrantyExpiryString != null) DetailInfoRow(Icons.Filled.Shield, "Warranty Active Until", item.warrantyExpiryString)
                    }
                }
            }

            // Waiting For Details (Section 6 & 13)
            if (item.isWaitingFor && !item.waitingForDetail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HourglassTop,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WAITING ON RESPONSE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.waitingForDetail,
                                fontSize = 12.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }

            // Key Info Card
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailInfoRow(
                        icon = Icons.Filled.Event,
                        label = "Due Date",
                        value = item.dueDateString ?: "Today"
                    )
                    if (item.companyOrPerson != null) {
                        DetailInfoRow(
                            icon = Icons.Filled.Person,
                            label = "Company / Contact",
                            value = item.companyOrPerson
                        )
                    }
                    if (item.location != null) {
                        DetailInfoRow(
                            icon = Icons.Filled.LocationOn,
                            label = "Location",
                            value = item.location
                        )
                    }
                    DetailInfoRow(
                        icon = Icons.Filled.Flag,
                        label = "Priority",
                        value = item.priority.label
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ask LifeAdmin shortcut button
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue600.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.askLifeAdmin("What do I need to prepare or complete for: ${item.title}?")
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = RoyalBlue600,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ask LifeAdmin about this obligation",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalBlue600
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions (Snooze / Archive)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.snoozeItem(item, "Tomorrow") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_snooze"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Snooze, contentDescription = "Snooze")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Snooze")
                }

                OutlinedButton(
                    onClick = { viewModel.archiveItem(item.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Archive, contentDescription = "Archive")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Archive")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Complete Button
            Button(
                onClick = {
                    viewModel.toggleItemCompletion(item)
                    viewModel.navigateTo(ScreenRoute.DASHBOARD)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_complete_action"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.isCompleted) RoyalBlue600 else Emerald500
                )
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Complete")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (item.isCompleted) "Mark as Incomplete" else "Mark Action Completed",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun DetailInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = RoyalBlue600,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
