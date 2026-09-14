package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import com.example.data.local.AppDatabase
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.ScannedDocumentEntity
import com.example.data.model.ExtractionResponse
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.data.model.WorkflowType
import com.example.data.remote.GeminiExtractionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LifeAdminRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val itemDao = db.itemDao()
    private val documentDao = db.documentDao()
    private val notificationDao = db.notificationDao()
    private val geminiEngine = GeminiExtractionEngine()

    private var ttsEngine: TextToSpeech? = null
    private var isTtsReady = false

    init {
        // Initialize TTS for Voice Assistant experience
        ttsEngine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.US
                isTtsReady = true
            }
        }

        // Seed initial items if database is fresh
        CoroutineScope(Dispatchers.IO).launch {
            val count = itemDao.getAllActiveItems().first().size
            if (count == 0) {
                seedInitialData()
            }
        }
    }

    val activeItems: Flow<List<ExtractedItemEntity>> = itemDao.getAllActiveItems()
    val archivedItems: Flow<List<ExtractedItemEntity>> = itemDao.getArchivedItems()
    val scannedDocuments: Flow<List<ScannedDocumentEntity>> = documentDao.getAllDocuments()
    val notifications: Flow<List<NotificationLogEntity>> = notificationDao.getAllNotifications()
    val renewals: Flow<List<ExtractedItemEntity>> = itemDao.getRenewals()
    val appointments: Flow<List<ExtractedItemEntity>> = itemDao.getAppointments()
    val purchases: Flow<List<ExtractedItemEntity>> = itemDao.getPurchases()
    val waitingForItems: Flow<List<ExtractedItemEntity>> = itemDao.getWaitingForItems()

    suspend fun insertItem(item: ExtractedItemEntity): Long = withContext(Dispatchers.IO) {
        itemDao.insertItem(item)
    }

    suspend fun updateItem(item: ExtractedItemEntity) = withContext(Dispatchers.IO) {
        itemDao.updateItem(item)
    }

    suspend fun toggleCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        itemDao.updateCompletionStatus(id, isCompleted)
    }

    suspend fun archiveItem(id: Long) = withContext(Dispatchers.IO) {
        itemDao.archiveItem(id)
    }

    suspend fun deleteItem(id: Long) = withContext(Dispatchers.IO) {
        itemDao.deleteItem(id)
    }

    suspend fun deleteDocument(id: Long) = withContext(Dispatchers.IO) {
        documentDao.deleteDocument(id)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun processTextExtraction(rawText: String, sourceType: String = "EMAIL_TEXT"): ExtractionResponse = withContext(Dispatchers.IO) {
        val result = geminiEngine.extractFromText(rawText)
        saveExtractionResult(result, sourceType, rawText)
        result
    }

    suspend fun processImageExtraction(bitmap: Bitmap, sourceType: String = "SCREENSHOT"): ExtractionResponse = withContext(Dispatchers.IO) {
        val result = geminiEngine.extractFromImage(bitmap)
        saveExtractionResult(result, sourceType, "[Uploaded Screenshot / Photo]")
        result
    }

    suspend fun askLifeAdmin(question: String): String = withContext(Dispatchers.IO) {
        val items = itemDao.getAllActiveItems().first()
        val docs = documentDao.getAllDocuments().first()

        val contextBuilder = StringBuilder()
        contextBuilder.append("Active Obligations (${items.size}):\n")
        items.forEach { item ->
            contextBuilder.append("- Title: ${item.title} | Type: ${item.type} | Priority: ${item.priority} | Due: ${item.dueDateString ?: "None"}")
            if (item.amount != null) contextBuilder.append(" | Amount Due: ${item.amount}")
            if (item.companyOrPerson != null) contextBuilder.append(" | Company: ${item.companyOrPerson}")
            if (item.requiredDocuments != null) contextBuilder.append(" | Required Docs: ${item.requiredDocuments}")
            if (item.preparationChecklist != null) contextBuilder.append(" | Prep Checklist: ${item.preparationChecklist}")
            if (item.priorityReason != null) contextBuilder.append(" | Priority Reason: ${item.priorityReason}")
            if (item.merchant != null) contextBuilder.append(" | Merchant: ${item.merchant}")
            if (item.returnDeadlineString != null) contextBuilder.append(" | Return Deadline: ${item.returnDeadlineString}")
            if (item.warrantyExpiryString != null) contextBuilder.append(" | Warranty: ${item.warrantyExpiryString}")
            if (item.renewalDateString != null) contextBuilder.append(" | Renewal Date: ${item.renewalDateString}")
            if (item.isWaitingFor) contextBuilder.append(" | Waiting For: ${item.waitingForDetail}")
            contextBuilder.append("\n")
        }

        contextBuilder.append("\nDocuments Memory Vault (${docs.size}):\n")
        docs.forEach { doc ->
            contextBuilder.append("- Document: ${doc.fileName} | Category: ${doc.documentCategory} | Issuer: ${doc.issuer} | Expiry: ${doc.expiryDateString} | Ref: ${doc.referenceNumber} | Required Action: ${doc.requiredAction}\n")
        }

        geminiEngine.askLifeAdmin(question, contextBuilder.toString())
    }

    suspend fun launchWorkflow(workflow: WorkflowType) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        when (workflow) {
            WorkflowType.PASSPORT_RENEWAL -> {
                val docId = documentDao.insertDocument(
                    ScannedDocumentEntity(
                        fileName = "Passport_Renewal_Application_DS82.pdf",
                        fileType = "PDF",
                        documentCategory = "IDENTIFICATION",
                        issuer = "Department of State",
                        referenceNumber = "DS-82-NEW",
                        expiryDateString = "In 10 Days",
                        requiredAction = "Fill out and submit at passport office",
                        extractedSummary = "Passport renewal process initialized with 4 connected tasks."
                    )
                )
                itemDao.insertItems(
                    listOf(
                        ExtractedItemEntity(
                            title = "Gather Passport Identity Documents",
                            description = "Collect original expiring passport and birth certificate.",
                            type = ItemType.TASK,
                            category = ItemCategory.GOVERNMENT,
                            dueDateString = "Tomorrow",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Required before booking regional passport agency appointment.",
                            linkedDocumentId = docId,
                            linkedDocumentName = "Passport_Renewal_Application_DS82.pdf",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Take 2x2 Official Color Photos",
                            description = "Visit local pharmacy/photo center to get 2 compliant 2x2 passport photos.",
                            type = ItemType.TASK,
                            category = ItemCategory.GOVERNMENT,
                            dueDateString = "In 3 Days",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Physical photos must accompany Form DS-82 submission.",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Passport Office Biometric Appointment",
                            description = "Official appointment to lodge renewal application.",
                            type = ItemType.APPOINTMENT,
                            category = ItemCategory.GOVERNMENT,
                            dueDateString = "In 10 Days at 10:00 AM",
                            location = "Regional Passport Agency, Floor 3",
                            companyOrPerson = "Department of State",
                            priority = ItemPriority.HIGH,
                            priorityReason = "HIGH PRIORITY: Application deadline approaches.",
                            requiredDocuments = "Old Passport, 2 Photos, Form DS-82, Certified Check",
                            preparationChecklist = "Print confirmation slip\nPrepare certified money order ($130)\nArrive 15 min early",
                            linkedDocumentId = docId,
                            sourceType = "WORKFLOW"
                        )
                    )
                )
            }
            WorkflowType.VEHICLE_REGISTRATION -> {
                itemDao.insertItems(
                    listOf(
                        ExtractedItemEntity(
                            title = "Complete Vehicle Emissions Inspection",
                            description = "Take car to certified state inspection station for emissions check.",
                            type = ItemType.TASK,
                            category = ItemCategory.VEHICLE,
                            dueDateString = "This Weekend",
                            priority = ItemPriority.HIGH,
                            priorityReason = "State inspection report needed prior to registration fee submission.",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Renew Vehicle Insurance Policy",
                            description = "Compare renewal rate and renew policy before current term expires.",
                            type = ItemType.RENEWAL,
                            category = ItemCategory.VEHICLE,
                            renewalDateString = "Next Week",
                            renewalStatus = "RENEWING_SOON",
                            companyOrPerson = "Geico Insurance",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Valid proof of insurance required for vehicle registration.",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Submit DMV Annual Registration Fee",
                            description = "Pay online state vehicle registration ($185.00) and affix new license plate sticker.",
                            type = ItemType.BILL,
                            category = ItemCategory.VEHICLE,
                            dueDateString = "In 2 Weeks",
                            amount = 185.00,
                            companyOrPerson = "Department of Motor Vehicles",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Avoid expired registration ticket penalties.",
                            sourceType = "WORKFLOW"
                        )
                    )
                )
            }
            WorkflowType.TRAVEL_PREPARATION -> {
                itemDao.insertItems(
                    listOf(
                        ExtractedItemEntity(
                            title = "Complete Online Flight Check-in (24h Window)",
                            description = "Select seats and download mobile boarding passes.",
                            type = ItemType.TASK,
                            category = ItemCategory.TRAVEL,
                            dueDateString = "24h Before Departure",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Early check-in guarantees preferred seat and boarding group.",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Pack Travel Essentials & Medications",
                            description = "Review packing list: passport, chargers, medications, weather appropriate clothes.",
                            type = ItemType.TASK,
                            category = ItemCategory.TRAVEL,
                            dueDateString = "Day Before Departure",
                            priority = ItemPriority.MEDIUM,
                            preparationChecklist = "Passport/ID in carry-on\nPrescription meds\nPhone & laptop chargers\nNoise canceling headphones",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Flight Departure - Set Leave-Home Reminder",
                            description = "Arrange airport transfer or drive; arrive 2.5 hours prior to departure.",
                            type = ItemType.TRAVEL,
                            category = ItemCategory.TRAVEL,
                            dueDateString = "Scheduled Departure Day",
                            priority = ItemPriority.HIGH,
                            sourceType = "WORKFLOW"
                        )
                    )
                )
            }
            WorkflowType.RETURN_WARRANTY -> {
                itemDao.insertItem(
                    ExtractedItemEntity(
                        title = "Verify Return Window & Print Label",
                        description = "Check merchant return eligibility date, print return shipping label, and pack item.",
                        type = ItemType.PURCHASE_ADMIN,
                        category = ItemCategory.SHOPPING,
                        dueDateString = "In 5 Days",
                        returnDeadlineString = "In 7 Days",
                        priority = ItemPriority.HIGH,
                        priorityReason = "Return deadline expires soon. Late returns are rejected by seller.",
                        sourceType = "WORKFLOW"
                    )
                )
            }
            WorkflowType.MOVING_HOUSE -> {
                itemDao.insertItems(
                    listOf(
                        ExtractedItemEntity(
                            title = "Submit Official 30-Day Notice to Landlord",
                            description = "Send written notice of intent to vacate according to lease terms.",
                            type = ItemType.TASK,
                            category = ItemCategory.HOME,
                            dueDateString = "This Friday",
                            priority = ItemPriority.HIGH,
                            priorityReason = "Required by contract to recover full security deposit.",
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "File USPS Official Change of Address",
                            description = "Submit online mail forwarding request 2 weeks prior to move.",
                            type = ItemType.TASK,
                            category = ItemCategory.HOME,
                            dueDateString = "Next Week",
                            priority = ItemPriority.MEDIUM,
                            sourceType = "WORKFLOW"
                        ),
                        ExtractedItemEntity(
                            title = "Transfer Electric & High-Speed Internet Utilities",
                            description = "Schedule disconnection at current residence and activation at new address.",
                            type = ItemType.TASK,
                            category = ItemCategory.HOME,
                            dueDateString = "10 Days Before Move",
                            companyOrPerson = "Power & Broadband Co.",
                            priority = ItemPriority.HIGH,
                            sourceType = "WORKFLOW"
                        )
                    )
                )
            }
            WorkflowType.DOCTOR_CHECKUP -> {
                itemDao.insertItem(
                    ExtractedItemEntity(
                        title = "Medical Appointment Preparation",
                        description = "Prepare documents and questions for doctor consultation.",
                        type = ItemType.APPOINTMENT,
                        category = ItemCategory.HEALTH,
                        dueDateString = "Scheduled Appointment Date",
                        priority = ItemPriority.HIGH,
                        requiredDocuments = "Insurance card, Photo ID, Recent lab reports, Medication list",
                        preparationChecklist = "Fast 10 hours if blood work ordered\nWrite down current symptoms & questions\nBring primary health insurance card",
                        sourceType = "WORKFLOW"
                    )
                )
            }
        }
    }

    private suspend fun saveExtractionResult(result: ExtractionResponse, sourceType: String, rawContent: String) {
        val docCategory = when {
            rawContent.contains("insurance", ignoreCase = true) -> "INSURANCE"
            rawContent.contains("warranty", ignoreCase = true) -> "WARRANTY"
            rawContent.contains("bill", ignoreCase = true) || rawContent.contains("electric", ignoreCase = true) -> "BILL_DOC"
            rawContent.contains("passport", ignoreCase = true) -> "IDENTIFICATION"
            rawContent.contains("flight", ignoreCase = true) || rawContent.contains("hotel", ignoreCase = true) -> "TRAVEL_DOC"
            rawContent.contains("amazon", ignoreCase = true) || rawContent.contains("order", ignoreCase = true) -> "RECEIPT"
            else -> "OTHER"
        }

        // Save document record in Document Memory
        val docId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = when (sourceType) {
                    "SCREENSHOT" -> "Screenshot_${System.currentTimeMillis() % 10000}.png"
                    "CAMERA" -> "Paper_Scan_${System.currentTimeMillis() % 10000}.jpg"
                    else -> "LifeInbox_Item_${System.currentTimeMillis() % 10000}.txt"
                },
                fileType = sourceType,
                documentCategory = docCategory,
                issuer = result.items.firstOrNull()?.companyOrPerson ?: result.items.firstOrNull()?.merchant ?: "LifeAdmin Inbox",
                referenceNumber = result.items.firstOrNull()?.orderNumber ?: result.items.firstOrNull()?.bookingReference,
                expiryDateString = result.items.firstOrNull()?.returnDeadlineText ?: result.items.firstOrNull()?.renewalDateText ?: result.items.firstOrNull()?.dueDateText,
                requiredAction = result.items.firstOrNull()?.title,
                status = "PROCESSED",
                extractedSummary = result.summary,
                extractedItemCount = result.items.size,
                rawContentSnippet = rawContent.take(250)
            )
        )

        // Convert extracted raw items to Entities with connected metadata
        val entities = result.items.map { raw ->
            ExtractedItemEntity(
                title = raw.title,
                description = raw.description,
                type = runCatching { ItemType.valueOf(raw.type) }.getOrDefault(ItemType.TASK),
                category = runCatching { ItemCategory.valueOf(raw.category) }.getOrDefault(ItemCategory.PERSONAL),
                dueDateString = raw.dueDateText ?: "Today",
                amount = raw.amount,
                companyOrPerson = raw.companyOrPerson,
                location = raw.location,
                priority = runCatching { ItemPriority.valueOf(raw.priority) }.getOrDefault(ItemPriority.MEDIUM),
                priorityReason = raw.priorityReason,
                isTopPriority = raw.isTopPriority,
                isWaitingFor = raw.isWaitingFor,
                waitingForDetail = raw.waitingForDetail,
                requiredDocuments = raw.requiredDocuments,
                preparationChecklist = raw.preparationChecklist,
                linkedDocumentId = docId,
                linkedDocumentName = "Document #$docId",
                merchant = raw.merchant,
                productName = raw.productName,
                orderNumber = raw.orderNumber,
                returnDeadlineString = raw.returnDeadlineText,
                warrantyExpiryString = raw.warrantyExpiryText,
                renewalDateString = raw.renewalDateText,
                renewalStatus = if (raw.renewalDateText != null) "RENEWING_SOON" else null,
                travelDateString = raw.travelDateText,
                bookingReference = raw.bookingReference,
                destination = raw.destination,
                sourceType = sourceType,
                rawSourceText = rawContent
            )
        }
        itemDao.insertItems(entities)

        // Trigger smart contextual reminder notification
        val firstItem = entities.firstOrNull()
        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "New Life Admin Action Required",
                body = firstItem?.priorityReason ?: "${entities.size} items added to your Life Admin agenda.",
                type = if (firstItem?.type == ItemType.BILL) "BILL_DUE" else "TASK_REMINDER",
                linkedDocumentId = docId
            )
        )
    }

    fun speakText(text: String) {
        if (isTtsReady) {
            ttsEngine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lifeadmin_speak_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        ttsEngine?.stop()
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        itemDao.deleteAllItems()
        documentDao.deleteAllDocuments()
        notificationDao.deleteAllNotifications()
    }

    private suspend fun seedInitialData() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        // 1. Seed Documents into Document Memory
        val docInsuranceId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = "Geico_Auto_Policy_2025_2026.pdf",
                fileType = "PDF",
                documentCategory = "INSURANCE",
                issuer = "Geico Insurance",
                referenceNumber = "POL-88392-CA",
                expiryDateString = "Tomorrow",
                expiryDate = now + oneDay,
                relatedPersonOrCompany = "Geico Insurance",
                requiredAction = "Pay renewal premium to prevent coverage lapse",
                extractedSummary = "Annual comprehensive vehicle insurance coverage policy.",
                extractedItemCount = 1
            )
        )

        val docPassportId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = "Passport_Renewal_Confirmation_DS82.pdf",
                fileType = "PDF",
                documentCategory = "IDENTIFICATION",
                issuer = "U.S. Department of State",
                referenceNumber = "APP-551029",
                expiryDateString = "September 22",
                expiryDate = now + (8 * oneDay),
                relatedPersonOrCompany = "National Passport Processing Center",
                requiredAction = "Bring required documents to biometric appointment",
                extractedSummary = "Official appointment receipt for biometric passport renewal.",
                extractedItemCount = 1
            )
        )

        val docReceiptId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = "Amazon_Order_Receipt_SonyHeadphones.pdf",
                fileType = "RECEIPT",
                documentCategory = "WARRANTY",
                issuer = "Amazon",
                referenceNumber = "#114-8923184",
                expiryDateString = "September 28 (Return Window)",
                expiryDate = now + (14 * oneDay),
                relatedPersonOrCompany = "Sony Electronics",
                requiredAction = "Keep original receipt for warranty claims",
                extractedSummary = "Purchase invoice with 30-day return window and 1-year manufacturer warranty.",
                extractedItemCount = 1
            )
        )

        val docElectricId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = "Electricity_Bill_Sept_2026.pdf",
                fileType = "BILL_DOC",
                documentCategory = "BILL_DOC",
                issuer = "State Electricity Board",
                referenceNumber = "ACC-449210",
                expiryDateString = "September 18",
                expiryDate = now + (4 * oneDay),
                relatedPersonOrCompany = "State Electricity Board",
                requiredAction = "Pay ₹1,240 before September 18",
                extractedSummary = "Residential power consumption statement due on September 18.",
                extractedItemCount = 1
            )
        )

        // 2. Seed Connected Life Administration Obligations
        val initialItems = listOf(
            ExtractedItemEntity(
                title = "Car Insurance Policy Renewal",
                description = "Annual comprehensive vehicle coverage expires tomorrow. Renew policy to prevent coverage lapse.",
                type = ItemType.RENEWAL,
                category = ItemCategory.VEHICLE,
                dueDate = now + oneDay,
                dueDateString = "Tomorrow",
                companyOrPerson = "Geico Insurance",
                priority = ItemPriority.HIGH,
                priorityReason = "HIGH PRIORITY: Your car insurance expires tomorrow. You haven't completed the renewal task yet.",
                isTopPriority = true,
                renewalDateString = "Tomorrow",
                renewalStatus = "RENEWING_SOON",
                linkedDocumentId = docInsuranceId,
                linkedDocumentName = "Geico_Auto_Policy_2025_2026.pdf",
                sourceType = "PDF"
            ),
            ExtractedItemEntity(
                title = "Electricity Bill Payment (₹1,240)",
                description = "Monthly residential electric power bill from State Electricity Board due September 18.",
                type = ItemType.BILL,
                category = ItemCategory.BILLS,
                dueDate = now + (4 * oneDay),
                dueDateString = "September 18",
                amount = 1240.0,
                companyOrPerson = "State Electricity Board",
                priority = ItemPriority.HIGH,
                priorityReason = "HIGH PRIORITY: Payment due September 18. Avoid late penalty.",
                isTopPriority = true,
                linkedDocumentId = docElectricId,
                linkedDocumentName = "Electricity_Bill_Sept_2026.pdf",
                sourceType = "SCREENSHOT"
            ),
            ExtractedItemEntity(
                title = "Passport Biometric & Renewal Appointment",
                description = "Official in-person appointment at regional passport agency.",
                type = ItemType.APPOINTMENT,
                category = ItemCategory.GOVERNMENT,
                dueDate = now + (8 * oneDay),
                dueDateString = "September 22 at 11:00 AM",
                companyOrPerson = "Department of State",
                location = "Regional Passport Agency, Floor 3",
                priority = ItemPriority.HIGH,
                priorityReason = "HIGH PRIORITY: Due in 8 days. Requires gathering 4 physical identity documents.",
                isTopPriority = true,
                requiredDocuments = "Old Passport, 2x2 Passport Photos (2 copies), Form DS-82, Fee Payment Slip",
                preparationChecklist = "Print appointment confirmation slip\nPrepare certified check for $130\nArrive 15 minutes early",
                linkedDocumentId = docPassportId,
                linkedDocumentName = "Passport_Renewal_Confirmation_DS82.pdf",
                sourceType = "PDF"
            ),
            ExtractedItemEntity(
                title = "Sony Headphones Return Window & Warranty",
                description = "Purchased August 28. Return window closes September 28. 1-year manufacturer warranty active until Aug 2027.",
                type = ItemType.PURCHASE_ADMIN,
                category = ItemCategory.SHOPPING,
                dueDate = now + (14 * oneDay),
                dueDateString = "September 28 (Return Deadline)",
                merchant = "Amazon",
                productName = "Sony WH-1000XM5 Headphones",
                orderNumber = "#114-8923184",
                returnDeadlineString = "September 28, 2026",
                warrantyExpiryString = "August 28, 2027",
                companyOrPerson = "Amazon / Sony",
                priority = ItemPriority.MEDIUM,
                priorityReason = "30-day return window active. Warranty document stored in memory.",
                linkedDocumentId = docReceiptId,
                linkedDocumentName = "Amazon_Order_Receipt_SonyHeadphones.pdf",
                sourceType = "EMAIL_TEXT"
            ),
            ExtractedItemEntity(
                title = "Flight to Chicago (AA 1420)",
                description = "Departure from Terminal 2 at 8:00 AM for annual conference.",
                type = ItemType.TRAVEL,
                category = ItemCategory.TRAVEL,
                dueDate = now + (10 * oneDay),
                dueDateString = "September 24 at 8:00 AM",
                travelDateString = "September 24 at 8:00 AM",
                bookingReference = "W7K9LQ",
                destination = "Chicago O'Hare (ORD)",
                companyOrPerson = "American Airlines",
                priority = ItemPriority.HIGH,
                priorityReason = "Upcoming flight travel. Check-in opens 24 hours prior to departure.",
                preparationChecklist = "Check-in online 24h prior\nPack essentials and medication\nPrepare physical ID & boarding pass\nSet 5:30 AM leave-home reminder",
                sourceType = "EMAIL_TEXT"
            ),
            ExtractedItemEntity(
                title = "Visa Application Processing Decision",
                description = "Submitted passport and application dossier to consulate.",
                type = ItemType.WAITING_FOR,
                category = ItemCategory.GOVERNMENT,
                dueDateString = "Pending Consulate Response",
                isWaitingFor = true,
                waitingForDetail = "Waiting for German Consulate visa approval email (Expected in 5-7 days)",
                companyOrPerson = "German Consulate General",
                priority = ItemPriority.MEDIUM,
                priorityReason = "Tracking pending visa application.",
                sourceType = "MANUAL"
            ),
            ExtractedItemEntity(
                title = "Annual Physical Health Checkup",
                description = "Routine medical checkup and consultation with Dr. Sarah Jenkins.",
                type = ItemType.APPOINTMENT,
                category = ItemCategory.HEALTH,
                dueDate = now + (16 * oneDay),
                dueDateString = "Next Friday 2:00 PM",
                companyOrPerson = "Dr. Sarah Jenkins",
                location = "Grand Ave Health Center, Suite 200",
                priority = ItemPriority.MEDIUM,
                requiredDocuments = "Recent blood lab results, Health insurance card",
                preparationChecklist = "Fast 8 hours prior if fasting blood tests required",
                sourceType = "MANUAL"
            )
        )

        itemDao.insertItems(initialItems)

        // 3. Seed Smart Contextual Reminders
        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "Renewal Alert: Car Insurance",
                body = "Your car insurance expires tomorrow. You haven't completed the renewal task yet.",
                type = "RENEWAL_DUE",
                linkedDocumentId = docInsuranceId
            )
        )
        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "Bill Due Soon: Electricity (₹1,240)",
                body = "Payment is due on September 18. Settle early to avoid service disruption.",
                type = "BILL_DUE",
                linkedDocumentId = docElectricId
            )
        )
        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "Appointment Preparation: Passport",
                body = "Passport appointment on Sep 22 requires 4 physical documents. Tap to review preparation checklist.",
                type = "PREP_REMINDER",
                linkedDocumentId = docPassportId
            )
        )
    }
}
