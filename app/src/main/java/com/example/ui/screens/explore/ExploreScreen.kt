package com.example.ui.screens.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExtensionDescriptor
import com.example.data.repository.ExtensionItemUi
import com.example.ui.theme.Teal60
import com.example.ui.viewmodel.AppDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    extensions: List<ExtensionItemUi>,
    onInstallExtension: (String) -> Unit,
    onToggleExtension: (String, Boolean) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedExtensionForDetail by remember { mutableStateOf<ExtensionItemUi?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val categories = listOf("All", "Nutrition", "Recovery", "Habits", "Analytics", "Cardio", "Anatomy")

    val filteredExtensions = extensions.filter { ext ->
        val desc = ext.descriptor
        val matchesQuery = desc.name.contains(searchQuery, ignoreCase = true) ||
                desc.description.contains(searchQuery, ignoreCase = true) ||
                desc.tagLine.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || desc.category.equals(selectedCategory, ignoreCase = true)
        matchesQuery && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("explore_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search extensions & capabilities...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("extension_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
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

        Spacer(modifier = Modifier.height(10.dp))

        // Extension Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("extensions_list_view")
        ) {
            items(filteredExtensions, key = { it.descriptor.id }) { item ->
                ExtensionCard(
                    item = item,
                    onClick = { selectedExtensionForDetail = item },
                    onInstall = { onInstallExtension(item.descriptor.id) },
                    onOpen = {
                        when (item.descriptor.id) {
                            "ext_nutrition" -> onNavigate(AppDestination.NUTRITION_LAB)
                            "ext_recovery" -> onNavigate(AppDestination.RECOVERY_STUDIO)
                            "ext_habits" -> onNavigate(AppDestination.HABIT_BUILDER)
                            "ext_analytics" -> onNavigate(AppDestination.PROGRESS_ANALYTICS)
                            "ext_endurance" -> onNavigate(AppDestination.ENDURANCE_LAB)
                            "ext_exercise_studio_3d" -> onNavigate(AppDestination.EXERCISE_STUDIO_3D)
                            else -> onNavigate(AppDestination.EXTENSION_MANAGEMENT)
                        }
                    },
                    onToggle = { onToggleExtension(item.descriptor.id, true) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Extension Detail Modal Bottom Sheet
    selectedExtensionForDetail?.let { item ->
        val desc = item.descriptor
        ModalBottomSheet(
            onDismissRequest = { selectedExtensionForDetail = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .testTag("extension_detail_sheet")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = desc.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v${desc.version}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Text(
                            text = desc.tagLine,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { selectedExtensionForDetail = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = desc.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Capabilities & Features",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                desc.featureList.forEach { feat ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Teal60,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feat, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Explicit Permissions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                desc.permissionsRequired.forEach { perm ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = perm, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (desc.isComingSoon) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Planned Module — Coming Soon",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (!item.isInstalled) {
                    Button(
                        onClick = {
                            onInstallExtension(desc.id)
                            selectedExtensionForDetail = null
                        },
                        modifier = Modifier.fillMaxWidth().testTag("modal_install_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Install & Activate Extension", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onToggleExtension(desc.id, !item.isEnabled)
                                selectedExtensionForDetail = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (item.isEnabled) "Deactivate" else "Activate")
                        }
                        Button(
                            onClick = {
                                selectedExtensionForDetail = null
                                when (desc.id) {
                                    "ext_nutrition" -> onNavigate(AppDestination.NUTRITION_LAB)
                                    "ext_recovery" -> onNavigate(AppDestination.RECOVERY_STUDIO)
                                    "ext_habits" -> onNavigate(AppDestination.HABIT_BUILDER)
                                    "ext_analytics" -> onNavigate(AppDestination.PROGRESS_ANALYTICS)
                                    "ext_endurance" -> onNavigate(AppDestination.ENDURANCE_LAB)
                                    "ext_exercise_studio_3d" -> onNavigate(AppDestination.EXERCISE_STUDIO_3D)
                                    else -> onNavigate(AppDestination.EXTENSION_MANAGEMENT)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Open Module", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ExtensionCard(
    item: ExtensionItemUi,
    onClick: () -> Unit,
    onInstall: () -> Unit,
    onOpen: () -> Unit,
    onToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val desc = item.descriptor
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("extension_card_${desc.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (desc.category) {
                                    "Nutrition" -> Color(0xFFFEF3C7)
                                    "Recovery" -> Color(0xFFEDE9FE)
                                    "Habits" -> Color(0xFFE0F2FE)
                                    "Analytics" -> Color(0xFFDCFCE7)
                                    "Cardio" -> Color(0xFFFFEDD5)
                                    "Training" -> Color(0xFFEEF2FF)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (desc.category) {
                                "Nutrition" -> Icons.Default.Restaurant
                                "Recovery" -> Icons.Default.Hotel
                                "Habits" -> Icons.Default.WaterDrop
                                "Analytics" -> Icons.AutoMirrored.Filled.ShowChart
                                "Cardio" -> Icons.AutoMirrored.Filled.DirectionsRun
                                "Training" -> Icons.Default.FitnessCenter
                                else -> Icons.Default.Extension
                            },
                            contentDescription = desc.name,
                            tint = when (desc.category) {
                                "Nutrition" -> Color(0xFFD97706)
                                "Recovery" -> Color(0xFF7C3AED)
                                "Habits" -> Color(0xFF0284C7)
                                "Analytics" -> Color(0xFF15803D)
                                "Cardio" -> Color(0xFFEA580C)
                                "Training" -> Color(0xFF4F46E5)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = desc.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = desc.tier,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = desc.tagLine,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (desc.isComingSoon) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (!item.isInstalled) {
                    FilledTonalButton(
                        onClick = onInstall,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("install_btn_${desc.id}")
                    ) {
                        Text("Install", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (item.isEnabled) onOpen() else onToggle()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag(if (item.isEnabled) "open_btn_${desc.id}" else "activate_btn_${desc.id}")
                    ) {
                        Text(if (item.isEnabled) "Open" else "Activate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
