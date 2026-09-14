package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanUploadScreen(
    viewModel: LifeAdminViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Paste, 1: Screenshot, 2: Camera, 3: Voice
    var textInput by remember { mutableStateOf("") }

    // Sample Email / Chat scenarios
    val sampleBillText = """
        From: billing@citypower.com
        Subject: Monthly Utility Bill Notice - Account #884920
        Hi Alex,
        Your electric utility statement for July is now ready. 
        Total Amount Due: $142.50
        Payment Due Date: August 12, 2026.
        Please pay via the portal or auto-pay to avoid late fees.
    """.trimIndent()

    val sampleChatText = """
        WhatsApp message from Dr. Marcus Office:
        "Hi Alex, confirming your dental checkup & X-ray for this Friday, Aug 8 at 3:30 PM. Location: Westside Health Plaza, Suite 102. Reply YES to confirm or call to reschedule."
    """.trimIndent()

    val sampleRentText = """
        From: management@apexapartments.com
        Subject: Notice: Lease Renewal & Monthly Rent Statement
        Dear Resident,
        Your monthly rent payment of $1,850.00 for Unit 4B is due on August 5, 2026.
        Please submit proof of renters insurance renewal before August 15.
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smart Capture & Extraction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.DASHBOARD) },
                        modifier = Modifier.testTag("btn_back_scan_upload")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("scan_upload_screen")
        ) {
            // Mode Segmented Buttons
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 4),
                    icon = { Icon(Icons.Filled.ContentPaste, contentDescription = "Paste", modifier = Modifier.size(16.dp)) },
                    label = { Text("Text", fontSize = 12.sp) }
                )
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 4),
                    icon = { Icon(Icons.Filled.PhotoLibrary, contentDescription = "Upload", modifier = Modifier.size(16.dp)) },
                    label = { Text("Photo", fontSize = 12.sp) }
                )
                SegmentedButton(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 4),
                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(16.dp)) },
                    label = { Text("Scan", fontSize = 12.sp) }
                )
                SegmentedButton(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    shape = SegmentedButtonDefaults.itemShape(index = 3, count = 4),
                    icon = { Icon(Icons.Filled.Mic, contentDescription = "Voice", modifier = Modifier.size(16.dp)) },
                    label = { Text("Voice", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> {
                    // Paste Text Mode
                    Text(
                        text = "Paste Email, Bill, Note, or Chat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Paste messy email content, chat text, or bills here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("input_paste_text"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Or tap a sample template:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Scenario Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SampleChip(
                            label = "Utility Bill Email",
                            onClick = { textInput = sampleBillText },
                            modifier = Modifier.weight(1f)
                        )
                        SampleChip(
                            label = "Doctor Chat",
                            onClick = { textInput = sampleChatText },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SampleChip(
                        label = "Rent & Lease Renewal Notice",
                        onClick = { textInput = sampleRentText },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val targetText = textInput.ifBlank { sampleBillText }
                            viewModel.processPastedText(targetText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_process_text"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "AI")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extract Actions with AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                1, 2 -> {
                    // Screenshot Upload / Camera Scan Mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val dummyBitmap = createDummyDocumentBitmap()
                                viewModel.processImageUpload(dummyBitmap)
                            }
                            .testTag("upload_image_area"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = RoyalBlue600.copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.PhotoLibrary else Icons.Filled.Camera,
                                        contentDescription = "Upload",
                                        tint = RoyalBlue600,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedTab == 1) "Tap to select screenshot or bill image" else "Tap to scan paper bill / letter",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Supports JPG, PNG, PDF receipts and letters",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val dummyBitmap = createDummyDocumentBitmap()
                            viewModel.processImageUpload(dummyBitmap)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_process_image"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "AI")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Document Image", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                3 -> {
                    // Voice Input Mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = Emerald500.copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Box(modifier = Modifier.padding(20.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = "Dictate",
                                        tint = Emerald500,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Speak your note or bill details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "e.g., 'Pay dentist $120 by next Tuesday and call insurance'",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.processPastedText("Voice Note: Pay dentist $120 statement due next Tuesday and confirm dental insurance claim.")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_process_voice"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Voice")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Process Dictated Voice Note", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SampleChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun createDummyDocumentBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 500, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.WHITE)

    val paint = Paint()
    paint.color = AndroidColor.BLACK
    paint.textSize = 24f
    paint.isAntiAlias = true

    canvas.drawText("ELECTRIC POWER STATEMENT", 30f, 60f, paint)
    paint.textSize = 18f
    canvas.drawText("Account #: 992-482", 30f, 100f, paint)
    canvas.drawText("Amount Due: $124.80", 30f, 140f, paint)
    canvas.drawText("Due Date: Aug 12, 2026", 30f, 180f, paint)
    canvas.drawText("Company: Metro Power & Light", 30f, 220f, paint)
    return bitmap
}
