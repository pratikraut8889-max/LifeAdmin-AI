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
    val amount: Double? = null,
    val companyOrPerson: String? = null,
    val location: String? = null,
    val priority: ItemPriority = ItemPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val isTopPriority: Boolean = false,
    val snoozedUntil: Long? = null,
    val sourceType: String = "MANUAL", // SCREENSHOT, PDF, EMAIL_TEXT, CAMERA, GALLERY, MANUAL
    val rawSourceText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
