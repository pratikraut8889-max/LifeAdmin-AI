package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentPaste
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

    val samplePassportText = """
        From: National Passport Processing Center <appointments@travel.state.gov>
        Subject: Confirmation: Passport Biometric Appointment #DS-82-9910
        Dear Alex,
        Your appointment is scheduled for September 22 at 11:00 AM at Regional Passport Agency, Floor 3.
        MANDATORY DOCUMENTS TO BRING:
        1. Form DS-82 printed & signed
        2. Current expiring passport
        3. Two compliant 2x2 color passport photos
        4. Application fee ($130 certified check or money order)
        Failure to bring these documents will require rescheduling.
    """.trimIndent()

    val sampleElectricBillText = """
        STATE ELECTRICITY BOARD
        Monthly Residential Electric Bill Notice
        Consumer Account: ACC-449210
        Amount Payable: ₹1,240.00
        Due Date: September 18, 2026
        Prompt payment avoids late surcharge of ₹85.00.
        Pay online at ebportal.gov or mobile banking.
    """.trimIndent()

    val sampleInsuranceText = """
        GEICO AUTO INSURANCE
        Policy Renewal Notice #POL-88392-CA
        Vehicle: 2023 Honda Civic Sedan
        Your current policy term expires tomorrow at 11:59 PM.
        To maintain continuous legal coverage without interruption, renew online today.
        Renewal Premium: $118.00 / month.
    """.trimIndent()

    val sampleAmazonText = """
        Amazon Order Confirmation #114-8923184
        Item: Sony WH-1000XM5 Wireless Headphones
        Order Date: August 28, 2026
        Return Window Status: Eligible for return or replacement through September 28, 2026.
        Manufacturer Warranty: 1-Year Limited Warranty active through August 2027.
    """.trimIndent()

    val sampleTravelText = """
        American Airlines Booking Confirmation: W7K9LQ
        Passenger: Alex Morgan
        Flight AA 1420 to Chicago O'Hare (ORD)
        Departure: September 24 at 8:00 AM from Terminal 2
        Online Check-in opens 24 hours before flight.
        Carry-on baggage: 1 personal item + 1 carry-on suitcase.
    """.trimIndent()

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
                                text = "Life Inbox",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Turn messy information into structured actions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                .background(Color(0xFFF8FAFC))
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
                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = "Scan", modifier = Modifier.size(16.dp)) },
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

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedTab) {
                0 -> {
                    // Paste Text Mode
                    Text(
                        text = "Paste Email, Receipt, Notice, or Letter",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Paste messy email content, forwarded messages, bill statements, or appointment notices...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .testTag("input_paste_text"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TAP A LIFE SCENARIO TEMPLATE:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue600,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SampleChip(
                            label = "Passport Appointment Notice",
                            onClick = { textInput = samplePassportText },
                            modifier = Modifier.weight(1f)
                        )
                        SampleChip(
                            label = "Electricity Bill (₹1,240)",
                            onClick = { textInput = sampleElectricBillText },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SampleChip(
                            label = "Car Insurance Expiration",
                            onClick = { textInput = sampleInsuranceText },
                            modifier = Modifier.weight(1f)
                        )
                        SampleChip(
                            label = "Amazon Receipt & Warranty",
                            onClick = { textInput = sampleAmazonText },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SampleChip(
                        label = "Flight Booking Itinerary (AA 1420)",
                        onClick = { textInput = sampleTravelText },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val targetText = textInput.ifBlank { samplePassportText }
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
                        Text("Extract Obligations & Memory", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                1, 2 -> {
                    // Screenshot Upload / Camera Scan Mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val dummyBitmap = createSampleDocumentBitmap()
                                viewModel.processImageUpload(dummyBitmap)
                            }
                            .testTag("upload_image_area"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = RoyalBlue600.copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.PhotoLibrary else Icons.Filled.CameraAlt,
                                        contentDescription = "Upload",
                                        tint = RoyalBlue600,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedTab == 1) "Tap to select screenshot or bill photo" else "Tap to scan paper bill / letter",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Supports JPG, PNG, PDF receipts, policies and invoices",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val dummyBitmap = createSampleDocumentBitmap()
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
                        Text("Analyze Document Image", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                3 -> {
                    // Voice Input Mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                Box(modifier = Modifier.padding(18.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = "Dictate",
                                        tint = Emerald500,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Speak your note or obligation details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "e.g., 'Pay electricity bill ₹1,240 by September 18 and renew car insurance'",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.processPastedText(
                                "Voice Note: Need to pay electricity bill ₹1,240 by September 18 and complete passport photo."
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_process_voice"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Process Voice")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Process Voice Dictation", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}

private fun createSampleDocumentBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = AndroidColor.WHITE
    canvas.drawRect(0f, 0f, 400f, 300f, paint)

    paint.color = AndroidColor.BLACK
    paint.textSize = 20f
    canvas.drawText("OFFICIAL STATEMENT & NOTICE", 20f, 40f, paint)

    paint.textSize = 14f
    paint.color = AndroidColor.DKGRAY
    canvas.drawText("State Power Board - Account #884102", 20f, 80f, paint)
    canvas.drawText("Due Date: September 18, 2026", 20f, 110f, paint)
    canvas.drawText("Total Due: ₹1,240.00", 20f, 140f, paint)
    canvas.drawText("Please retain receipt for your records.", 20f, 170f, paint)

    return bitmap
}
