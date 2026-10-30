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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ExtensionItemUi
import com.example.ui.viewmodel.AppDestination

@Composable
fun ExtensionManagementScreen(
    installedExtensions: List<ExtensionItemUi>,
    onToggleExtension: (String, Boolean) -> Unit,
    onUninstallExtension: (String) -> Unit,
    onNavigateToExtension: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var extensionToUninstall by remember { mutableStateOf<ExtensionItemUi?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("extension_management_screen")
    ) {
        Text(
            text = "Installed Modules",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Configure, activate, or disable extensions. Disabling a module hides its interface while preserving your logged records.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (installedExtensions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No optional extensions installed yet. Browse the Explore tab to add modules!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(installedExtensions, key = { it.descriptor.id }) { item ->
                    val desc = item.descriptor
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("manage_ext_${desc.id}"),
                        shape = RoundedCornerShape(16.dp),
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
                                    Icon(
                                        imageVector = Icons.Default.Extension,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = desc.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "v${desc.version} • ${desc.category}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Switch(
                                    checked = item.isEnabled,
                                    onCheckedChange = { checked ->
                                        onToggleExtension(desc.id, checked)
                                    },
                                    modifier = Modifier.testTag("toggle_ext_${desc.id}")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = desc.description,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Permissions used
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Granted: ${desc.permissionsRequired.joinToString(", ")}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { extensionToUninstall = item }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Deactivate & Remove", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }

                                if (item.isEnabled) {
                                    FilledTonalButton(
                                        onClick = {
                                            when (desc.id) {
                                                "ext_nutrition" -> onNavigateToExtension(AppDestination.NUTRITION_LAB)
                                                "ext_recovery" -> onNavigateToExtension(AppDestination.RECOVERY_STUDIO)
                                                "ext_habits" -> onNavigateToExtension(AppDestination.HABIT_BUILDER)
                                                "ext_analytics" -> onNavigateToExtension(AppDestination.PROGRESS_ANALYTICS)
                                                "ext_endurance" -> onNavigateToExtension(AppDestination.ENDURANCE_LAB)
                                                "ext_exercise_studio_3d" -> onNavigateToExtension(AppDestination.EXERCISE_STUDIO_3D)
                                                else -> {}
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Open", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    extensionToUninstall?.let { item ->
        AlertDialog(
            onDismissRequest = { extensionToUninstall = null },
            title = { Text("Remove ${item.descriptor.name}?") },
            text = {
                Text(
                    "This will remove the extension's views from your dashboard and navigation. Your logged history and health metrics will be preserved safely in the database."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUninstallExtension(item.descriptor.id)
                        extensionToUninstall = null
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { extensionToUninstall = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
