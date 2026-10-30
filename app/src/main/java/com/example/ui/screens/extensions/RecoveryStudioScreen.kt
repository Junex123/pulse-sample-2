package com.example.ui.screens.extensions

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.RecoveryEntry
import com.example.ui.theme.SuccessGreen

@Composable
fun RecoveryStudioScreen(
    entries: List<RecoveryEntry>,
    onLogRecovery: (sleepHours: Float, quality: Int, soreness: Int, score: Int, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sleepHours by remember { mutableFloatStateOf(7.5f) }
    var sleepQuality by remember { mutableIntStateOf(4) }
    var sorenessLevel by remember { mutableIntStateOf(2) }
    var notesText by remember { mutableStateOf("") }
    var justLoggedMessage by remember { mutableStateOf<String?>(null) }

    // Subjective Readiness score calculation:
    // Base 60 + sleep quality*6 (up to 30) - soreness*6 (up to 30) + (sleepHours - 7)*4
    val computedScore = remember(sleepHours, sleepQuality, sorenessLevel) {
        val base = 50
        val sleepBonus = (sleepQuality * 8)
        val sorenessDeduction = (sorenessLevel * 5)
        val hoursBonus = ((sleepHours - 7f) * 4).toInt()
        (base + sleepBonus - sorenessDeduction + hoursBonus).coerceIn(20, 100)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("recovery_studio_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Readiness Score Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estimated Readiness Score",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$computedScore / 100",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (computedScore >= 80) SuccessGreen else MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = if (computedScore >= 80) "Optimal for high neurological load & PR attempts"
                        else if (computedScore >= 60) "Good for moderate volume hypertrophy training"
                        else "Consider deload, light technique, or active recovery",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recovery Check-in Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Daily Recovery Check-In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sleep Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sleep Duration", fontSize = 13.sp)
                        Text(text = String.format("%.1f hrs", sleepHours), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = sleepHours,
                        onValueChange = { sleepHours = it },
                        valueRange = 4f..11f,
                        steps = 13,
                        modifier = Modifier.fillMaxWidth().testTag("sleep_hours_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sleep Quality (1-5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sleep Quality", fontSize = 13.sp)
                        Row {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= sleepQuality) Color(0xFFF59E0B) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .clickable { sleepQuality = star }
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Muscle Soreness (1-5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Muscle Soreness Level", fontSize = 13.sp)
                        Text(
                            text = when (sorenessLevel) {
                                1 -> "None (1/5)"
                                2 -> "Mild (2/5)"
                                3 -> "Moderate (3/5)"
                                4 -> "High (4/5)"
                                else -> "Severe (5/5)"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Slider(
                        value = sorenessLevel.toFloat(),
                        onValueChange = { sorenessLevel = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth().testTag("soreness_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("How do you feel today? (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onLogRecovery(sleepHours, sleepQuality, sorenessLevel, computedScore, notesText)
                            justLoggedMessage = "Logged! Readiness: $computedScore/100"
                            notesText = ""
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_recovery_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Today's Check-In", fontWeight = FontWeight.Bold)
                    }

                    justLoggedMessage?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(it, color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Past Check-Ins (${entries.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(entries, key = { it.id }) { item ->
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
                        Text(
                            text = "${item.dateIso} • Sleep: ${item.sleepHours}h",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Quality: ${item.sleepQuality}/5 • Soreness: ${item.sorenessLevel}/5",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${item.readinessScore}/100",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (item.readinessScore >= 80) SuccessGreen else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
