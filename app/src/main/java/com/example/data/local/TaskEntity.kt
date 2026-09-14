package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskUrgency(val label: String) {
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    companion object {
        fun fromString(value: String?): TaskUrgency {
            if (value.isNullOrBlank()) return MEDIUM
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: MEDIUM
        }
    }
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val deadline: Long? = null, // epoch millis
    val deadlineFormatted: String? = null, // e.g. "Today, 5:00 PM", "Sep 18, 2026"
    val urgency: String = "MEDIUM", // CRITICAL, HIGH, MEDIUM, LOW
    val isCompleted: Boolean = false,
    val category: String = "Personal", // Personal, Financial, Legal, Health, Home, Auto
    val actionReason: String? = null, // Explains priority reason (e.g. "Fine doubles if unpaid by midnight")
    val createdAt: Long = System.currentTimeMillis()
)
