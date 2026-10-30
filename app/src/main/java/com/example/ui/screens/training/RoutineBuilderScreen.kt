package com.example.ui.screens.training

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise

@Composable
fun RoutineBuilderScreen(
    allExercises: List<Exercise>,
    onSaveRoutine: (name: String, desc: String, category: String, exerciseIds: List<String>, estimatedMins: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var routineName by remember { mutableStateOf("") }
    var routineDesc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Full Body") }
    var estimatedMinutesText by remember { mutableStateOf("45") }
    var selectedExerciseIds by remember { mutableStateOf(setOf<String>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Full Body", "Upper Body", "Lower Body", "Push", "Pull", "Custom")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("routine_builder_screen")
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Text(
                    text = "Create Custom Routine",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Build a structured workout routine tailored to your weekly split.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = routineName,
                    onValueChange = {
                        routineName = it
                        errorMessage = null
                    },
                    label = { Text("Routine Name (e.g. Chest & Tricep Blast)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("routine_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = routineDesc,
                    onValueChange = { routineDesc = it },
                    label = { Text("Short Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("routine_desc_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Exercises (${selectedExerciseIds.size} selected)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(allExercises, key = { it.id }) { ex ->
                val isSelected = ex.id in selectedExerciseIds
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedExerciseIds = if (isSelected) {
                                selectedExerciseIds - ex.id
                            } else {
                                selectedExerciseIds + ex.id
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = ex.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${ex.targetMuscle} • ${ex.equipment}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                selectedExerciseIds = if (checked) selectedExerciseIds + ex.id else selectedExerciseIds - ex.id
                            }
                        )
                    }
                }
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (routineName.isBlank()) {
                    errorMessage = "Please enter a routine name"
                    return@Button
                }
                if (selectedExerciseIds.isEmpty()) {
                    errorMessage = "Please select at least 1 exercise"
                    return@Button
                }
                val estMins = estimatedMinutesText.toIntOrNull() ?: 45
                onSaveRoutine(routineName, routineDesc, selectedCategory, selectedExerciseIds.toList(), estMins)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_routine_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Routine", fontWeight = FontWeight.Bold)
        }
    }
}
