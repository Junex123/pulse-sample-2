package com.example.ui.screens.extensions

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnduranceSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EnduranceLabScreen(
    sessions: List<EnduranceSession>,
    onLogEndurance: (activity: String, distanceKm: Float, durationMins: Int, calories: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogDialog by remember { mutableStateOf(false) }

    val totalDistance = sessions.fold(0f) { acc, s -> acc + s.distanceKm }
    val totalCalories = sessions.sumOf { it.caloriesBurned }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("log_cardio_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Cardio")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("endurance_lab_screen")
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Endurance Overview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Distance", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = String.format("%.1f km", totalDistance), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text(text = "Cardio Burn", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "$totalCalories kcal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Logged Cardio Sessions (${sessions.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (sessions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No cardio sessions logged yet. Tap + to record a run or ride!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(sessions, key = { it.id }) { s ->
                    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(s.dateMillis))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (s.activityType) {
                                            "Cycling" -> Icons.AutoMirrored.Filled.DirectionsBike
                                            "Walking" -> Icons.AutoMirrored.Filled.DirectionsWalk
                                            else -> Icons.AutoMirrored.Filled.DirectionsRun
                                        },
                                        contentDescription = s.activityType,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${s.activityType} • ${s.distanceKm} km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$dateStr • ${s.durationMinutes} min • Pace: ${String.format("%.2f", s.paceMinutesPerKm)} min/km",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${s.caloriesBurned} kcal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showLogDialog) {
        var selectedActivity by remember { mutableStateOf("Running") }
        var distanceText by remember { mutableStateOf("") }
        var durationText by remember { mutableStateOf("") }
        var caloriesText by remember { mutableStateOf("") }
        val activities = listOf("Running", "Cycling", "Walking")

        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Log Cardio Activity") },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        activities.forEach { act ->
                            FilterChip(
                                selected = selectedActivity == act,
                                onClick = { selectedActivity = act },
                                label = { Text(act, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = distanceText,
                        onValueChange = { distanceText = it },
                        label = { Text("Distance (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cardio_distance_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cardio_duration_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Estimated Calories Burned") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val d = distanceText.toFloatOrNull() ?: 0f
                        val m = durationText.toIntOrNull() ?: 0
                        val c = caloriesText.toIntOrNull() ?: (m * 10)
                        if (d > 0f && m > 0) {
                            onLogEndurance(selectedActivity, d, m, c)
                            showLogDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_cardio_btn")
                ) {
                    Text("Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
