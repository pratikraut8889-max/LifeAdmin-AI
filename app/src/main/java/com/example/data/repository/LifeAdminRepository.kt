package com.example.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.data.local.AppDatabase
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.ScannedDocumentEntity
import com.example.data.model.ExtractionResponse
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.data.remote.GeminiExtractionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.remote.FirebaseVertexAIService
import java.util.Locale

class LifeAdminRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val itemDao = db.itemDao()
    private val documentDao = db.documentDao()
    private val notificationDao = db.notificationDao()
    private val vertexAiService = FirebaseVertexAIService()

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

    suspend fun processTextExtraction(rawText: String, sourceType: String = "EMAIL_TEXT"): ExtractionResponse = withContext(Dispatchers.IO) {
        val result = vertexAiService.extractTasksFromText(rawText)
        saveExtractionResult(result, sourceType, rawText)
        result
    }

    suspend fun processImageExtraction(bitmap: android.graphics.Bitmap, sourceType: String = "SCREENSHOT"): ExtractionResponse = withContext(Dispatchers.IO) {
        val result = vertexAiService.extractTasksFromImage(bitmap)
        saveExtractionResult(result, sourceType, "[Image File Upload]")
        result
    }

    private suspend fun saveExtractionResult(result: ExtractionResponse, sourceType: String, rawContent: String) {
        // Save document record
        val docId = documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = when (sourceType) {
                    "SCREENSHOT" -> "Screenshot_${System.currentTimeMillis() % 10000}.png"
                    "CAMERA" -> "Paper_Scan_${System.currentTimeMillis() % 10000}.jpg"
                    else -> "Email_Message_${System.currentTimeMillis() % 10000}.txt"
                },
                fileType = sourceType,
                status = "PROCESSED",
                extractedSummary = result.summary,
                extractedItemCount = result.items.size,
                rawContentSnippet = rawContent.take(200)
            )
        )

        // Convert extracted raw items to Entities
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
                isTopPriority = raw.isTopPriority,
                sourceType = sourceType,
                rawSourceText = rawContent
            )
        }
        itemDao.insertItems(entities)

        // Add a smart notification trigger for newly extracted items
        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "AI Analysis Complete",
                body = "Extracted ${result.items.size} action items from $sourceType.",
                type = "DAILY_BRIEF"
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

        val initialItems = listOf(
            ExtractedItemEntity(
                title = "Pay Electric Utility Statement",
                description = "Monthly residential electric power bill from Metro Energy Co.",
                type = ItemType.BILL,
                category = ItemCategory.BILLS,
                dueDate = now + oneDay,
                dueDateString = "Tomorrow 5:00 PM",
                amount = 118.50,
                companyOrPerson = "Metro Power & Light",
                priority = ItemPriority.HIGH,
                isTopPriority = true,
                sourceType = "SCREENSHOT"
            ),
            ExtractedItemEntity(
                title = "Quarterly Dental Hygiene Checkup",
                description = "Teeth cleaning and routine checkup with Dr. Sarah Jenkins.",
                type = ItemType.APPOINTMENT,
                category = ItemCategory.HEALTH,
                dueDate = now + (2 * oneDay),
                dueDateString = "Thursday 10:30 AM",
                companyOrPerson = "Dr. Sarah Jenkins DDS",
                location = "Grand Avenue Health Suite 200",
                priority = ItemPriority.MEDIUM,
                isTopPriority = true,
                sourceType = "EMAIL_TEXT"
            ),
            ExtractedItemEntity(
                title = "Submit Q3 Expenses Report",
                description = "Compile receipts for travel and software subscriptions.",
                type = ItemType.TASK,
                category = ItemCategory.WORK,
                dueDate = now + (3 * oneDay),
                dueDateString = "Friday 6:00 PM",
                amount = 450.00,
                priority = ItemPriority.HIGH,
                isTopPriority = true,
                sourceType = "MANUAL"
            ),
            ExtractedItemEntity(
                title = "Confirm Hotel Booking for Chicago Conference",
                description = "Review flight arrival and hotel check-in time.",
                type = ItemType.FOLLOW_UP,
                category = ItemCategory.TRAVEL,
                dueDateString = "Next Monday",
                companyOrPerson = "Hyatt Regency Chicago",
                priority = ItemPriority.LOW,
                sourceType = "PDF"
            ),
            ExtractedItemEntity(
                title = "Renew Vehicle Registration & Insurance",
                description = "Annual DMV state registration fee payment.",
                type = ItemType.BILL,
                category = ItemCategory.BILLS,
                dueDateString = "Aug 20, 2026",
                amount = 185.00,
                companyOrPerson = "Department of Motor Vehicles",
                priority = ItemPriority.MEDIUM,
                sourceType = "CAMERA"
            )
        )

        itemDao.insertItems(initialItems)

        documentDao.insertDocument(
            ScannedDocumentEntity(
                fileName = "Utility_Bill_Aug_2026.pdf",
                fileType = "PDF",
                extractedSummary = "You have 1 electric bill ($118.50) due tomorrow and 1 appointment scheduled Thursday.",
                extractedItemCount = 2,
                rawContentSnippet = "Statement Date: Aug 1, 2026. Total Amount Due: $118.50. Due Date: Aug 4, 2026."
            )
        )

        notificationDao.insertNotification(
            NotificationLogEntity(
                title = "Upcoming Bill Payment",
                body = "Metro Energy Co. bill ($118.50) is due tomorrow at 5:00 PM.",
                type = "BILL_DUE"
            )
        )
    }
}
