package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PulseBottomBar
import com.example.ui.components.PulseTopBar
import com.example.ui.screens.aicoach.AiCoachScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.extensions.EnduranceLabScreen
import com.example.ui.screens.extensions.ExtensionManagementScreen
import com.example.ui.screens.extensions.HabitBuilderScreen
import com.example.ui.screens.extensions.NutritionLabScreen
import com.example.ui.screens.extensions.ProgressAnalyticsScreen
import com.example.ui.screens.extensions.RecoveryStudioScreen
import com.example.ui.screens.extensions.exercisestudio.ExerciseStudioDetailScreen
import com.example.ui.screens.extensions.exercisestudio.ExerciseStudioScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.training.ActiveWorkoutScreen
import com.example.ui.screens.training.ExerciseLibraryScreen
import com.example.ui.screens.training.RoutineBuilderScreen
import com.example.ui.screens.training.TrainingScreen
import com.example.ui.screens.training.WorkoutHistoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.PulseViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isAppReady by viewModel.isAppReady.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val allExtensions by viewModel.allExtensions.collectAsStateWithLifecycle()
            val enabledExtensions by viewModel.enabledExtensions.collectAsStateWithLifecycle()
            val allRoutines by viewModel.allRoutines.collectAsStateWithLifecycle()
            val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
            val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
            val activeDraft by viewModel.activeDraft.collectAsStateWithLifecycle()
            val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
            val elapsedSeconds by viewModel.sessionElapsedSeconds.collectAsStateWithLifecycle()
            val isRestTimerActive by viewModel.isRestTimerActive.collectAsStateWithLifecycle()
            val restRemainingSeconds by viewModel.restTimerRemainingSeconds.collectAsStateWithLifecycle()
            val restTotalSeconds by viewModel.restTimerTotalSeconds.collectAsStateWithLifecycle()

            val nutritionEntries by viewModel.nutritionEntries.collectAsStateWithLifecycle()
            val recoveryEntries by viewModel.recoveryEntries.collectAsStateWithLifecycle()
            val habits by viewModel.habits.collectAsStateWithLifecycle()
            val weightMeasurements by viewModel.weightMeasurements.collectAsStateWithLifecycle()
            val enduranceSessions by viewModel.enduranceSessions.collectAsStateWithLifecycle()

            val coachMessages by viewModel.coachMessages.collectAsStateWithLifecycle()
            val isCoachGenerating by viewModel.isCoachGenerating.collectAsStateWithLifecycle()

            val allExercises3d by viewModel.allExercises3d.collectAsStateWithLifecycle()
            val selectedExercise3d by viewModel.selectedExercise3d.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = userProfile.isDarkMode) {
                if (!isAppReady) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pulse Health",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else if (!userProfile.hasCompletedOnboarding) {
                    OnboardingScreen(
                        currentProfile = userProfile,
                        onComplete = { viewModel.completeOnboarding(it) },
                        onSkip = { viewModel.completeOnboarding(userProfile) }
                    )
                } else {
                    // Handle system back navigation
                    BackHandler(enabled = currentScreen != AppDestination.HOME) {
                        viewModel.navigateBack()
                    }

                    if (currentScreen == AppDestination.ACTIVE_WORKOUT) {
                        ActiveWorkoutScreen(
                            session = activeSession,
                            elapsedSeconds = elapsedSeconds,
                            isRestTimerActive = isRestTimerActive,
                            restRemainingSeconds = restRemainingSeconds,
                            restTotalSeconds = restTotalSeconds,
                            allExercises = viewModel.allExercises,
                            onAddExercise = { viewModel.addExerciseToActiveWorkout(it) },
                            onAddSet = { id, name -> viewModel.addSetToExercise(id, name) },
                            onUpdateSet = { idx, w, r, rpe -> viewModel.updateSet(idx, w, r, rpe) },
                            onToggleSetCompleted = { idx -> viewModel.toggleSetCompleted(idx) },
                            onAddRestSeconds = { sec -> viewModel.startRestTimer(sec) },
                            onDismissRestTimer = { viewModel.pauseOrDismissRestTimer() },
                            onFinishWorkout = { notes -> viewModel.finishWorkout(notes) },
                            onCancelWorkout = { viewModel.cancelActiveWorkout() }
                        )
                    } else {
                        val showBottomNav = currentScreen in listOf(
                            AppDestination.HOME,
                            AppDestination.TRAINING,
                            AppDestination.AI_COACH,
                            AppDestination.EXPLORE
                        )

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                PulseTopBar(
                                    currentDestination = currentScreen,
                                    activeExtensionCount = enabledExtensions.size,
                                    onBack = { viewModel.navigateBack() },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            },
                            bottomBar = {
                                if (showBottomNav) {
                                    PulseBottomBar(
                                        currentDestination = currentScreen,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            val screenModifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)

                            when (currentScreen) {
                                AppDestination.HOME -> {
                                    HomeScreen(
                                        profile = userProfile,
                                        sessions = allSessions,
                                        enabledExtensions = enabledExtensions,
                                        nutritionEntries = nutritionEntries,
                                        recoveryEntries = recoveryEntries,
                                        habits = habits,
                                        onStartQuickWorkout = { viewModel.startWorkout(allRoutines.firstOrNull()) },
                                        onLogWeight = { viewModel.logWeight(it) },
                                        onToggleHabit = { viewModel.toggleHabit(it) },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onUpdateDashboardCards = { viewModel.setDashboardCards(it) },
                                        activeDraft = activeDraft,
                                        onResumeDraft = { viewModel.resumeDraft(it) },
                                        onDiscardDraft = { viewModel.discardDraft() },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.TRAINING -> {
                                    TrainingScreen(
                                        routines = allRoutines,
                                        recentSessions = allSessions,
                                        onStartRoutine = { viewModel.startWorkout(it) },
                                        onStartBlankWorkout = { viewModel.startWorkout(null) },
                                        onDeleteRoutine = { viewModel.deleteRoutine(it) },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        activeDraft = activeDraft,
                                        onResumeDraft = { viewModel.resumeDraft(it) },
                                        onDiscardDraft = { viewModel.discardDraft() },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.AI_COACH -> {
                                    AiCoachScreen(
                                        messages = coachMessages,
                                        isGenerating = isCoachGenerating,
                                        onSendMessage = { viewModel.sendCoachMessage(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.EXPLORE -> {
                                    ExploreScreen(
                                        extensions = allExtensions,
                                        onInstallExtension = { viewModel.installExtension(it) },
                                        onToggleExtension = { id, enabled -> viewModel.toggleExtensionEnabled(id, enabled) },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.SETTINGS -> {
                                    SettingsScreen(
                                        profile = userProfile,
                                        onUpdateProfile = { viewModel.updateProfile(it) },
                                        onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                        onToggleDemoData = { viewModel.toggleDemoData(it) },
                                        onToggleAiConsent = { viewModel.toggleAiConsent(it) },
                                        onPurgeDemoData = { viewModel.purgeDemoData() },
                                        onDeleteAllData = { viewModel.deleteAllData() },
                                        onExportData = { viewModel.exportAllDataAsJson() },
                                        onNavigate = { viewModel.navigateTo(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.EXTENSION_MANAGEMENT -> {
                                    ExtensionManagementScreen(
                                        installedExtensions = allExtensions.filter { it.isInstalled },
                                        onToggleExtension = { id, enabled -> viewModel.toggleExtensionEnabled(id, enabled) },
                                        onUninstallExtension = { viewModel.uninstallExtension(it) },
                                        onNavigateToExtension = { viewModel.navigateTo(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.EXERCISE_LIBRARY -> {
                                    ExerciseLibraryScreen(
                                        exercises = viewModel.allExercises,
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.ROUTINE_BUILDER -> {
                                    RoutineBuilderScreen(
                                        allExercises = viewModel.allExercises,
                                        onSaveRoutine = { name, desc, cat, exIds, mins ->
                                            viewModel.saveCustomRoutine(name, desc, cat, exIds, mins)
                                            viewModel.navigateBack()
                                        },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.WORKOUT_HISTORY -> {
                                    WorkoutHistoryScreen(
                                        sessions = allSessions,
                                        personalRecords = personalRecords,
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.NUTRITION_LAB -> {
                                    NutritionLabScreen(
                                        entries = nutritionEntries,
                                        onLogFood = { name, type, cal, p, c, f ->
                                            viewModel.logNutrition(name, type, cal, p, c, f)
                                        },
                                        onDeleteEntry = { viewModel.deleteNutrition(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.RECOVERY_STUDIO -> {
                                    RecoveryStudioScreen(
                                        entries = recoveryEntries,
                                        onLogRecovery = { s, q, sor, sc, n ->
                                            viewModel.logRecovery(s, q, sor, sc, n)
                                        },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.HABIT_BUILDER -> {
                                    HabitBuilderScreen(
                                        habits = habits,
                                        onToggleHabit = { viewModel.toggleHabit(it) },
                                        onAddHabit = { title, count -> viewModel.addHabit(title, count) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.PROGRESS_ANALYTICS -> {
                                    ProgressAnalyticsScreen(
                                        weightMeasurements = weightMeasurements,
                                        sessions = allSessions,
                                        personalRecords = personalRecords,
                                        onLogWeight = { viewModel.logWeight(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.ENDURANCE_LAB -> {
                                    EnduranceLabScreen(
                                        sessions = enduranceSessions,
                                        onLogEndurance = { act, d, dur, c ->
                                            viewModel.logEndurance(act, d, dur, c)
                                        },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.ONBOARDING -> {
                                    OnboardingScreen(
                                        currentProfile = userProfile,
                                        onComplete = { viewModel.completeOnboarding(it) },
                                        onSkip = { viewModel.completeOnboarding(userProfile) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.ACTIVE_WORKOUT -> {
                                    // Managed in full-screen above
                                }
                                AppDestination.EXERCISE_STUDIO_3D -> {
                                    ExerciseStudioScreen(
                                        exercises = allExercises3d,
                                        onSelectExercise = { viewModel.selectExercise3d(it) },
                                        modifier = screenModifier
                                    )
                                }
                                AppDestination.EXERCISE_STUDIO_DETAIL -> {
                                    ExerciseStudioDetailScreen(
                                        exercise = selectedExercise3d,
                                        routines = allRoutines,
                                        onBack = { viewModel.navigateBack() },
                                        onStartWorkoutWithExercise = { viewModel.startWorkoutWith3dExercise(it) },
                                        onAddExerciseToRoutine = { rId, exId -> viewModel.addExerciseToRoutine(rId, exId) },
                                        modifier = screenModifier
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
