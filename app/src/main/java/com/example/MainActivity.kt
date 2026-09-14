package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AskLifeAdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CalendarPlannerScreen
import com.example.ui.screens.DocumentMemoryScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPermissionsScreen
import com.example.ui.screens.ProcessingResultScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.RenewalsScreen
import com.example.ui.screens.ScanUploadScreen
import com.example.ui.screens.SearchFilterScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WorkflowsScreen
import com.example.ui.theme.LifeAdminTheme
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.LifeAdminViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : ComponentActivity() {

    private val viewModel: LifeAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeAdminTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LifeAdminApp(viewModel = viewModel)
                }
            }
        }
    }
}

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Today : BottomNavScreen(
        route = "today",
        title = "Today",
        selectedIcon = Icons.Filled.CalendarToday,
        unselectedIcon = Icons.Outlined.CalendarToday,
        testTag = "nav_today"
    )

    object Documents : BottomNavScreen(
        route = "documents",
        title = "Documents",
        selectedIcon = Icons.Filled.FolderShared,
        unselectedIcon = Icons.Outlined.FolderShared,
        testTag = "nav_documents"
    )

    object AiChat : BottomNavScreen(
        route = "ai_chat",
        title = "AI Chat",
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology,
        testTag = "nav_ai_chat"
    )

    object Timeline : BottomNavScreen(
        route = "timeline",
        title = "Timeline",
        selectedIcon = Icons.Filled.Timeline,
        unselectedIcon = Icons.Outlined.Timeline,
        testTag = "nav_timeline"
    )
}

@Composable
fun LifeAdminApp(viewModel: LifeAdminViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "today"

    val bottomNavItems = listOf(
        BottomNavScreen.Today,
        BottomNavScreen.Documents,
        BottomNavScreen.AiChat,
        BottomNavScreen.Timeline
    )

    // Screens where bottom bar should be displayed
    val isBottomBarVisible = currentRoute in listOf(
        "today",
        "documents",
        "ai_chat",
        "timeline",
        "renewals",
        "workflows"
    )

    // Bridge viewModel.currentScreen changes with NavHost
    val viewModelCurrentScreen by viewModel.currentScreen.collectAsState()
    LaunchedEffect(viewModelCurrentScreen) {
        val targetRoute = when (viewModelCurrentScreen) {
            ScreenRoute.TODAY, ScreenRoute.DASHBOARD -> "today"
            ScreenRoute.DOCUMENT_MEMORY -> "documents"
            ScreenRoute.ASK_AI -> "ai_chat"
            ScreenRoute.TIMELINE -> "timeline"
            ScreenRoute.SCAN_UPLOAD -> "scan_upload"
            ScreenRoute.NOTIFICATIONS -> "notifications"
            ScreenRoute.RENEWALS -> "renewals"
            ScreenRoute.WORKFLOWS -> "workflows"
            ScreenRoute.TASK_DETAIL -> "task_detail"
            ScreenRoute.SEARCH -> "search"
            ScreenRoute.PROFILE -> "profile"
            ScreenRoute.PRIVACY -> "privacy"
            ScreenRoute.CALENDAR -> "calendar"
            ScreenRoute.AUTH -> "auth"
            ScreenRoute.ONBOARDING -> "onboarding"
            ScreenRoute.SPLASH -> "splash"
            ScreenRoute.PROCESSING_RESULTS -> "processing_results"
        }

        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue600,
                                selectedTextColor = RoyalBlue600,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "today",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main tabs
            composable("today") {
                TodayScreen(
                    viewModel = viewModel,
                    onNavigateToDocuments = { navController.navigate("documents") },
                    onNavigateToAiChat = { query ->
                        viewModel.askLifeAdmin(query)
                        navController.navigate("ai_chat")
                    },
                    onNavigateToScanUpload = { navController.navigate("scan_upload") },
                    onNavigateToNotifications = { navController.navigate("notifications") }
                )
            }

            composable("documents") {
                DocumentMemoryScreen(viewModel = viewModel)
            }

            composable("ai_chat") {
                AskLifeAdminScreen(viewModel = viewModel)
            }

            composable("timeline") {
                TimelineScreen(viewModel = viewModel)
            }

            // Secondary screens
            composable("scan_upload") {
                ScanUploadScreen(viewModel = viewModel)
            }

            composable("task_detail") {
                TaskDetailScreen(viewModel = viewModel)
            }

            composable("processing_results") {
                ProcessingResultScreen(viewModel = viewModel)
            }

            composable("notifications") {
                NotificationsScreen(viewModel = viewModel)
            }

            composable("renewals") {
                RenewalsScreen(viewModel = viewModel)
            }

            composable("workflows") {
                WorkflowsScreen(viewModel = viewModel)
            }

            composable("calendar") {
                CalendarPlannerScreen(viewModel = viewModel)
            }

            composable("search") {
                SearchFilterScreen(viewModel = viewModel)
            }

            composable("profile") {
                ProfileSettingsScreen(viewModel = viewModel)
            }

            composable("privacy") {
                PrivacyPermissionsScreen(viewModel = viewModel)
            }

            composable("auth") {
                AuthScreen(viewModel = viewModel)
            }

            composable("onboarding") {
                OnboardingScreen(
                    onGetStarted = { navController.navigate("auth") },
                    onSkip = { navController.navigate("today") }
                )
            }

            composable("splash") {
                SplashScreen(
                    onFinishSplash = { navController.navigate("today") }
                )
            }
        }
    }
}
