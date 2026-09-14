package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ItemCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM extracted_items WHERE isArchived = 0 ORDER BY isCompleted ASC, isTopPriority DESC, dueDate ASC, id DESC")
    fun getAllActiveItems(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE isArchived = 1 ORDER BY id DESC")
    fun getArchivedItems(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE category = :category AND isArchived = 0 ORDER BY isCompleted ASC, dueDate ASC")
    fun getItemsByCategory(category: ItemCategory): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE type = 'RENEWAL' AND isArchived = 0 ORDER BY isCompleted ASC, renewalDate ASC, id DESC")
    fun getRenewals(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE type = 'APPOINTMENT' AND isArchived = 0 ORDER BY isCompleted ASC, dueDate ASC, id DESC")
    fun getAppointments(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE type = 'PURCHASE_ADMIN' AND isArchived = 0 ORDER BY isCompleted ASC, returnDeadline ASC, id DESC")
    fun getPurchases(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE isWaitingFor = 1 AND isArchived = 0 ORDER BY isCompleted ASC, id DESC")
    fun getWaitingForItems(): Flow<List<ExtractedItemEntity>>

    @Query("SELECT * FROM extracted_items WHERE id = :id")
    suspend fun getItemById(id: Long): ExtractedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ExtractedItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ExtractedItemEntity>)

    @Update
    suspend fun updateItem(item: ExtractedItemEntity)

    @Query("UPDATE extracted_items SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)

    @Query("UPDATE extracted_items SET isArchived = 1 WHERE id = :id")
    suspend fun archiveItem(id: Long)

    @Query("DELETE FROM extracted_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("DELETE FROM extracted_items")
    suspend fun deleteAllItems()
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM scanned_documents ORDER BY uploadDate DESC")
    fun getAllDocuments(): Flow<List<ScannedDocumentEntity>>

    @Query("SELECT * FROM scanned_documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): ScannedDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: ScannedDocumentEntity): Long

    @Query("DELETE FROM scanned_documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)

    @Query("DELETE FROM scanned_documents")
    suspend fun deleteAllDocuments()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity): Long

    @Query("UPDATE notification_logs SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notification_logs SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notification_logs WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM notification_logs")
    suspend fun deleteAllNotifications()
}
