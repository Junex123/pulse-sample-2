package com.example.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitItem
import com.example.data.model.NutritionEntry
import com.example.data.model.RecoveryEntry
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.repository.ExtensionItemUi
import com.example.ui.viewmodel.AppDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profile: UserProfile,
    sessions: List<WorkoutSession>,
    enabledExtensions: List<ExtensionItemUi>,
    nutritionEntries: List<NutritionEntry>,
    recoveryEntries: List<RecoveryEntry>,
    habits: List<HabitItem>,
    onStartQuickWorkout: () -> Unit,
    onLogWeight: (Float) -> Unit,
    onToggleHabit: (HabitItem) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onUpdateDashboardCards: (List<String>) -> Unit,
    activeDraft: WorkoutSession? = null,
    onResumeDraft: (WorkoutSession) -> Unit = {},
    onDiscardDraft: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(profile.weightKg.toString()) }

    var showManageSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val visibleCards = profile.visibleDashboardCards
    val isNutritionEnabled = enabledExtensions.any { it.descriptor.id == "ext_nutrition" }
    val isHabitsEnabled = enabledExtensions.any { it.descriptor.id == "ext_habits" }
    val isRecoveryEnabled = enabledExtensions.any { it.descriptor.id == "ext_recovery" }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .testTag("home_screen_lazy_column")
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GreetingCard(
                    profile = profile,
                    onManageDashboard = { showManageSheet = true }
                )
            }

            if (activeDraft != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("home_resume_active_draft_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active Workout in Progress",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "${activeDraft.sets.size} sets recorded",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Text(
                                text = activeDraft.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onResumeDraft(activeDraft) },
                                    modifier = Modifier.weight(1f).testTag("home_resume_draft_btn")
                                ) {
                                    Text("Resume Session", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = onDiscardDraft,
                                    modifier = Modifier.testTag("home_discard_draft_btn")
                                ) {
                                    Text("Discard")
                                }
                            }
                        }
                    }
                }
            }

            if ("daily_summary" in visibleCards) {
                item {
                    val completed = sessions.filter { it.isCompleted }
                    val activeMins = completed.sumOf { it.durationMinutes }
                    val estCals = completed.sumOf { it.durationMinutes * 6 }
                    DailySummaryCard(
                        sessionsThisWeek = completed.size,
                        targetFrequency = profile.preferredFrequencyDays,
                        totalActiveMinutes = activeMins,
                        estimatedCalories = estCals,
                        isDemo = profile.isDemoDataEnabled
                    )
                }
            }

            if ("quick_workout" in visibleCards) {
                item {
                    QuickWorkoutCard(onStartQuickWorkout = onStartQuickWorkout)
                }
            }

            // Extension: Nutrition Lab
            if (isNutritionEnabled && "ext_nutrition" in visibleCards) {
                item {
                    NutritionLabDashboardCard(
                        entries = nutritionEntries,
                        onOpenNutrition = { onNavigate(AppDestination.NUTRITION_LAB) }
                    )
                }
            }

            // Extension: Habits Builder
            if (isHabitsEnabled && "ext_habits" in visibleCards) {
                item {
                    HabitBuilderDashboardCard(
                        habits = habits,
                        onToggleHabit = onToggleHabit,
                        onOpenHabits = { onNavigate(AppDestination.HABIT_BUILDER) }
                    )
                }
            }

            // Extension: Recovery Studio
            if (isRecoveryEnabled && "ext_recovery" in visibleCards) {
                item {
                    RecoveryStudioDashboardCard(
                        latestRecovery = recoveryEntries.firstOrNull(),
                        onOpenRecovery = { onNavigate(AppDestination.RECOVERY_STUDIO) }
                    )
                }
            }

            if ("weight_activity" in visibleCards) {
                item {
                    WeightAndActivityCard(
                        currentWeightKg = profile.weightKg,
                        onQuickLogWeight = { showWeightDialog = true }
                    )
                }
            }

            if ("weekly_progress" in visibleCards) {
                item {
                    WeeklyProgressPreviewCard(sessions = sessions)
                }
            }

            item {
                AddToolsCard(onExplore = { onNavigate(AppDestination.EXPLORE) })
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Quick Weight Log Dialog
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Log Body Weight") },
            text = {
                Column {
                    Text(
                        text = "Enter current weight in ${profile.weightUnit}:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (${profile.weightUnit})") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("weight_input_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = weightInput.toFloatOrNull()
                        if (parsed != null && parsed > 0f) {
                            onLogWeight(parsed)
                        }
                        showWeightDialog = false
                    },
                    modifier = Modifier.testTag("weight_dialog_save_btn")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dashboard Customization Bottom Sheet
    if (showManageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showManageSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .testTag("manage_dashboard_sheet")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showManageSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text(
                    text = "Toggle which cards appear on your main home dashboard.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                val availableCards = listOf(
                    "daily_summary" to "Weekly Activity Summary",
                    "quick_workout" to "Quick Workout Launch",
                    "weight_activity" to "Weight & Activity Widget",
                    "weekly_progress" to "Consistency Tracker",
                    "ext_nutrition" to "Nutrition Lab (Requires Extension)",
                    "ext_habits" to "Habit Tracker (Requires Extension)",
                    "ext_recovery" to "Recovery Studio (Requires Extension)"
                )

                availableCards.forEach { (key, label) ->
                    val isChecked = key in visibleCards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val updated = if (checked) {
                                    (visibleCards + key).distinct()
                                } else {
                                    visibleCards.filter { it != key }
                                }
                                onUpdateDashboardCards(updated)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = {
                        onUpdateDashboardCards(
                            listOf("daily_summary", "quick_workout", "weight_activity", "weekly_progress", "ext_nutrition", "ext_habits", "ext_recovery")
                        )
                    }
                ) {
                    Text("Reset to Default Layout")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
