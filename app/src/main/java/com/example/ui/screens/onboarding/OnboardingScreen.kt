package com.example.ui.screens.onboarding

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile

/**
 * 4-Step Production Onboarding Flow:
 * Step 1: Basic Profile (Name, Age Range, Height, Weight)
 * Step 2: Fitness Goals (Build Muscle, Lose Fat, Improve Strength, General Fitness, Maintain)
 * Step 3: Experience & Preferences (Level, Training Frequency, Equipment)
 * Step 4: Review & Confirm (Summary with jump-back editing and Finish Setup)
 */
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    initialStep: Int = 1,
    onStepChanged: (Int, UserProfile) -> Unit = { _, _ -> },
    onComplete: (UserProfile) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(initialStep.coerceIn(1, 4)) }

    var name by remember { mutableStateOf(currentProfile.name) }
    var ageRange by remember { mutableStateOf("25-34") }
    var weightText by remember { mutableStateOf(currentProfile.weightKg.toString()) }
    var heightText by remember { mutableStateOf(currentProfile.heightCm.toInt().toString()) }

    var selectedGoal by remember { mutableStateOf(currentProfile.goal) }
    var selectedExperience by remember { mutableStateOf(currentProfile.experienceLevel) }
    var frequencyDays by remember { mutableIntStateOf(currentProfile.preferredFrequencyDays) }
    var selectedEquipment by remember { mutableStateOf(currentProfile.equipmentAvailable) }

    val goals = listOf(
        "Build Muscle" to "Focus on hypertrophy and progressive overload",
        "Lose Fat" to "Prioritize caloric expenditure and metabolic conditioning",
        "Improve Strength" to "Heavy compound lifts and neurological adaptations",
        "Improve General Fitness" to "Balanced endurance, mobility, and functional movement",
        "Maintain Current Fitness" to "Consistent training to sustain strength and health"
    )

    val experienceLevels = listOf(
        "Beginner" to "< 1 year of consistent lifting",
        "Intermediate" to "1 - 3 years of structured programming",
        "Advanced" to "3+ years mastering complex compound lifts"
    )

    val equipmentOptions = listOf(
        "Full Gym Access" to "Barbells, dumbbells, cable towers, and machines",
        "Home Dumbbells" to "Adjustable or fixed dumbbells and bench",
        "Bodyweight & Bands" to "Calisthenics, pull-up bar, and resistance bands"
    )

    val ageRanges = listOf("18-24", "25-34", "35-44", "45-54", "55+")

    fun buildCurrentProfile(): UserProfile {
        val parsedWeight = weightText.toFloatOrNull() ?: currentProfile.weightKg
        val parsedHeight = heightText.toFloatOrNull() ?: currentProfile.heightCm
        return currentProfile.copy(
            name = name.ifBlank { "Athlete" },
            goal = selectedGoal,
            experienceLevel = selectedExperience,
            equipmentAvailable = selectedEquipment,
            preferredFrequencyDays = frequencyDays,
            weightKg = parsedWeight,
            heightCm = parsedHeight,
            hasCompletedOnboarding = true
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        IconButton(
                            onClick = {
                                step--
                                onStepChanged(step, buildCurrentProfile())
                            },
                            modifier = Modifier.testTag("onboarding_back_step_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous step")
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pulse Onboarding",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.testTag("skip_onboarding_btn")
                    ) {
                        Text("Skip", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Step progress bar (1 to 4)
                LinearProgressIndicator(
                    progress = { step / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Step $step of 4",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                when (step) {
                    // STEP 1: Basic Profile
                    1 -> {
                        Text(
                            text = "Tell Us About Yourself",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Basic metrics allow Pulse to calculate accurate volume and metabolic estimates.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Age Range (Privacy-conscious)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ageRanges.forEach { range ->
                                FilterChip(
                                    selected = ageRange == range,
                                    onClick = { ageRange = range },
                                    label = { Text(range, fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_weight_input")
                            )

                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_height_input")
                            )
                        }
                    }

                    // STEP 2: Fitness Goals
                    2 -> {
                        Text(
                            text = "What is your primary fitness goal?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pulse customizes starter routines and AI suggestions based on your target focus.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        goals.forEach { (goalTitle, goalDesc) ->
                            val isSelected = selectedGoal == goalTitle
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedGoal = goalTitle }
                                    .testTag("onboarding_goal_${goalTitle.replace(" ", "_")}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = goalTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = goalDesc,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // STEP 3: Experience & Preferences
                    3 -> {
                        Text(
                            text = "Experience & Equipment",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Configure your lifting background, weekly frequency, and available training gear.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Training Experience", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        experienceLevels.forEach { (expTitle, expDesc) ->
                            val isSelected = selectedExperience == expTitle
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedExperience = expTitle },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(expTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(expDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Weekly Target Days", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2, 3, 4, 5, 6).forEach { days ->
                                FilterChip(
                                    selected = frequencyDays == days,
                                    onClick = { frequencyDays = days },
                                    label = { Text("$days days", fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Available Equipment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        equipmentOptions.forEach { (eqTitle, eqDesc) ->
                            val isSelected = selectedEquipment == eqTitle
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { selectedEquipment = eqTitle },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(eqTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(eqDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // STEP 4: Review & Finish Setup
                    4 -> {
                        Text(
                            text = "Review Your Profile",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Review your setup. You can adjust any category before entering the dashboard.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Review Card 1: Identity & Metrics
                        ReviewSummaryCard(
                            title = "Basic Profile",
                            details = listOf(
                                "Name" to name.ifBlank { "Athlete" },
                                "Age Bracket" to ageRange,
                                "Weight" to "$weightText kg",
                                "Height" to "$heightText cm"
                            ),
                            onEdit = { step = 1 }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Review Card 2: Goal
                        ReviewSummaryCard(
                            title = "Fitness Focus",
                            details = listOf(
                                "Primary Goal" to selectedGoal
                            ),
                            onEdit = { step = 2 }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Review Card 3: Experience & Schedule
                        ReviewSummaryCard(
                            title = "Training Preferences",
                            details = listOf(
                                "Experience" to selectedExperience,
                                "Frequency" to "$frequencyDays workouts/week",
                                "Equipment" to selectedEquipment
                            ),
                            onEdit = { step = 3 }
                        )
                    }
                }
            }

            // Bottom Navigation Next / Finish Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Button(
                    onClick = {
                        if (step < 4) {
                            step++
                            onStepChanged(step, buildCurrentProfile())
                        } else {
                            onComplete(buildCurrentProfile())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag(if (step == 4) "onboarding_finish_btn" else "onboarding_next_step_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (step == 4) "Complete Setup & Open App" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    title: String,
    details: List<Pair<String, String>>,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit $title", modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            details.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
