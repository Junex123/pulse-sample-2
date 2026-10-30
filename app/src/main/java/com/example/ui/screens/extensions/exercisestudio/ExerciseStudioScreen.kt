package com.example.ui.screens.extensions.exercisestudio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseStudioScreen(
    exercises: List<Exercise3d>,
    onSelectExercise: (Exercise3d) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedEquipment by remember { mutableStateOf("All") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val categories = listOf("All", "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Legs", "Core")
    val equipments = listOf("All", "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight")
    val difficulties = listOf("All", "Beginner", "Intermediate", "Advanced")

    // Filter exercises locally
    val filteredExercises = exercises.filter { ex ->
        val query = searchQuery.trim().lowercase()
        val matchesQuery = query.isEmpty() ||
            ex.name.lowercase().contains(query) ||
            ex.category.lowercase().contains(query) ||
            ex.equipment.lowercase().contains(query) ||
            ex.primaryMuscles.any { it.name.lowercase().contains(query) || it.commonName.lowercase().contains(query) }

        val matchesCat = selectedCategory == "All" || ex.category.equals(selectedCategory, ignoreCase = true)
        val matchesEquip = selectedEquipment == "All" || ex.equipment.equals(selectedEquipment, ignoreCase = true)
        val matchesDiff = selectedDifficulty == "All" || ex.difficulty.equals(selectedDifficulty, ignoreCase = true)

        matchesQuery && matchesCat && matchesEquip && matchesDiff
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("exercise_studio_screen")
    ) {
        // Search & Filter Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exercise_studio_search"),
                    placeholder = { Text("Search 3D exercises, muscles, equipment...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                            IconButton(onClick = { showAdvancedFilters = !showAdvancedFilters }) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filters",
                                    tint = if (selectedEquipment != "All" || selectedDifficulty != "All") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Muscle-Group Filter Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("filter_cat_$cat")
                        )
                    }
                }

                // Advanced Filter Drawer (Equipment & Difficulty)
                AnimatedVisibility(visible = showAdvancedFilters) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text("Equipment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            items(equipments) { eq ->
                                FilterChip(
                                    selected = selectedEquipment == eq,
                                    onClick = { selectedEquipment = eq },
                                    label = { Text(eq, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Difficulty", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            items(difficulties) { diff ->
                                FilterChip(
                                    selected = selectedDifficulty == diff,
                                    onClick = { selectedDifficulty = diff },
                                    label = { Text(diff, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Exercise Count Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredExercises.size} 3D Demonstrations",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedCategory != "All" || selectedEquipment != "All" || selectedDifficulty != "All" || searchQuery.isNotEmpty()) {
                Text(
                    text = "Reset Filters",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        searchQuery = ""
                        selectedCategory = "All"
                        selectedEquipment = "All"
                        selectedDifficulty = "All"
                    }
                )
            }
        }

        // Exercises List
        if (filteredExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No matching 3D exercises found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing search or adjusting muscle and equipment filters.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredExercises, key = { it.id }) { exercise ->
                    Exercise3dCard(
                        exercise = exercise,
                        onClick = { onSelectExercise(exercise) }
                    )
                }
            }
        }
    }
}

@Composable
fun Exercise3dCard(
    exercise: Exercise3d,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("exercise_3d_item_${exercise.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Model Badge Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ViewInAr,
                    contentDescription = "3D Rig",
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = exercise.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Target Muscle
                Text(
                    text = "Target: ${exercise.primaryMuscles.joinToString { it.commonName }}",
                    fontSize = 12.sp,
                    color = Color(0xFFFF7043),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Badges: Category, Equipment, Difficulty
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BadgeChip(text = exercise.category, containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    BadgeChip(text = exercise.equipment, containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    BadgeChip(
                        text = exercise.difficulty,
                        containerColor = when (exercise.difficulty) {
                            "Beginner" -> Color(0xFFE8F5E9)
                            "Intermediate" -> Color(0xFFFFF8E1)
                            else -> Color(0xFFFFEBEE)
                        },
                        contentColor = when (exercise.difficulty) {
                            "Beginner" -> Color(0xFF2E7D32)
                            "Intermediate" -> Color(0xFFF57F17)
                            else -> Color(0xFFC62828)
                        }
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BadgeChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}
