package com.example.ui.screens.training

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import com.example.ui.components.RestTimerSheet
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    session: WorkoutSession?,
    elapsedSeconds: Int,
    isRestTimerActive: Boolean,
    restRemainingSeconds: Int,
    restTotalSeconds: Int,
    allExercises: List<Exercise>,
    onAddExercise: (Exercise) -> Unit,
    onAddSet: (exerciseId: String, exerciseName: String) -> Unit,
    onUpdateSet: (index: Int, weight: Float, reps: Int, rpe: Int) -> Unit,
    onToggleSetCompleted: (index: Int) -> Unit,
    onAddRestSeconds: (Int) -> Unit,
    onDismissRestTimer: () -> Unit,
    onFinishWorkout: (notes: String) -> Unit,
    onCancelWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (session == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active session.")
        }
        return
    }

    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var isFinishing by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }
    var showAddExerciseSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    BackHandler { showCancelDialog = true }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timerStr = String.format("%02d:%02d", minutes, seconds)

    // Map sets with their true global index in session.sets to prevent indexOf collision bugs
    val indexedSets = session.sets.mapIndexed { index, set -> index to set }
    val setsByExercise = indexedSets.groupBy { it.second.exerciseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = session.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timerStr,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.testTag("workout_cancel_icon_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("finish_workout_header_btn")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            RestTimerSheet(
                isActive = isRestTimerActive,
                remainingSeconds = restRemainingSeconds,
                totalSeconds = restTotalSeconds,
                onAddSeconds = onAddRestSeconds,
                onDismiss = onDismissRestTimer
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("active_workout_sets_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            setsByExercise.forEach { (exerciseId, exerciseSets) ->
                val firstSet = exerciseSets.first().second
                item(key = exerciseId) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = firstSet.exerciseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${exerciseSets.count { it.second.isCompleted }}/${exerciseSets.size} sets",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp))
                                Text("KG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(60.dp))
                                Text("REPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(60.dp))
                                Text("RPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(50.dp))
                                Text("DONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(44.dp))
                            }

                            // Sets rows
                            exerciseSets.forEach { (globalIndex, setItem) ->
                                SetRowItem(
                                    set = setItem,
                                    onUpdate = { w, r, rpe -> onUpdateSet(globalIndex, w, r, rpe) },
                                    onToggle = { onToggleSetCompleted(globalIndex) }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onAddSet(exerciseId, firstSet.exerciseName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_set_${exerciseId}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Set")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                FilledTonalButton(
                    onClick = { showAddExerciseSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_exercise_to_workout_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Exercise to Session")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Exercise Bottom Sheet
    if (showAddExerciseSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddExerciseSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("add_exercise_sheet")
            ) {
                Text(
                    text = "Select Exercise to Add",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    items(allExercises) { ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = ex.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${ex.targetMuscle} • ${ex.equipment}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            FilledTonalIconButton(
                                onClick = {
                                    onAddExercise(ex)
                                    showAddExerciseSheet = false
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
            }
        }
    }

    // Finish Workout Dialog
    if (showFinishDialog) {
        val completedSets = session.sets.filter { it.isCompleted }
        val volume = completedSets.fold(0f) { acc, s -> acc + (s.weightKg * s.reps) }
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish & Save Workout") },
            text = {
                Column {
                    Text("Duration: $timerStr", fontWeight = FontWeight.SemiBold)
                    Text("Completed Sets: ${completedSets.size} of ${session.sets.size}")
                    Text("Total Volume: ${volume.toInt()} kg", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Session Notes (optional)") },
                        modifier = Modifier.fillMaxWidth().testTag("workout_notes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isFinishing) {
                            isFinishing = true
                            showFinishDialog = false
                            onFinishWorkout(notesText)
                        }
                    },
                    enabled = !isFinishing,
                    modifier = Modifier.testTag("confirm_finish_workout_btn")
                ) {
                    Text("Save Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Keep Training")
                }
            }
        )
    }

    // Cancel Workout Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Discard Workout?") },
            text = { Text("Are you sure you want to exit? Your active sets for this session will not be saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }
}

@Composable
fun SetRowItem(
    set: WorkoutSet,
    onUpdate: (weight: Float, reps: Int, rpe: Int) -> Unit,
    onToggle: () -> Unit
) {
    var weightText by remember(set.weightKg) { mutableStateOf(if (set.weightKg % 1f == 0f) set.weightKg.toInt().toString() else set.weightKg.toString()) }
    var repsText by remember(set.reps) { mutableStateOf(set.reps.toString()) }
    var rpeText by remember(set.rpe) { mutableStateOf(set.rpe.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (set.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Set #
        Text(
            text = "${set.setNumber}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp)
        )

        // Weight Input
        OutlinedTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                val parsed = it.toFloatOrNull()
                val w = if (parsed != null && parsed >= 0f) minOf(parsed, 1000f) else set.weightKg
                val r = repsText.toIntOrNull()?.coerceIn(1, 999) ?: set.reps
                val rpe = rpeText.toIntOrNull()?.coerceIn(1, 10) ?: set.rpe
                onUpdate(w, r, rpe)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(62.dp)
                .height(48.dp)
                .testTag("set_weight_${set.exerciseId}_${set.setNumber}")
        )

        // Reps Input
        OutlinedTextField(
            value = repsText,
            onValueChange = {
                repsText = it
                val r = it.toIntOrNull()?.coerceIn(1, 999) ?: set.reps
                val parsedW = weightText.toFloatOrNull()
                val w = if (parsedW != null && parsedW >= 0f) minOf(parsedW, 1000f) else set.weightKg
                val rpe = rpeText.toIntOrNull()?.coerceIn(1, 10) ?: set.rpe
                onUpdate(w, r, rpe)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(62.dp)
                .height(48.dp)
                .testTag("set_reps_${set.exerciseId}_${set.setNumber}")
        )

        // RPE Input
        OutlinedTextField(
            value = rpeText,
            onValueChange = {
                rpeText = it
                val rpe = it.toIntOrNull()?.coerceIn(1, 10) ?: set.rpe
                val parsedW = weightText.toFloatOrNull()
                val w = if (parsedW != null && parsedW >= 0f) minOf(parsedW, 1000f) else set.weightKg
                val r = repsText.toIntOrNull()?.coerceIn(1, 999) ?: set.reps
                onUpdate(w, r, rpe)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(52.dp)
                .height(48.dp)
                .testTag("set_rpe_${set.exerciseId}_${set.setNumber}")
        )

        // Checkbox button
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (set.isCompleted) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant)
                .testTag("set_complete_btn_${set.exerciseId}_${set.setNumber}")
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete Set",
                tint = if (set.isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
