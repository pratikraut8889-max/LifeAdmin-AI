package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_documents")
data class ScannedDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val fileType: String, // SCREENSHOT, PDF, EMAIL_PASTE, SCAN_PHOTO
    val uploadDate: Long = System.currentTimeMillis(),
    val status: String = "PROCESSED", // PENDING, PROCESSED, FAILED
    val extractedSummary: String? = null,
    val extractedItemCount: Int = 0,
    val rawContentSnippet: String? = null
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "BILL_DUE", // BILL_DUE, APPOINTMENT, TASK_REMINDER, DAILY_BRIEF
    val isRead: Boolean = false
)
