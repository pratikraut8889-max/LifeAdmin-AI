package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType

@Entity(tableName = "extracted_items")
data class ExtractedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val type: ItemType = ItemType.TASK,
    val category: ItemCategory = ItemCategory.PERSONAL,
    val dueDate: Long? = null, // epoch millis
    val dueDateString: String? = null,
    val amount: Double? = null, // Contextual life obligation amount only (e.g. ₹1,240 utility bill)
    val companyOrPerson: String? = null,
    val location: String? = null,
    val priority: ItemPriority = ItemPriority.MEDIUM,
    val priorityReason: String? = null, // Explains why item is high priority (e.g. "Due tomorrow. Requires passport photo.")
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val isTopPriority: Boolean = false,
    val isWaitingFor: Boolean = false,
    val waitingForDetail: String? = null, // e.g. "Waiting for consular feedback"
    // Connected object relationships
    val requiredDocuments: String? = null, // Comma-separated or checklist (e.g. "Old Passport, 2 Photos, Proof of Address")
    val preparationChecklist: String? = null, // Checklist items (e.g. "Print confirmation, Get money order, Arrive 15 min early")
    val linkedDocumentId: Long? = null,
    val linkedDocumentName: String? = null,
    val linkedParentItemId: Long? = null,
    // Purchase & Warranty administration
    val merchant: String? = null, // e.g. "Amazon"
    val productName: String? = null, // e.g. "Sony Headphones"
    val orderNumber: String? = null, // e.g. "#402-881923"
    val purchaseDateString: String? = null,
    val returnDeadlineString: String? = null, // Return window
    val returnDeadline: Long? = null,
    val warrantyExpiryString: String? = null, // Warranty expiration
    val warrantyExpiry: Long? = null,
    // Renewal administration
    val renewalDateString: String? = null,
    val renewalDate: Long? = null,
    val renewalStatus: String? = null, // RENEWING_SOON, UPCOMING, EXPIRED, COMPLETED
    // Travel administration
    val travelDateString: String? = null,
    val bookingReference: String? = null, // e.g. "W7K9LQ"
    val destination: String? = null,
    // Meta
    val snoozedUntil: Long? = null,
    val sourceType: String = "MANUAL", // SCREENSHOT, PDF, EMAIL_TEXT, CAMERA, GALLERY, MANUAL, WORKFLOW
    val rawSourceText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
