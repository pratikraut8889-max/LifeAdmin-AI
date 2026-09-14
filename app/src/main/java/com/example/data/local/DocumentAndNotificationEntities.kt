package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_documents")
data class ScannedDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val fileType: String, // SCREENSHOT, PDF, EMAIL_PASTE, SCAN_PHOTO
    val documentCategory: String? = null, // INSURANCE, WARRANTY, RECEIPT, CONTRACT, TRAVEL_DOC, IDENTIFICATION, BILL_DOC, APPOINTMENT_DOC, CERTIFICATE
    val issuer: String? = null, // e.g. Geico, Apple, Dept of State, Metro Energy
    val referenceNumber: String? = null, // Policy number, Order number, Confirmation code
    val expiryDateString: String? = null, // e.g. "Sep 19, 2026"
    val expiryDate: Long? = null,
    val relatedPersonOrCompany: String? = null,
    val requiredAction: String? = null, // e.g. "Pay bill by Sep 18", "Renew policy", "Keep for return window"
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
    val type: String = "TASK_REMINDER", // BILL_DUE, APPOINTMENT, TASK_REMINDER, RENEWAL_DUE, PREP_REMINDER, DAILY_BRIEF
    val isRead: Boolean = false,
    val linkedItemId: Long? = null,
    val linkedDocumentId: Long? = null
)
