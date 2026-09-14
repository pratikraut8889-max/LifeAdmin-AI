package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timeline
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
        // Today / Dashboard
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.TODAY || currentRoute == ScreenRoute.DASHBOARD,
            onClick = { onNavigate(ScreenRoute.TODAY) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.TODAY || currentRoute == ScreenRoute.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "Today"
                )
            },
            label = { Text("Today") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        // Timeline
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.TIMELINE,
            onClick = { onNavigate(ScreenRoute.TIMELINE) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.TIMELINE) Icons.Filled.Timeline else Icons.Outlined.Timeline,
                    contentDescription = "Timeline"
                )
            },
            label = { Text("Timeline") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_timeline")
        )

        // Life Inbox FAB (Center)
        FloatingActionButton(
            onClick = { onNavigate(ScreenRoute.SCAN_UPLOAD) },
            containerColor = RoyalBlue600,
            contentColor = androidx.compose.ui.graphics.Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            modifier = Modifier
                .size(52.dp)
                .testTag("fab_scan_upload")
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Life Inbox",
                modifier = Modifier.size(26.dp)
            )
        }

        // Document Memory
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.DOCUMENT_MEMORY,
            onClick = { onNavigate(ScreenRoute.DOCUMENT_MEMORY) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.DOCUMENT_MEMORY) Icons.Filled.FolderShared else Icons.Outlined.FolderShared,
                    contentDescription = "Documents"
                )
            },
            label = { Text("Documents") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_documents")
        )

        // Ask AI
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.ASK_AI,
            onClick = { onNavigate(ScreenRoute.ASK_AI) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.ASK_AI) Icons.Filled.Psychology else Icons.Outlined.Psychology,
                    contentDescription = "Ask AI"
                )
            },
            label = { Text("Ask AI") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RoyalBlue600,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_ask_ai")
        )
    }
}
