package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType

class Converters {
    @TypeConverter
    fun fromItemType(value: ItemType): String = value.name

    @TypeConverter
    fun toItemType(value: String): ItemType = runCatching { ItemType.valueOf(value) }.getOrDefault(ItemType.TASK)

    @TypeConverter
    fun fromItemCategory(value: ItemCategory): String = value.name

    @TypeConverter
    fun toItemCategory(value: String): ItemCategory = runCatching { ItemCategory.valueOf(value) }.getOrDefault(ItemCategory.PERSONAL)

    @TypeConverter
    fun fromItemPriority(value: ItemPriority): String = value.name

    @TypeConverter
    fun toItemPriority(value: String): ItemPriority = runCatching { ItemPriority.valueOf(value) }.getOrDefault(ItemPriority.MEDIUM)
}

@Database(
    entities = [
        ExtractedItemEntity::class,
        ScannedDocumentEntity::class,
        NotificationLogEntity::class,
        TaskEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun documentDao(): DocumentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeadmin_ai.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
