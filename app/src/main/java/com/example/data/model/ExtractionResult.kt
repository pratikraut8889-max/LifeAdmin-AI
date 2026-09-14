package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExtractedItemRaw(
    val title: String,
    val description: String? = null,
    val type: String = "TASK", // TASK, BILL, APPOINTMENT, REMINDER, FOLLOW_UP
    val category: String = "PERSONAL", // BILLS, PERSONAL, WORK, SCHOOL, HEALTH, SHOPPING, TRAVEL
    val dueDateText: String? = null, // e.g. "Aug 15, 2026" or "Tomorrow 3 PM"
    val amount: Double? = null,
    val companyOrPerson: String? = null,
    val location: String? = null,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val isTopPriority: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ExtractionResponse(
    val summary: String,
    val items: List<ExtractedItemRaw>
)
