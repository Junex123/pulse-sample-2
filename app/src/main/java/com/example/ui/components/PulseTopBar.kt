package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseTopBar(
    currentDestination: AppDestination,
    activeExtensionCount: Int,
    onBack: () -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    val isPrimaryScreen = currentDestination in listOf(
        AppDestination.HOME,
        AppDestination.TRAINING,
        AppDestination.AI_COACH,
        AppDestination.EXPLORE
    )

    val titleText = when (currentDestination) {
        AppDestination.HOME -> "Pulse"
        AppDestination.TRAINING -> "Training"
        AppDestination.AI_COACH -> "AI Coach"
        AppDestination.EXPLORE -> "Extension Hub"
        AppDestination.SETTINGS -> "Settings"
        AppDestination.EXTENSION_MANAGEMENT -> "Manage Extensions"
        AppDestination.EXERCISE_LIBRARY -> "Exercise Library"
        AppDestination.ROUTINE_BUILDER -> "Routine Builder"
        AppDestination.WORKOUT_HISTORY -> "Workout History"
        AppDestination.ACTIVE_WORKOUT -> "Active Workout"
        AppDestination.NUTRITION_LAB -> "Nutrition Lab"
        AppDestination.RECOVERY_STUDIO -> "Recovery Studio"
        AppDestination.HABIT_BUILDER -> "Habit Builder"
        AppDestination.PROGRESS_ANALYTICS -> "Progress Analytics"
        AppDestination.ENDURANCE_LAB -> "Endurance Lab"
        AppDestination.EXERCISE_STUDIO_3D -> "3D Exercise Studio"
        AppDestination.EXERCISE_STUDIO_DETAIL -> "Exercise Technique"
        AppDestination.ONBOARDING -> "Welcome to Pulse"
    }

    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentDestination == AppDestination.HOME) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = titleText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            if (!isPrimaryScreen) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("topbar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            // Extension Management Shortcut
            IconButton(
                onClick = { onNavigate(AppDestination.EXTENSION_MANAGEMENT) },
                modifier = Modifier.testTag("topbar_extensions_button")
            ) {
                BadgedBox(
                    badge = {
                        if (activeExtensionCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text("$activeExtensionCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Extension,
                        contentDescription = "Installed Extensions",
                        tint = if (currentDestination == AppDestination.EXTENSION_MANAGEMENT)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Settings Shortcut
            IconButton(
                onClick = { onNavigate(AppDestination.SETTINGS) },
                modifier = Modifier.testTag("topbar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (currentDestination == AppDestination.SETTINGS)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
