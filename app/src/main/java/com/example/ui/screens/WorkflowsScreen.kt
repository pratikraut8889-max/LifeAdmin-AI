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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkflowType
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkflowsScreen(
    viewModel: LifeAdminViewModel
) {
    val workflows = listOf(
        WorkflowItemData(
            type = WorkflowType.PASSPORT_RENEWAL,
            icon = Icons.Filled.Gavel,
            steps = listOf(
                "Collect old passport & birth certificate",
                "Take 2x2 official color passport photos",
                "Book regional passport agency biometric appointment",
                "Prepare certified application fee money order ($130)"
            )
        ),
        WorkflowItemData(
            type = WorkflowType.VEHICLE_REGISTRATION,
            icon = Icons.Filled.DirectionsCar,
            steps = listOf(
                "Complete certified state emissions inspection",
                "Compare insurance quotes and renew vehicle policy",
                "Submit annual DMV vehicle registration fee ($185)",
                "Affix new license plate year validation sticker"
            )
        ),
        WorkflowItemData(
            type = WorkflowType.TRAVEL_PREPARATION,
            icon = Icons.Filled.Flight,
            steps = listOf(
                "Complete online flight check-in 24 hours prior",
                "Pack essential medications & chargers in carry-on",
                "Download mobile boarding pass & verify passport validity",
                "Set automated 2.5-hour leave-home departure alarm"
            )
        ),
        WorkflowItemData(
            type = WorkflowType.MOVING_HOUSE,
            icon = Icons.Filled.Home,
            steps = listOf(
                "Submit official 30-day written notice to landlord",
                "Submit USPS official mail forwarding address update",
                "Schedule electric & internet disconnection and transfer",
                "Update driver's license and bank accounts with new address"
            )
        ),
        WorkflowItemData(
            type = WorkflowType.RETURN_WARRANTY,
            icon = Icons.Filled.ShoppingBag,
            steps = listOf(
                "Verify merchant 30-day return eligibility window",
                "Locate original purchase invoice/receipt in document memory",
                "Generate prepaid courier return shipping label",
                "Drop packaged parcel at authorized drop-off center"
            )
        ),
        WorkflowItemData(
            type = WorkflowType.DOCTOR_CHECKUP,
            icon = Icons.Filled.LocalHospital,
            steps = listOf(
                "Locate recent lab test results and medical records",
                "Verify primary health insurance eligibility & copay",
                "Follow 8-hour fasting instructions if required",
                "Write list of current symptoms and prescription questions"
            )
        )
    )

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
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Life Workflows",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Automate complex personal administration",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(workflows, key = { it.type.name }) { item ->
                WorkflowCard(
                    data = item,
                    onLaunch = { viewModel.launchWorkflow(item.type) }
                )
            }
        }
    }
}

data class WorkflowItemData(
    val type: WorkflowType,
    val icon: ImageVector,
    val steps: List<String>
)

@Composable
fun WorkflowCard(
    data: WorkflowItemData,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workflow_card_${data.type.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RoyalBlue600.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        tint = RoyalBlue600,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.type.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = data.type.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Steps Breakdown
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "GENERATES ${data.steps.size} CONNECTED OBLIGATIONS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue600,
                        letterSpacing = 0.5.sp
                    )
                    data.steps.forEachIndexed { index, step ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF334155),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onLaunch,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_launch_${data.type.name}"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Launch This Workflow",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
