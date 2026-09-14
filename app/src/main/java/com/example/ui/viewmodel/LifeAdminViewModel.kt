package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ExtractedItemEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.ScannedDocumentEntity
import com.example.data.local.TaskEntity
import com.example.data.model.ExtractionResponse
import com.example.data.model.ItemCategory
import com.example.data.model.ItemPriority
import com.example.data.model.ItemType
import com.example.data.model.UserAuthMode
import com.example.data.model.WorkflowType
import com.example.data.repository.LifeAdminRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.TaskRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenRoute {
    SPLASH,
    ONBOARDING,
    AUTH,
    TODAY,
    DASHBOARD,
    TIMELINE,
    ASK_AI,
    DOCUMENT_MEMORY,
    RENEWALS,
    WORKFLOWS,
    SCAN_UPLOAD,
    PROCESSING_RESULTS,
    TASK_DETAIL,
    CALENDAR,
    SEARCH,
    NOTIFICATIONS,
    PROFILE,
    PRIVACY
}

enum class ExtractionProcessingStep(val label: String) {
    IDLE("Ready"),
    SCANNING_SOURCE("Scanning document & layout..."),
    EXTRACTING_ENTITIES("Extracting deadlines, obligations & requirements..."),
    CATEGORIZING("Connecting objects & prioritizing..."),
    GENERATING_ACTION_PLAN("Formulating clean LifeAdmin plan..."),
    COMPLETED("Extraction Complete!")
}

class LifeAdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LifeAdminRepository(application.applicationContext)
    private val taskRepository: TaskRepository = TaskRepositoryImpl(
        AppDatabase.getInstance(application.applicationContext).taskDao()
    )

    init {
        viewModelScope.launch {
            taskRepository.seedInitialTasks()
        }
    }

    // Navigation & Auth State
    private val _currentScreen = MutableStateFlow(ScreenRoute.DASHBOARD)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    private val _userAuthMode = MutableStateFlow(UserAuthMode.GUEST)
    val userAuthMode: StateFlow<UserAuthMode> = _userAuthMode.asStateFlow()

    private val _userName = MutableStateFlow("Alex Morgan")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("alex.morgan@lifeadmin.ai")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Room Task Repository Streams (Daily Command Center: Critical, Today, Upcoming)
    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val criticalTasks: StateFlow<List<TaskEntity>> = taskRepository.criticalTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTasks: StateFlow<List<TaskEntity>> = taskRepository.todayTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks: StateFlow<List<TaskEntity>> = taskRepository.upcomingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database Streams
    val activeItems: StateFlow<List<ExtractedItemEntity>> = repository.activeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedItems: StateFlow<List<ExtractedItemEntity>> = repository.archivedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scannedDocuments: StateFlow<List<ScannedDocumentEntity>> = repository.scannedDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationLogEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val renewals: StateFlow<List<ExtractedItemEntity>> = repository.renewals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<ExtractedItemEntity>> = repository.appointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchases: StateFlow<List<ExtractedItemEntity>> = repository.purchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waitingForItems: StateFlow<List<ExtractedItemEntity>> = repository.waitingForItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<ItemCategory?>(null)
    val selectedCategoryFilter: StateFlow<ItemCategory?> = _selectedCategoryFilter.asStateFlow()

    val filteredActiveItems: StateFlow<List<ExtractedItemEntity>> = combine(
        activeItems,
        searchQuery,
        selectedCategoryFilter
    ) { items, query, category ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                (item.description?.contains(query, ignoreCase = true) == true) ||
                (item.companyOrPerson?.contains(query, ignoreCase = true) == true) ||
                (item.merchant?.contains(query, ignoreCase = true) == true) ||
                (item.productName?.contains(query, ignoreCase = true) == true) ||
                (item.orderNumber?.contains(query, ignoreCase = true) == true)
            val matchesCategory = category == null || item.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Item & Document Selection
    private val _selectedItemForDetail = MutableStateFlow<ExtractedItemEntity?>(null)
    val selectedItemForDetail: StateFlow<ExtractedItemEntity?> = _selectedItemForDetail.asStateFlow()

    private val _selectedDocumentForDetail = MutableStateFlow<ScannedDocumentEntity?>(null)
    val selectedDocumentForDetail: StateFlow<ScannedDocumentEntity?> = _selectedDocumentForDetail.asStateFlow()

    // AI Extraction Processing Pipeline State
    private val _processingStep = MutableStateFlow(ExtractionProcessingStep.IDLE)
    val processingStep: StateFlow<ExtractionProcessingStep> = _processingStep.asStateFlow()

    private val _latestExtraction = MutableStateFlow<ExtractionResponse?>(null)
    val latestExtraction: StateFlow<ExtractionResponse?> = _latestExtraction.asStateFlow()

    // Ask LifeAdmin AI Assistant
    private val _askAiQuestion = MutableStateFlow("")
    val askAiQuestion: StateFlow<String> = _askAiQuestion.asStateFlow()

    private val _askAiAnswer = MutableStateFlow<String?>(null)
    val askAiAnswer: StateFlow<String?> = _askAiAnswer.asStateFlow()

    private val _isAskingAi = MutableStateFlow(false)
    val isAskingAi: StateFlow<Boolean> = _isAskingAi.asStateFlow()

    // Audio / Voice Assistant
    private val _isSpeakingAudioBrief = MutableStateFlow(false)
    val isSpeakingAudioBrief: StateFlow<Boolean> = _isSpeakingAudioBrief.asStateFlow()

    // Privacy Mode
    private val _isPrivacyModeActive = MutableStateFlow(false)
    val isPrivacyModeActive: StateFlow<Boolean> = _isPrivacyModeActive.asStateFlow()

    // User Notification Settings
    val isSmartRemindersEnabled = MutableStateFlow(true)
    val isVoiceBriefingsEnabled = MutableStateFlow(true)

    fun navigateTo(screen: ScreenRoute) {
        _currentScreen.value = screen
    }

    fun setAuthMode(mode: UserAuthMode) {
        _userAuthMode.value = mode
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun loginWithEmail(email: String) {
        _userAuthMode.value = UserAuthMode.EMAIL
        _userEmail.value = email.trim()
        val defaultName = email.substringBefore("@").replace(".", " ").capitalize()
        _userName.value = if (defaultName.isNotBlank()) defaultName else "Alex Morgan"
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun signUpWithEmail(name: String, email: String) {
        _userAuthMode.value = UserAuthMode.EMAIL
        _userName.value = name.trim().ifBlank { "Alex Morgan" }
        _userEmail.value = email.trim()
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun loginWithGoogle() {
        _userAuthMode.value = UserAuthMode.GOOGLE
        _userName.value = "Alex Morgan"
        _userEmail.value = "alex.morgan@gmail.com"
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun loginWithApple() {
        _userAuthMode.value = UserAuthMode.APPLE
        _userName.value = "Alex Morgan"
        _userEmail.value = "alex.morgan@icloud.com"
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun loginAsGuest() {
        _userAuthMode.value = UserAuthMode.GUEST
        _userName.value = "Guest User"
        _userEmail.value = "guest@lifeadmin.ai"
        _currentScreen.value = ScreenRoute.DASHBOARD
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ItemCategory?) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
    }

    fun selectItemForDetail(item: ExtractedItemEntity?) {
        _selectedItemForDetail.value = item
        if (item != null) {
            _currentScreen.value = ScreenRoute.TASK_DETAIL
        }
    }

    fun selectDocumentForDetail(doc: ScannedDocumentEntity?) {
        _selectedDocumentForDetail.value = doc
    }

    fun toggleItemCompletion(item: ExtractedItemEntity) {
        viewModelScope.launch {
            repository.toggleCompletion(item.id, !item.isCompleted)
        }
    }

    fun archiveItem(id: Long) {
        viewModelScope.launch {
            repository.archiveItem(id)
            if (_selectedItemForDetail.value?.id == id) {
                _selectedItemForDetail.value = null
                _currentScreen.value = ScreenRoute.DASHBOARD
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
            if (_selectedItemForDetail.value?.id == id) {
                _selectedItemForDetail.value = null
                _currentScreen.value = ScreenRoute.DASHBOARD
            }
        }
    }

    fun deleteDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteDocument(id)
            if (_selectedDocumentForDetail.value?.id == id) {
                _selectedDocumentForDetail.value = null
            }
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun snoozeItem(item: ExtractedItemEntity, snoozeLabel: String) {
        viewModelScope.launch {
            val updated = item.copy(dueDateString = "Snoozed ($snoozeLabel)")
            repository.updateItem(updated)
        }
    }

    fun togglePrivacyMode() {
        _isPrivacyModeActive.value = !_isPrivacyModeActive.value
    }

    fun askLifeAdmin(question: String) {
        _askAiQuestion.value = question
        _isAskingAi.value = true
        _currentScreen.value = ScreenRoute.ASK_AI

        viewModelScope.launch {
            val answer = repository.askLifeAdmin(question)
            _askAiAnswer.value = answer
            _isAskingAi.value = false
        }
    }

    fun launchWorkflow(workflow: WorkflowType) {
        viewModelScope.launch {
            repository.launchWorkflow(workflow)
            _currentScreen.value = ScreenRoute.DASHBOARD
        }
    }

    fun processPastedText(rawText: String) {
        viewModelScope.launch {
            _currentScreen.value = ScreenRoute.PROCESSING_RESULTS
            _processingStep.value = ExtractionProcessingStep.SCANNING_SOURCE
            delay(500)
            _processingStep.value = ExtractionProcessingStep.EXTRACTING_ENTITIES
            delay(500)
            _processingStep.value = ExtractionProcessingStep.CATEGORIZING
            delay(400)
            _processingStep.value = ExtractionProcessingStep.GENERATING_ACTION_PLAN

            val result = repository.processTextExtraction(rawText, "EMAIL_TEXT")
            _latestExtraction.value = result

            _processingStep.value = ExtractionProcessingStep.COMPLETED
        }
    }

    fun processImageUpload(bitmap: Bitmap) {
        viewModelScope.launch {
            _currentScreen.value = ScreenRoute.PROCESSING_RESULTS
            _processingStep.value = ExtractionProcessingStep.SCANNING_SOURCE
            delay(600)
            _processingStep.value = ExtractionProcessingStep.EXTRACTING_ENTITIES
            delay(600)
            _processingStep.value = ExtractionProcessingStep.CATEGORIZING
            delay(400)
            _processingStep.value = ExtractionProcessingStep.GENERATING_ACTION_PLAN

            val result = repository.processImageExtraction(bitmap, "SCREENSHOT")
            _latestExtraction.value = result

            _processingStep.value = ExtractionProcessingStep.COMPLETED
        }
    }

    fun toggleVoiceBriefingPlayback(summaryText: String) {
        if (_isSpeakingAudioBrief.value) {
            repository.stopSpeaking()
            _isSpeakingAudioBrief.value = false
        } else {
            _isSpeakingAudioBrief.value = true
            repository.speakText(summaryText)
        }
    }

    fun addNewItemManually(
        title: String,
        type: ItemType,
        category: ItemCategory,
        dueDateString: String,
        amountText: String,
        company: String,
        priority: ItemPriority
    ) {
        viewModelScope.launch {
            val amount = amountText.toDoubleOrNull()
            repository.insertItem(
                ExtractedItemEntity(
                    title = title,
                    type = type,
                    category = category,
                    dueDateString = dueDateString,
                    amount = amount,
                    companyOrPerson = company.ifBlank { null },
                    priority = priority,
                    sourceType = "MANUAL"
                )
            )
            _currentScreen.value = ScreenRoute.DASHBOARD
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            repository.clearAllData()
            _currentScreen.value = ScreenRoute.DASHBOARD
        }
    }

    // Room Task management actions (Local-first)
    fun addNewTask(
        title: String,
        description: String? = null,
        urgency: String = "MEDIUM",
        deadlineFormatted: String? = null,
        category: String = "Personal",
        actionReason: String? = null,
        relatedDocumentId: Long? = null
    ) {
        viewModelScope.launch {
            taskRepository.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    urgency = urgency,
                    deadlineFormatted = deadlineFormatted,
                    category = category,
                    actionReason = actionReason,
                    relatedDocumentId = relatedDocumentId,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }
}
