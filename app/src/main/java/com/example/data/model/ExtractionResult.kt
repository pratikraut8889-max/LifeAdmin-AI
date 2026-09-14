package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExtractedItemRaw(
    val title: String,
    val description: String? = null,
    val type: String = "TASK", // TASK, BILL, APPOINTMENT, RENEWAL, PURCHASE_ADMIN, TRAVEL, DOCUMENT_ACTION, APPLICATION, WAITING_FOR, REMINDER
    val category: String = "PERSONAL", // PERSONAL, BILLS, HEALTH, TRAVEL, SHOPPING, WORK, SCHOOL, VEHICLE, GOVERNMENT, HOME
    val dueDateText: String? = null, // e.g. "Sep 22, 11:00 AM", "Tomorrow 5 PM"
    val amount: Double? = null, // Contextual life obligation only (e.g. 1240.0 for utility bill)
    val companyOrPerson: String? = null,
    val location: String? = null,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val priorityReason: String? = null, // e.g. "Due tomorrow. Requires passport photo."
    val isTopPriority: Boolean = false,
    val isWaitingFor: Boolean = false,
    val waitingForDetail: String? = null,
    val requiredDocuments: String? = null, // e.g. "Old Passport, 2 Photos, Proof of Address"
    val preparationChecklist: String? = null, // e.g. "Print confirmation, Get money order, Arrive 15 min early"
    val merchant: String? = null, // e.g. "Amazon"
    val productName: String? = null, // e.g. "Sony Headphones"
    val orderNumber: String? = null, // e.g. "#402-881923"
    val returnDeadlineText: String? = null,
    val warrantyExpiryText: String? = null,
    val renewalDateText: String? = null,
    val travelDateText: String? = null,
    val bookingReference: String? = null,
    val destination: String? = null
)

@JsonClass(generateAdapter = true)
data class ExtractionResponse(
    val summary: String,
    val items: List<ExtractedItemRaw>
)
