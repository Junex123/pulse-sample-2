package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.db.PersonalRecordEntity
import com.example.data.db.WorkoutRoutineEntity
import com.example.data.db.WorkoutSessionEntity
import com.example.data.db.WorkoutSetEntity
import com.example.data.model.PersonalRecord
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

enum class SyncState {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE_ONLY,
    ERROR
}

data class CloudSyncInfo(
    val state: SyncState = SyncState.IDLE,
    val lastSyncMillis: Long = 0L,
    val message: String = ""
)

/**
 * Handles two-way synchronization and cloud data restoration between Room and Firestore.
 */
class FirestoreSyncRepository(context: Context) {

    private val firestore: FirebaseFirestore by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            if (dbId.isNotEmpty()) {
                FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (_: Exception) {
            FirebaseFirestore.getInstance()
        }
    }

    private val _syncInfo = MutableStateFlow(CloudSyncInfo())
    val syncInfo: StateFlow<CloudSyncInfo> = _syncInfo.asStateFlow()

    /**
     * Synchronizes current local user profile, workout sessions, PRs, and custom routines to Firestore.
     */
    suspend fun syncLocalDataToCloud(
        userId: String,
        userEmail: String,
        profile: UserProfile,
        sessions: List<WorkoutSession>,
        personalRecords: List<PersonalRecord>,
        customRoutines: List<WorkoutRoutine>
    ): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(Exception("Cannot sync: user ID is missing"))
        }

        _syncInfo.value = _syncInfo.value.copy(state = SyncState.SYNCING, message = "Synchronizing data to cloud...")

        return try {
            val userDocRef = firestore.collection("users").document(userId)

            // 1. Sync User Profile
            val profileMap = hashMapOf(
                "uid" to userId,
                "name" to profile.name,
                "email" to userEmail,
                "goal" to profile.goal,
                "experienceLevel" to profile.experienceLevel,
                "trainingLocation" to profile.trainingLocation,
                "equipmentAvailable" to profile.equipmentAvailable,
                "preferredFrequencyDays" to profile.preferredFrequencyDays,
                "weightKg" to profile.weightKg,
                "heightCm" to profile.heightCm,
                "weightUnit" to profile.weightUnit,
                "hasCompletedOnboarding" to profile.hasCompletedOnboarding,
                "lastSyncedAt" to System.currentTimeMillis()
            )
            userDocRef.set(profileMap, SetOptions.merge()).await()

            // 2. Sync Real (Non-Demo) Workout Sessions
            val realSessions = sessions.filter { !it.isDemo }
            for (s in realSessions) {
                val sessionMap = hashMapOf(
                    "id" to s.id,
                    "title" to s.title,
                    "routineId" to (s.routineId ?: ""),
                    "startTimeMillis" to s.startTimeMillis,
                    "endTimeMillis" to s.endTimeMillis,
                    "durationMinutes" to s.durationMinutes,
                    "notes" to s.notes,
                    "totalVolumeKg" to s.totalVolumeKg,
                    "isCompleted" to s.isCompleted,
                    "setsJson" to s.sets.joinToString(";") { "${it.exerciseId},${it.exerciseName},${it.weightKg},${it.reps},${it.rpe}" }
                )
                userDocRef.collection("workout_sessions").document(s.id).set(sessionMap, SetOptions.merge()).await()
            }

            // 3. Sync Personal Records
            val realPrs = personalRecords.filter { !it.isDemo }
            for (pr in realPrs) {
                val prMap = hashMapOf(
                    "exerciseId" to pr.exerciseId,
                    "exerciseName" to pr.exerciseName,
                    "maxWeightKg" to pr.maxWeightKg,
                    "maxReps" to pr.maxReps,
                    "estimatedOneRepMaxKg" to pr.estimatedOneRepMaxKg,
                    "achievedDateMillis" to pr.achievedDateMillis
                )
                userDocRef.collection("personal_records").document(pr.exerciseId).set(prMap, SetOptions.merge()).await()
            }

            // 4. Sync Custom Routines
            for (routine in customRoutines.filter { it.isCustom }) {
                val routineMap = hashMapOf(
                    "id" to routine.id,
                    "name" to routine.name,
                    "description" to routine.description,
                    "category" to routine.category,
                    "exerciseIdsCsv" to routine.exercises.joinToString(",") { it.id },
                    "estimatedMinutes" to routine.estimatedMinutes
                )
                userDocRef.collection("custom_routines").document(routine.id).set(routineMap, SetOptions.merge()).await()
            }

            val now = System.currentTimeMillis()
            _syncInfo.value = CloudSyncInfo(
                state = SyncState.SYNCED,
                lastSyncMillis = now,
                message = "All data backed up to cloud"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            _syncInfo.value = _syncInfo.value.copy(
                state = SyncState.ERROR,
                message = e.localizedMessage ?: "Sync failed. Local data preserved."
            )
            Result.failure(e)
        }
    }

    /**
     * Restores cloud-backed records for the user and merges them into Room.
     * Used on fresh installs or subsequent sign-ins.
     */
    suspend fun restoreDataFromCloud(
        userId: String,
        userDataRepo: UserDataRepository,
        workoutRepo: WorkoutRepository
    ): Result<Int> {
        if (userId.isBlank()) {
            return Result.failure(Exception("Cannot restore: User ID is blank"))
        }

        _syncInfo.value = _syncInfo.value.copy(state = SyncState.SYNCING, message = "Restoring cloud records...")

        return try {
            val userDocRef = firestore.collection("users").document(userId)
            val profileDoc = userDocRef.get().await()

            var restoredCount = 0

            // 1. Restore Profile if found
            if (profileDoc.exists()) {
                val name = profileDoc.getString("name") ?: "Athlete"
                val goal = profileDoc.getString("goal") ?: "Build Muscle"
                val exp = profileDoc.getString("experienceLevel") ?: "Beginner"
                val location = profileDoc.getString("trainingLocation") ?: "Gym"
                val equip = profileDoc.getString("equipmentAvailable") ?: "Full Equipment"
                val days = profileDoc.getLong("preferredFrequencyDays")?.toInt() ?: 4
                val weight = profileDoc.getDouble("weightKg")?.toFloat() ?: 70.0f
                val height = profileDoc.getDouble("heightCm")?.toFloat() ?: 175.0f
                val completedOnboarding = profileDoc.getBoolean("hasCompletedOnboarding") ?: true

                val currentLocalProfile = userDataRepo.getUserProfileSync()
                val restoredProfile = (currentLocalProfile ?: UserProfile()).copy(
                    name = name,
                    goal = goal,
                    experienceLevel = exp,
                    trainingLocation = location,
                    equipmentAvailable = equip,
                    preferredFrequencyDays = days,
                    weightKg = weight,
                    heightCm = height,
                    hasCompletedOnboarding = completedOnboarding
                )
                userDataRepo.saveUserProfile(restoredProfile)
                restoredCount++
            }

            // 2. Restore Workout Sessions
            val sessionsSnapshot = userDocRef.collection("workout_sessions").get().await()
            for (doc in sessionsSnapshot.documents) {
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: "Workout"
                val routineId = doc.getString("routineId")?.takeIf { it.isNotBlank() }
                val start = doc.getLong("startTimeMillis") ?: System.currentTimeMillis()
                val end = doc.getLong("endTimeMillis") ?: start
                val duration = doc.getLong("durationMinutes")?.toInt() ?: 30
                val notes = doc.getString("notes") ?: ""
                val volume = doc.getDouble("totalVolumeKg")?.toFloat() ?: 0f
                val isCompleted = doc.getBoolean("isCompleted") ?: true

                val entity = WorkoutSessionEntity(
                    id = id,
                    title = title,
                    routineId = routineId,
                    startTimeMillis = start,
                    endTimeMillis = end,
                    durationMinutes = duration,
                    notes = notes,
                    totalVolumeKg = volume,
                    isCompleted = isCompleted,
                    isDemo = false
                )

                // Sets parser
                val setsRaw = doc.getString("setsJson") ?: ""
                val sets = if (setsRaw.isNotBlank()) {
                    setsRaw.split(";").mapIndexedNotNull { idx, setStr ->
                        val parts = setStr.split(",")
                        if (parts.size >= 5) {
                            WorkoutSetEntity(
                                sessionId = id,
                                setNumber = idx + 1,
                                exerciseId = parts[0],
                                exerciseName = parts[1],
                                weightKg = parts[2].toFloatOrNull() ?: 0f,
                                reps = parts[3].toIntOrNull() ?: 0,
                                rpe = parts[4].toIntOrNull() ?: 8,
                                isCompleted = true,
                                isDemo = false
                            )
                        } else null
                    }
                } else emptyList()

                workoutRepo.restoreCloudSession(entity, sets)
                restoredCount++
            }

            // 3. Restore Personal Records
            val prSnapshot = userDocRef.collection("personal_records").get().await()
            for (doc in prSnapshot.documents) {
                val exId = doc.getString("exerciseId") ?: doc.id
                val exName = doc.getString("exerciseName") ?: exId
                val maxW = doc.getDouble("maxWeightKg")?.toFloat() ?: 0f
                val maxR = doc.getLong("maxReps")?.toInt() ?: 0
                val e1rm = doc.getDouble("estimatedOneRepMaxKg")?.toFloat() ?: maxW
                val date = doc.getLong("achievedDateMillis") ?: System.currentTimeMillis()

                val prEntity = PersonalRecordEntity(
                    exerciseId = exId,
                    exerciseName = exName,
                    maxWeightKg = maxW,
                    maxReps = maxR,
                    estimatedOneRepMaxKg = e1rm,
                    achievedDateMillis = date,
                    isDemo = false
                )
                workoutRepo.savePersonalRecord(prEntity)
                restoredCount++
            }

            // 4. Restore Custom Routines
            val routinesSnapshot = userDocRef.collection("custom_routines").get().await()
            for (doc in routinesSnapshot.documents) {
                val rId = doc.getString("id") ?: doc.id
                val rName = doc.getString("name") ?: "Custom Routine"
                val rDesc = doc.getString("description") ?: ""
                val rCat = doc.getString("category") ?: "Custom"
                val exCsv = doc.getString("exerciseIdsCsv") ?: ""
                val mins = doc.getLong("estimatedMinutes")?.toInt() ?: 45

                val routineEntity = WorkoutRoutineEntity(
                    id = rId,
                    name = rName,
                    description = rDesc,
                    category = rCat,
                    exerciseIdsCsv = exCsv,
                    estimatedMinutes = mins,
                    isCustom = true
                )
                workoutRepo.saveRoutineEntity(routineEntity)
                restoredCount++
            }

            val now = System.currentTimeMillis()
            _syncInfo.value = CloudSyncInfo(
                state = SyncState.SYNCED,
                lastSyncMillis = now,
                message = if (restoredCount > 0) "Restored $restoredCount items from cloud" else "Account synced (cloud is up to date)"
            )
            Result.success(restoredCount)
        } catch (e: Exception) {
            _syncInfo.value = _syncInfo.value.copy(
                state = SyncState.ERROR,
                message = e.localizedMessage ?: "Could not connect to cloud. Using offline data."
            )
            Result.failure(e)
        }
    }

    /**
     * Deletes user cloud documents and subcollections upon account deletion request.
     */
    suspend fun deleteCloudUserData(userId: String): Result<Unit> {
        if (userId.isBlank()) return Result.success(Unit)

        return try {
            val userDocRef = firestore.collection("users").document(userId)

            // Delete subcollections documents
            listOf("workout_sessions", "personal_records", "custom_routines").forEach { subcol ->
                val docs = userDocRef.collection(subcol).get().await()
                for (doc in docs.documents) {
                    doc.reference.delete().await()
                }
            }

            // Delete root profile doc
            userDocRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
