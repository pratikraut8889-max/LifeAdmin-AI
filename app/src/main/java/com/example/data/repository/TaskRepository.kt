package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

interface TaskRepository {
    val allTasks: Flow<List<TaskEntity>>
    val criticalTasks: Flow<List<TaskEntity>>
    val todayTasks: Flow<List<TaskEntity>>
    val upcomingTasks: Flow<List<TaskEntity>>

    suspend fun insertTask(task: TaskEntity): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean)
    suspend fun deleteTask(taskId: Long)
    suspend fun getTaskById(id: Long): TaskEntity?
    fun getTasksByDocumentId(documentId: Long): Flow<List<TaskEntity>>
    suspend fun seedInitialTasks()
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override val criticalTasks: Flow<List<TaskEntity>> = allTasks.map { list ->
        list.filter { task ->
            !task.isCompleted && task.urgency.equals("CRITICAL", ignoreCase = true)
        }
    }.flowOn(Dispatchers.Default)

    override val todayTasks: Flow<List<TaskEntity>> = allTasks.map { list ->
        val (startOfDay, endOfDay) = getTodayBounds()
        list.filter { task ->
            !task.isCompleted &&
                !task.urgency.equals("CRITICAL", ignoreCase = true) &&
                isTaskDueToday(task, startOfDay, endOfDay)
        }
    }.flowOn(Dispatchers.Default)

    override val upcomingTasks: Flow<List<TaskEntity>> = allTasks.map { list ->
        val (_, endOfDay) = getTodayBounds()
        list.filter { task ->
            !task.isCompleted &&
                !task.urgency.equals("CRITICAL", ignoreCase = true) &&
                isTaskUpcoming(task, endOfDay)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    override suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateCompletionStatus(taskId, isCompleted)
    }

    override suspend fun deleteTask(taskId: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(id: Long): TaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(id)
    }

    override fun getTasksByDocumentId(documentId: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDocumentId(documentId)
    }

    override suspend fun seedInitialTasks() = withContext(Dispatchers.IO) {
        if (taskDao.getCount() > 0) return@withContext

        val now = System.currentTimeMillis()
        val sampleTasks = listOf(
            TaskEntity(
                title = "Renew Expiring Car Insurance Policy",
                description = "GEICO Policy #POL-88392-CA expires tonight at 11:59 PM. Must renew to avoid lapse in legal coverage.",
                deadline = now + 6 * 3600 * 1000L,
                deadlineFormatted = "Today, 11:59 PM",
                urgency = "CRITICAL",
                isCompleted = false,
                relatedDocumentId = 1L,
                category = "Auto / Legal",
                actionReason = "Coverage lapses at midnight; fines & registration suspension risk"
            ),
            TaskEntity(
                title = "Submit Form DS-82 & 2x2 Photos for Passport",
                description = "Consular appointment scheduled. Must bring signed DS-82, expiring passport, 2 compliant photos, and money order.",
                deadline = now + 26 * 3600 * 1000L,
                deadlineFormatted = "Tomorrow, 11:00 AM",
                urgency = "CRITICAL",
                isCompleted = false,
                relatedDocumentId = 2L,
                category = "Legal / Identity",
                actionReason = "Appointment requires physical documents; appointment will be cancelled if missing"
            ),
            TaskEntity(
                title = "Pay State Electricity Board Bill (₹1,240)",
                description = "Consumer Account #ACC-449210. Prompt payment avoids ₹85 late surcharge penalty.",
                deadline = now + 7 * 3600 * 1000L,
                deadlineFormatted = "Today, 6:00 PM",
                urgency = "HIGH",
                isCompleted = false,
                relatedDocumentId = 3L,
                category = "Utility / Bill",
                actionReason = "Avoid ₹85 late surcharge"
            ),
            TaskEntity(
                title = "Online Flight Check-In (AA 1420)",
                description = "American Airlines flight to Chicago O'Hare. Check-in window opens 24 hours prior to departure.",
                deadline = now + 9 * 3600 * 1000L,
                deadlineFormatted = "Today, 8:00 PM",
                urgency = "MEDIUM",
                isCompleted = false,
                category = "Travel",
                actionReason = "Select complimentary window seat before seat lock"
            ),
            TaskEntity(
                title = "Call Pediatrician for Annual Immunization Records",
                description = "School registration deadline requires certified records stamped by clinic.",
                deadline = now + 4 * 3600 * 1000L,
                deadlineFormatted = "Today, 3:30 PM",
                urgency = "MEDIUM",
                isCompleted = false,
                category = "Health",
                actionReason = "Records department closes at 4:00 PM"
            ),
            TaskEntity(
                title = "Return Window Closes: Sony WH-1000XM5",
                description = "Amazon Order #114-8923184. Eligible for 100% refund drop-off through September 28.",
                deadline = now + 14 * 86400 * 1000L,
                deadlineFormatted = "Sep 28, 2026",
                urgency = "HIGH",
                isCompleted = false,
                category = "Purchases",
                actionReason = "Full refund window expires after deadline"
            ),
            TaskEntity(
                title = "Vehicle Smog Inspection & DMV Registration",
                description = "California DMV smog inspection required prior to completing online annual registration.",
                deadline = now + 28 * 86400 * 1000L,
                deadlineFormatted = "Oct 15, 2026",
                urgency = "MEDIUM",
                isCompleted = false,
                category = "Auto / Legal",
                actionReason = "DMV registration renewal milestone"
            ),
            TaskEntity(
                title = "Home Heating HVAC Annual Filter Replacement",
                description = "Replace 20x25x4 MERV 11 filter before cold weather cycle begins.",
                deadline = now + 35 * 86400 * 1000L,
                deadlineFormatted = "Oct 22, 2026",
                urgency = "LOW",
                isCompleted = false,
                category = "Home Maintenance",
                actionReason = "Prevent winter heating system efficiency loss"
            )
        )
        taskDao.insertTasks(sampleTasks)
    }

    private fun getTodayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    private fun isTaskDueToday(task: TaskEntity, startOfDay: Long, endOfDay: Long): Boolean {
        if (task.deadlineFormatted?.contains("Today", ignoreCase = true) == true) {
            return true
        }
        val dl = task.deadline ?: return true // Default unscheduled tasks to Today
        // Included if it's within today or overdue
        return dl <= endOfDay
    }

    private fun isTaskUpcoming(task: TaskEntity, endOfDay: Long): Boolean {
        if (task.deadlineFormatted?.contains("Today", ignoreCase = true) == true) {
            return false
        }
        val dl = task.deadline ?: return false
        return dl > endOfDay
    }
}
