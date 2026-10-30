package com.example.ui.screens.extensions.exercisestudio

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise3d
import com.example.data.model.WorkoutRoutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseStudioDetailScreen(
    exercise: Exercise3d?,
    routines: List<WorkoutRoutine>,
    onBack: () -> Unit,
    onStartWorkoutWithExercise: (Exercise3d) -> Unit,
    onAddExerciseToRoutine: (routineId: String, exerciseId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    if (exercise == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Exercise not found.")
        }
        return
    }

    var showAddToRoutineDialog by remember { mutableStateOf(false) }
    var selectedRoutineId by remember { mutableStateOf(routines.firstOrNull()?.id ?: "") }
    var showRoutineAddedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = exercise.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${exercise.category} • ${exercise.equipment}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("exercise_detail_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(
                                color = when (exercise.difficulty) {
                                    "Beginner" -> Color(0xFFE8F5E9)
                                    "Intermediate" -> Color(0xFFFFF8E1)
                                    else -> Color(0xFFFFEBEE)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = exercise.difficulty,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (exercise.difficulty) {
                                "Beginner" -> Color(0xFF2E7D32)
                                "Intermediate" -> Color(0xFFF57F17)
                                else -> Color(0xFFC62828)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("exercise_studio_detail_screen")
        ) {
            // Main 3D Animation Viewer
            Exercise3dViewer(
                exercise = exercise,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Start Logging & Add to Routine
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onStartWorkoutWithExercise(exercise) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_workout_with_exercise_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log in Workout", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showAddToRoutineDialog = true },
                    modifier = Modifier.testTag("add_to_routine_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Routine")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Summary description
            Text(
                text = exercise.summary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Target Muscles Section
            DetailSectionCard(
                title = "Anatomical Muscle Engagement",
                icon = Icons.Default.FitnessCenter,
                iconTint = Color(0xFFFF5722)
            ) {
                Text(
                    text = "PRIMARY TARGET (PRIME MOVER)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                exercise.primaryMuscles.forEach { muscle ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5722))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = muscle.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = muscle.commonName, fontSize = 12.sp, color = Color(0xFFFF7043), fontWeight = FontWeight.SemiBold)
                            Text(text = muscle.anatomicalFunction, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (exercise.secondaryMuscles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SECONDARY SYNERGISTS & STABILIZERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26A69A),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    exercise.secondaryMuscles.forEach { muscle ->
                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF26A69A))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = muscle.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = muscle.commonName, fontSize = 12.sp, color = Color(0xFF4DB6AC), fontWeight = FontWeight.SemiBold)
                                Text(text = muscle.anatomicalFunction, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step-by-Step Instructions
            DetailSectionCard(
                title = "Technique & Execution",
                icon = Icons.Default.CheckCircle,
                iconTint = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "SETUP & STARTING POSITION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                exercise.setupInstructions.forEachIndexed { idx, step ->
                    StepItemRow(number = idx + 1, text = step)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MOVEMENT EXECUTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                exercise.executionSteps.forEachIndexed { idx, step ->
                    StepItemRow(number = idx + 1, text = step)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breathing Guidance
            DetailSectionCard(
                title = "Breathing Pattern",
                icon = Icons.Default.Air,
                iconTint = Color(0xFF0284C7)
            ) {
                Text(
                    text = exercise.breathingGuidance,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Common Mistakes
            DetailSectionCard(
                title = "Common Form Mistakes",
                icon = Icons.Default.Warning,
                iconTint = Color(0xFFD97706)
            ) {
                exercise.commonMistakes.forEach { mistake ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Text("• ", fontWeight = FontWeight.Bold, color = Color(0xFFD97706), fontSize = 14.sp)
                        Text(text = mistake, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Notes
            DetailSectionCard(
                title = "Safety & Injury Prevention",
                icon = Icons.Default.Security,
                iconTint = Color(0xFFDC2626)
            ) {
                Text(
                    text = exercise.safetyNotes,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset Licensing & Attribution Section
            DetailSectionCard(
                title = "3D Asset Specification & Licensing",
                icon = Icons.Default.Info,
                iconTint = Color(0xFF6366F1)
            ) {
                val meta = exercise.assetMetadata
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Engine Format: ${meta.engineType.name}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Provider: ${meta.providerName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "License: ${meta.licenseType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Attribution: ${meta.attribution}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Commercial API Support: Provider interface supports streaming WorkoutX, RepDB, and MoveKit GLB models without client APK code changes.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Add To Routine Dialog
    if (showAddToRoutineDialog) {
        AlertDialog(
            onDismissRequest = { showAddToRoutineDialog = false },
            title = { Text("Add ${exercise.name} to Routine") },
            text = {
                if (routines.isEmpty()) {
                    Text("No routines found. Create a routine in Routine Builder first.")
                } else {
                    Column {
                        Text("Select routine to add this exercise to:", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        routines.forEach { r ->
                            val isSelected = selectedRoutineId == r.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .padding(10.dp)
                                    .testTag("routine_option_${r.id}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedRoutineId = r.id }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = r.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${r.category} • ${r.exerciseIds.size} exercises", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (routines.isNotEmpty()) {
                    Button(
                        onClick = {
                            onAddExerciseToRoutine(selectedRoutineId, exercise.id)
                            showAddToRoutineDialog = false
                            showRoutineAddedSnackbar = true
                        },
                        modifier = Modifier.testTag("confirm_add_to_routine_btn")
                    ) {
                        Text("Add")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddToRoutineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun StepItemRow(number: Int, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
