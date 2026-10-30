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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import com.example.data.model.DEFAULT_FOOD_DATABASE
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NutritionEntry
import com.example.ui.theme.Teal60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionLabScreen(
    entries: List<NutritionEntry>,
    onLogFood: (foodName: String, mealType: String, calories: Int, protein: Int, carbs: Int, fat: Int) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val targetCalories = 2400
    val targetProtein = 160
    val targetCarbs = 280
    val targetFat = 70

    val totalCalories = entries.sumOf { it.calories }
    val totalProtein = entries.sumOf { it.proteinGrams }
    val totalCarbs = entries.sumOf { it.carbsGrams }
    val totalFat = entries.sumOf { it.fatGrams }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_food_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Food")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("nutrition_lab_screen")
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Macronutrient Summary Card
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
                                text = "Daily Calorie & Macro Target",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$totalCalories / $targetCalories kcal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val calProgress = (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { calProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroBarItem("Protein", totalProtein, targetProtein, Teal60)
                            MacroBarItem("Carbs", totalCarbs, targetCarbs, MaterialTheme.colorScheme.secondary)
                            MacroBarItem("Fats", totalFat, targetFat, Color(0xFFEAB308))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Logged Meals (${entries.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (entries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No food logged yet today. Tap + to add a meal!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(entries, key = { it.id }) { entry ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.foodName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${entry.mealType} • ${entry.calories} kcal • P: ${entry.proteinGrams}g, C: ${entry.carbsGrams}g, F: ${entry.fatGrams}g",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onDeleteEntry(entry.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        var foodName by remember { mutableStateOf("") }
        var mealType by remember { mutableStateOf("Breakfast") }
        var caloriesText by remember { mutableStateOf("") }
        var proteinText by remember { mutableStateOf("") }
        var carbsText by remember { mutableStateOf("") }
        var fatText by remember { mutableStateOf("") }

        val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
        var expandedDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Food Item") },
            text = {
                Column {
                    Text("Quick Pick Staple Foods:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(DEFAULT_FOOD_DATABASE.size) { idx ->
                            val food = DEFAULT_FOOD_DATABASE[idx]
                            AssistChip(
                                onClick = {
                                    foodName = food.name
                                    caloriesText = food.calories.toString()
                                    proteinText = food.proteinGrams.toString()
                                    carbsText = food.carbsGrams.toString()
                                    fatText = food.fatGrams.toString()
                                },
                                label = { Text(food.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food / Meal Name (e.g. Greek Yogurt)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("food_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = mealType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Meal") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            mealTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        mealType = type
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = caloriesText,
                            onValueChange = { caloriesText = it },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("food_calories_input")
                        )
                        OutlinedTextField(
                            value = proteinText,
                            onValueChange = { proteinText = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("food_protein_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = carbsText,
                            onValueChange = { carbsText = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatText,
                            onValueChange = { fatText = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (foodName.isNotBlank()) {
                            val c = caloriesText.toIntOrNull() ?: 0
                            val p = proteinText.toIntOrNull() ?: 0
                            val cb = carbsText.toIntOrNull() ?: 0
                            val f = fatText.toIntOrNull() ?: 0
                            onLogFood(foodName, mealType, c, p, cb, f)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_food_button")
                ) {
                    Text("Log Meal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MacroBarItem(
    label: String,
    current: Int,
    target: Int,
    color: Color
) {
    Column(modifier = Modifier.width(90.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "$current / ${target}g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(4.dp))
        val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
