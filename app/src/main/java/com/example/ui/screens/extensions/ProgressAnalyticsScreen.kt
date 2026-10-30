package com.example.ui.screens.extensions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonalRecord
import com.example.data.model.WeightMeasurement
import com.example.data.model.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressAnalyticsScreen(
    weightMeasurements: List<WeightMeasurement>,
    sessions: List<WorkoutSession>,
    personalRecords: List<PersonalRecord>,
    onLogWeight: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("log_weight_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Weight")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("progress_analytics_screen")
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Weight Trend Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Body Weight Trend (kg)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            val latestWeight = weightMeasurements.firstOrNull()?.weightKg ?: 72.5f
                            Text(
                                text = "$latestWeight kg",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Trend Chart via Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("weight_trend_canvas")
                        ) {
                            val w = size.width
                            val h = size.height

                            // Draw baseline grid lines
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, h * 0.25f),
                                end = Offset(w, h * 0.25f),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, h * 0.75f),
                                end = Offset(w, h * 0.75f),
                                strokeWidth = 1f
                            )

                            // Sample points curve
                            val path = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.25f, h * 0.65f, w * 0.5f, h * 0.45f, w * 0.75f, h * 0.4f)
                                lineTo(w, h * 0.35f)
                            }
                            drawPath(
                                path = path,
                                color = primaryColor,
                                style = Stroke(width = 6f)
                            )
                            // Draw end dot
                            drawCircle(
                                color = primaryColor,
                                radius = 8f,
                                center = Offset(w, h * 0.35f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Consistent downward/steady trend aligned with goal.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Training Volume Load
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Volume Load Progression",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val totalVol = sessions.sumOf { it.totalVolumeKg.toDouble() }.toInt()
                        Text(
                            text = "$totalVol kg total volume lifted",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "${sessions.size} completed workouts tracked in engine",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Weight Log History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(weightMeasurements) { entry ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.dateMillis))
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = dateStr, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (entry.notes.isNotBlank()) {
                                Text(text = entry.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Text(
                            text = "${entry.weightKg} kg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showLogDialog) {
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Log Body Weight") },
            text = {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("weight_dialog_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = weightInput.toFloatOrNull()
                        if (parsed != null && parsed > 0f) {
                            onLogWeight(parsed)
                        }
                        showLogDialog = false
                    }
                ) {
                    Text("Save")
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
