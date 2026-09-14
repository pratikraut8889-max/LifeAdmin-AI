package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CalendarPlannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPermissionsScreen
import com.example.ui.screens.ProcessingResultScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ScanUploadScreen
import com.example.ui.screens.SearchFilterScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.theme.LifeAdminTheme
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

@Composable
fun LifeAdminApp(viewModel: LifeAdminViewModel) {
    val currentRoute by viewModel.currentScreen.collectAsState()

    Crossfade(
        targetState = currentRoute,
        animationSpec = tween(300),
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            ScreenRoute.SPLASH -> SplashScreen(
                onFinishSplash = { viewModel.navigateTo(it) }
            )
            ScreenRoute.ONBOARDING -> OnboardingScreen(
                onGetStarted = { viewModel.navigateTo(ScreenRoute.AUTH) },
                onSkip = { viewModel.navigateTo(ScreenRoute.DASHBOARD) }
            )
            ScreenRoute.AUTH -> AuthScreen(
                viewModel = viewModel
            )
            ScreenRoute.DASHBOARD -> DashboardScreen(
                viewModel = viewModel
            )
            ScreenRoute.SCAN_UPLOAD -> ScanUploadScreen(
                viewModel = viewModel
            )
            ScreenRoute.PROCESSING_RESULTS -> ProcessingResultScreen(
                viewModel = viewModel
            )
            ScreenRoute.TASK_DETAIL -> TaskDetailScreen(
                viewModel = viewModel
            )
            ScreenRoute.CALENDAR -> CalendarPlannerScreen(
                viewModel = viewModel
            )
            ScreenRoute.SEARCH -> SearchFilterScreen(
                viewModel = viewModel
            )
            ScreenRoute.NOTIFICATIONS -> NotificationsScreen(
                viewModel = viewModel
            )
            ScreenRoute.PROFILE -> ProfileSettingsScreen(
                viewModel = viewModel
            )
            ScreenRoute.PRIVACY -> PrivacyPermissionsScreen(
                viewModel = viewModel
            )
        }
    }
}
