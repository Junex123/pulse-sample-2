package com.example.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.CloudSyncInfo
import com.example.data.repository.SyncState
import com.example.ui.viewmodel.AppDestination
import com.example.util.DateUtils
import com.example.util.UnitConverter

@Composable
fun SettingsScreen(
    profile: UserProfile,
    userEmail: String,
    syncInfo: CloudSyncInfo,
    onSyncNow: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onUpdateProfile: (UserProfile) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleDemoData: (Boolean) -> Unit,
    onToggleAiConsent: (Boolean) -> Unit,
    onPurgeDemoData: () -> Unit,
    onDeleteAllData: () -> Unit,
    onExportData: () -> String,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var name by remember(profile.name) { mutableStateOf(profile.name) }
    var weightText by remember(profile.weightKg) { mutableStateOf(profile.weightKg.toString()) }
    var heightText by remember(profile.heightCm) { mutableStateOf(profile.heightCm.toInt().toString()) }
    var savedNotice by remember { mutableStateOf(false) }

    var showSignOutConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteAccountConfirmDialog by remember { mutableStateOf(false) }
    var showPurgeConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteAllConfirmDialog by remember { mutableStateOf(false) }
    var exportJsonContent by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Settings & Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage your authenticated account, cloud synchronization, and privacy settings.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Account & Cloud Synchronization Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_account_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar Initial Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (profile.name.firstOrNull() ?: 'P').uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name.ifBlank { "Athlete" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = userEmail.ifBlank { "Offline Guest Account" },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cloud Sync Status Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = when (syncInfo.state) {
                                    SyncState.SYNCED -> Icons.Default.CloudDone
                                    SyncState.SYNCING -> Icons.Default.CloudSync
                                    SyncState.ERROR -> Icons.Default.SyncProblem
                                    else -> Icons.Default.CloudSync
                                },
                                contentDescription = null,
                                tint = when (syncInfo.state) {
                                    SyncState.SYNCED -> Color(0xFF22C55E)
                                    SyncState.SYNCING -> MaterialTheme.colorScheme.primary
                                    SyncState.ERROR -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = when (syncInfo.state) {
                                        SyncState.SYNCED -> "Cloud Synced"
                                        SyncState.SYNCING -> "Backing up..."
                                        SyncState.ERROR -> "Sync Warning"
                                        else -> "Cloud Backup Ready"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (syncInfo.lastSyncMillis > 0)
                                        "Last: ${DateUtils.formatDateTime(syncInfo.lastSyncMillis)}"
                                    else
                                        "Ready to sync",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onSyncNow,
                            enabled = syncInfo.state != SyncState.SYNCING,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sync_now_btn")
                        ) {
                            if (syncInfo.state == SyncState.SYNCING) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sync Now", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Account Actions: Sign Out & Delete Account
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showSignOutConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sign_out_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign Out", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showDeleteAccountConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("delete_account_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Account", fontSize = 12.sp)
                        }
                    }

                    // Web Deletion Request Link (Google Play Policy Compliance)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pulsehealth.app/request-account-deletion"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Visit: https://pulsehealth.app/request-account-deletion", Toast.LENGTH_LONG).show()
                                }
                            }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Web portal account deletion request",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Appearance & Theme Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Appearance", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Toggle high-contrast dark theme", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = profile.isDarkMode,
                            onCheckedChange = { onToggleDarkMode(it) },
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. AI & Privacy Consent Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "AI Coach & Privacy", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "AI Coaching Consent", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                text = if (profile.isAiConsentGranted)
                                    "Consent active. Requests are rate-limited and proxied securely."
                                else
                                    "Disabled. Coach runs exclusively in offline privacy mode.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = profile.isAiConsentGranted,
                            onCheckedChange = { onToggleAiConsent(it) },
                            modifier = Modifier.testTag("ai_consent_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Demo Data & Transparency Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Data Transparency & Sample Data", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Show Sample Data Badges", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Highlights demo metrics vs genuine logged workouts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = profile.isDemoDataEnabled,
                            onCheckedChange = { onToggleDemoData(it) },
                            modifier = Modifier.testTag("demo_data_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showPurgeConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("purge_demo_data_btn")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Purge All Sample / Demo Data")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. User Profile Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Physical Metrics & Units", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; savedNotice = false },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("settings_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it; savedNotice = false },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { heightText = it; savedNotice = false },
                            label = { Text("Height (cm)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val wKg = weightText.toFloatOrNull() ?: profile.weightKg
                    val wLbs = UnitConverter.kgToLbs(wKg)
                    val hCm = heightText.toFloatOrNull() ?: profile.heightCm
                    val (hFt, hIn) = UnitConverter.cmToFeetInches(hCm)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Equivalent: ${String.format("%.1f", wLbs)} lbs • ${hFt}ft ${hIn}in",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val w = weightText.toFloatOrNull() ?: profile.weightKg
                            val h = heightText.toFloatOrNull() ?: profile.heightCm
                            onUpdateProfile(
                                profile.copy(
                                    name = if (name.isNotBlank()) name else profile.name,
                                    weightKg = w,
                                    heightCm = h
                                )
                            )
                            savedNotice = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_settings_btn")
                    ) {
                        Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                    }

                    if (savedNotice) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Changes saved successfully!", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Data Portability & Privacy Zone
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Data Portability & Privacy", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Export your training history and logs in open standard JSON format, or wipe all local data.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val json = onExportData()
                                exportJsonContent = json
                            },
                            modifier = Modifier.weight(1f).testTag("export_data_json_btn")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export JSON")
                        }

                        Button(
                            onClick = { showDeleteAllConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            modifier = Modifier.weight(1f).testTag("delete_all_data_btn")
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete All")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Extension Hub Shortcut
            FilledTonalButton(
                onClick = { onNavigate(AppDestination.EXTENSION_MANAGEMENT) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Extension, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage Active Extensions", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Version & About
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Pulse — Modular Personal Health & Fitness Platform",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Built with Jetpack Compose & Clean Architecture • Version 1.0.0",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Confirmation Dialogs

    // Sign Out Dialog
    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            title = { Text("Sign Out of Pulse?") },
            text = { Text("Are you sure you want to sign out? Your cloud data will remain backed up and can be restored when you sign back in.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutConfirmDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirmDialog = false },
            title = { Text("Permanently Delete Account?") },
            text = {
                Text(
                    text = "This will permanently delete your authenticated account, erase all cloud backup records in Firestore, and clear local logs on this device. This action cannot be reversed.",
                    color = MaterialTheme.colorScheme.error
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Account & Cloud Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPurgeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirmDialog = false },
            title = { Text("Purge Demo Data?") },
            text = { Text("This will remove all pre-populated sample workouts, meal records, and sleep logs. Your genuine logged entries will remain untouched.") },
            confirmButton = {
                Button(
                    onClick = {
                        onPurgeDemoData()
                        showPurgeConfirmDialog = false
                        Toast.makeText(context, "Sample data removed.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Purge Sample Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirmDialog = false },
            title = { Text("Delete All User Data?") },
            text = { Text("This will permanently erase all workout history, personal records, nutrition logs, recovery entries, and reset the app to initial setup. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllData()
                        showDeleteAllConfirmDialog = false
                        Toast.makeText(context, "All user data deleted.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Permanently Erase Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export JSON Dialog
    if (exportJsonContent != null) {
        val json = exportJsonContent ?: ""
        AlertDialog(
            onDismissRequest = { exportJsonContent = null },
            title = { Text("Exported Data (JSON)") },
            text = {
                Column {
                    Text("Your complete health and training logs are packaged below:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = json.take(1500) + if (json.length > 1500) "\n\n... (truncated for preview)" else "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(json))
                        Toast.makeText(context, "Copied JSON to clipboard!", Toast.LENGTH_SHORT).show()
                        exportJsonContent = null
                    }
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { exportJsonContent = null }) {
                    Text("Close")
                }
            }
        )
    }
}
