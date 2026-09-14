package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RoyalBlue600
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun CustomBottomNav(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("custom_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Dashboard
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.DASHBOARD,
            onClick = { onNavigate(ScreenRoute.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Today") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        // Planner / Calendar
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.CALENDAR,
            onClick = { onNavigate(ScreenRoute.CALENDAR) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "Calendar"
                )
            },
            label = { Text("Planner") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_calendar")
        )

        // FAB Center Scan/Upload Accent Button
        FloatingActionButton(
            onClick = { onNavigate(ScreenRoute.SCAN_UPLOAD) },
            containerColor = RoyalBlue600,
            contentColor = androidx.compose.ui.graphics.Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            modifier = Modifier
                .size(52.dp)
                .testTag("fab_scan_upload")
        ) {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = "Scan or Upload Content",
                modifier = Modifier.size(24.dp)
            )
        }

        // Search & Filter
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.SEARCH,
            onClick = { onNavigate(ScreenRoute.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_search")
        )

        // Profile / Settings
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.PROFILE,
            onClick = { onNavigate(ScreenRoute.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}
