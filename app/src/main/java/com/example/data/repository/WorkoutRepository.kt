package com.example.data.repository

import com.example.data.db.ActiveWorkoutDraftEntity
import com.example.data.db.PersonalRecordEntity
import com.example.data.db.WorkoutDao
import com.example.data.db.WorkoutRoutineEntity
import com.example.data.db.WorkoutSessionEntity
import com.example.data.db.WorkoutSetEntity
import com.example.data.model.DEFAULT_EXERCISES
import com.example.data.model.DEFAULT_ROUTINES
import com.example.data.model.Exercise
import com.example.data.model.PersonalRecord
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import com.example.util.UnitConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    val allExercises: List<Exercise> = DEFAULT_EXERCISES

    fun getExerciseById(id: String): Exercise? {
        return allExercises.find { it.id == id }
    }

    val allRoutines: Flow<List<WorkoutRoutine>> = workoutDao.getAllRoutines().map { entities ->
        if (entities.isEmpty()) {
            DEFAULT_ROUTINES
        } else {
            entities.map { entity ->
                WorkoutRoutine(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    category = entity.category,
                    exerciseIds = entity.exerciseIdsCsv.split(",").filter { it.isNotBlank() },
                    estimatedMinutes = entity.estimatedMinutes,
                    isCustom = entity.isCustom
                )
            }
        }
    }

    suspend fun initializeDefaultRoutinesIfEmpty() {
        val initialEntities = DEFAULT_ROUTINES.map { routine ->
            WorkoutRoutineEntity(
                id = routine.id,
                name = routine.name,
                description = routine.description,
                category = routine.category,
                exerciseIdsCsv = routine.exerciseIds.joinToString(","),
                estimatedMinutes = routine.estimatedMinutes,
                isCustom = routine.isCustom
            )
        }
        workoutDao.insertRoutinesIfNotExist(initialEntities)
    }

    val allSessions: Flow<List<WorkoutSession>> = workoutDao.getAllWorkoutsWithSets().map { list ->
        list.map { item ->
            val sets = item.sets.map { s ->
                WorkoutSet(
                    id = s.id,
                    setNumber = s.setNumber,
                    exerciseId = s.exerciseId,
                    exerciseName = s.exerciseName,
                    weightKg = s.weightKg,
                    reps = s.reps,
                    rpe = s.rpe,
                    isCompleted = s.isCompleted,
                    isDemo = s.isDemo
                )
            }
            WorkoutSession(
                id = item.session.id,
                title = item.session.title,
                routineId = item.session.routineId,
                startTimeMillis = item.session.startTimeMillis,
                endTimeMillis = item.session.endTimeMillis,
                durationMinutes = item.session.durationMinutes,
                notes = item.session.notes,
                sets = sets,
                totalVolumeKg = item.session.totalVolumeKg,
                isCompleted = item.session.isCompleted,
                isDemo = item.session.isDemo
            )
        }
    }

    val realSessionsOnly: Flow<List<WorkoutSession>> = workoutDao.getRealWorkoutsWithSets().map { list ->
        list.map { item ->
            val sets = item.sets.map { s ->
                WorkoutSet(
                    id = s.id,
                    setNumber = s.setNumber,
                    exerciseId = s.exerciseId,
                    exerciseName = s.exerciseName,
                    weightKg = s.weightKg,
                    reps = s.reps,
                    rpe = s.rpe,
                    isCompleted = s.isCompleted,
                    isDemo = s.isDemo
                )
            }
            WorkoutSession(
                id = item.session.id,
                title = item.session.title,
                routineId = item.session.routineId,
                startTimeMillis = item.session.startTimeMillis,
                endTimeMillis = item.session.endTimeMillis,
                durationMinutes = item.session.durationMinutes,
                notes = item.session.notes,
                sets = sets,
                totalVolumeKg = item.session.totalVolumeKg,
                isCompleted = item.session.isCompleted,
                isDemo = item.session.isDemo
            )
        }
    }

    val personalRecords: Flow<List<PersonalRecord>> = workoutDao.getAllPersonalRecords().map { entities ->
        entities.map {
            PersonalRecord(
                exerciseId = it.exerciseId,
                exerciseName = it.exerciseName,
                maxWeightKg = it.maxWeightKg,
                maxReps = it.maxReps,
                estimatedOneRepMaxKg = it.estimatedOneRepMaxKg,
                achievedDateMillis = it.achievedDateMillis,
                isDemo = it.isDemo
            )
        }
    }

    val activeDraft: Flow<WorkoutSession?> = workoutDao.observeActiveDraft().map { draft ->
        if (draft == null) null
        else {
            WorkoutSession(
                id = "draft_session",
                title = draft.title,
                routineId = draft.routineId,
                startTimeMillis = draft.startTimeMillis,
                notes = draft.notes,
                sets = deserializeDraftSets(draft.setsJson),
                isCompleted = false
            )
        }
    }

    // Active Draft Persistence
    suspend fun getActiveDraft(): WorkoutSession? {
        val draft = workoutDao.getActiveDraft() ?: return null
        val sets = deserializeDraftSets(draft.setsJson)
        return WorkoutSession(
            id = "draft_session",
            title = draft.title,
            routineId = draft.routineId,
            startTimeMillis = draft.startTimeMillis,
            notes = draft.notes,
            sets = sets,
            isCompleted = false
        )
    }

    suspend fun saveActiveDraft(session: WorkoutSession) {
        val json = serializeDraftSets(session.sets)
        workoutDao.saveActiveDraft(
            ActiveWorkoutDraftEntity(
                id = "active_draft",
                title = session.title,
                routineId = session.routineId,
                startTimeMillis = session.startTimeMillis,
                notes = session.notes,
                setsJson = json
            )
        )
    }

    suspend fun clearActiveDraft() {
        workoutDao.clearActiveDraft()
    }

    private fun serializeDraftSets(sets: List<WorkoutSet>): String {
        val arr = JSONArray()
        sets.forEach { s ->
            val obj = JSONObject().apply {
                put("setNumber", s.setNumber)
                put("exerciseId", s.exerciseId)
                put("exerciseName", s.exerciseName)
                put("weightKg", s.weightKg.toDouble())
                put("reps", s.reps)
                put("rpe", s.rpe)
                put("isCompleted", s.isCompleted)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeDraftSets(jsonStr: String): List<WorkoutSet> {
        return try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<WorkoutSet>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    WorkoutSet(
                        id = i.toLong(),
                        setNumber = obj.optInt("setNumber", i + 1),
                        exerciseId = obj.optString("exerciseId"),
                        exerciseName = obj.optString("exerciseName"),
                        weightKg = obj.optDouble("weightKg", 0.0).toFloat(),
                        reps = obj.optInt("reps", 10),
                        rpe = obj.optInt("rpe", 8),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveCompletedWorkout(
        title: String,
        routineId: String?,
        startTimeMillis: Long,
        endTimeMillis: Long,
        durationMinutes: Int,
        notes: String,
        sets: List<WorkoutSet>
    ): String {
        val sessionId = UUID.randomUUID().toString()
        val totalVolume = sets.filter { it.isCompleted }.fold(0f) { acc, s -> acc + (s.weightKg * s.reps) }

        val sessionEntity = WorkoutSessionEntity(
            id = sessionId,
            title = title,
            routineId = routineId,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            durationMinutes = durationMinutes,
            notes = notes,
            totalVolumeKg = totalVolume,
            isCompleted = true,
            isDemo = false
        )

        val setEntities = sets.map { s ->
            WorkoutSetEntity(
                sessionId = sessionId,
                setNumber = s.setNumber,
                exerciseId = s.exerciseId,
                exerciseName = s.exerciseName,
                weightKg = s.weightKg,
                reps = s.reps,
                rpe = s.rpe,
                isCompleted = s.isCompleted,
                isDemo = false
            )
        }

        // Update Personal Records with estimated 1RM
        val prList = mutableListOf<PersonalRecordEntity>()
        sets.filter { it.isCompleted && it.weightKg > 0f && it.reps > 0 }.forEach { s ->
            val estimated1RM = UnitConverter.calculateEpley1RM(s.weightKg, s.reps)
            val existing = workoutDao.getPersonalRecordForExercise(s.exerciseId)
            val shouldUpdate = existing == null ||
                    s.weightKg > existing.maxWeightKg ||
                    estimated1RM > existing.estimatedOneRepMaxKg

            if (shouldUpdate) {
                val newMaxWeight = maxOf(s.weightKg, existing?.maxWeightKg ?: 0f)
                val newReps = if (s.weightKg >= (existing?.maxWeightKg ?: 0f)) s.reps else existing?.maxReps ?: s.reps
                val new1RM = maxOf(estimated1RM, existing?.estimatedOneRepMaxKg ?: 0f)

                prList.add(
                    PersonalRecordEntity(
                        exerciseId = s.exerciseId,
                        exerciseName = s.exerciseName,
                        maxWeightKg = newMaxWeight,
                        maxReps = newReps,
                        estimatedOneRepMaxKg = new1RM,
                        achievedDateMillis = endTimeMillis,
                        isDemo = false
                    )
                )
            }
        }

        // Atomically complete workout in single database transaction
        workoutDao.completeWorkoutTransaction(sessionEntity, setEntities, prList)

        return sessionId
    }

    suspend fun saveCustomRoutine(
        name: String,
        description: String,
        category: String,
        exerciseIds: List<String>,
        estimatedMinutes: Int
    ) {
        val routine = WorkoutRoutineEntity(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            name = name,
            description = description,
            category = category,
            exerciseIdsCsv = exerciseIds.joinToString(","),
            estimatedMinutes = estimatedMinutes,
            isCustom = true
        )
        workoutDao.insertRoutine(routine)
    }

    suspend fun deleteRoutine(id: String) {
        workoutDao.deleteRoutine(id)
    }

    suspend fun purgeDemoData() {
        workoutDao.purgeDemoSessions()
        workoutDao.purgeDemoSets()
        workoutDao.purgeDemoPersonalRecords()
    }

    suspend fun deleteAllWorkoutData() {
        workoutDao.deleteAllSessions()
        workoutDao.deleteAllSets()
        workoutDao.deleteAllPersonalRecords()
        workoutDao.deleteAllCustomRoutines()
        workoutDao.clearActiveDraft()
    }

    suspend fun seedDemoWorkoutHistoryIfEmpty() {
        val seeded = workoutDao.getConfig("seed_workouts_version")
        if (seeded != null && seeded.value == "1") {
            // Already seeded previously. Never reintroduce demo workouts after user deletion!
            return
        }

        val existing = workoutDao.getSessionById("demo_session_1")
        if (existing == null) {
            val demoSession = WorkoutSessionEntity(
                id = "demo_session_1",
                title = "Upper Body Hypertrophy (Sample)",
                routineId = "routine_upper_power",
                startTimeMillis = System.currentTimeMillis() - (86400000L * 2),
                endTimeMillis = System.currentTimeMillis() - (86400000L * 2) + (45 * 60000L),
                durationMinutes = 45,
                notes = "Sample training session for demo purposes. Felt great on bench press.",
                totalVolumeKg = 3120f,
                isCompleted = true,
                isDemo = true
            )
            workoutDao.insertSession(demoSession)
            val demoSets = listOf(
                WorkoutSetEntity(sessionId = "demo_session_1", setNumber = 1, exerciseId = "barbell_bench_press", exerciseName = "Barbell Bench Press", weightKg = 60f, reps = 10, rpe = 7, isCompleted = true, isDemo = true),
                WorkoutSetEntity(sessionId = "demo_session_1", setNumber = 2, exerciseId = "barbell_bench_press", exerciseName = "Barbell Bench Press", weightKg = 70f, reps = 8, rpe = 8, isCompleted = true, isDemo = true),
                WorkoutSetEntity(sessionId = "demo_session_1", setNumber = 3, exerciseId = "barbell_bench_press", exerciseName = "Barbell Bench Press", weightKg = 75f, reps = 6, rpe = 9, isCompleted = true, isDemo = true),
                WorkoutSetEntity(sessionId = "demo_session_1", setNumber = 1, exerciseId = "dumbbell_row", exerciseName = "Single-Arm Dumbbell Row", weightKg = 24f, reps = 12, rpe = 8, isCompleted = true, isDemo = true),
                WorkoutSetEntity(sessionId = "demo_session_1", setNumber = 2, exerciseId = "dumbbell_row", exerciseName = "Single-Arm Dumbbell Row", weightKg = 26f, reps = 10, rpe = 8, isCompleted = true, isDemo = true)
            )
            workoutDao.insertSets(demoSets)

            // Seed initial demo PR
            workoutDao.insertPersonalRecord(
                PersonalRecordEntity(
                    exerciseId = "barbell_bench_press",
                    exerciseName = "Barbell Bench Press",
                    maxWeightKg = 75f,
                    maxReps = 6,
                    estimatedOneRepMaxKg = 90f,
                    achievedDateMillis = System.currentTimeMillis() - (86400000L * 2),
                    isDemo = true
                )
            )
        }
        workoutDao.setConfig(com.example.data.db.AppConfigEntity("seed_workouts_version", "1"))
    }

    suspend fun restoreCloudSession(session: WorkoutSessionEntity, sets: List<WorkoutSetEntity>) {
        workoutDao.insertSession(session)
        if (sets.isNotEmpty()) {
            workoutDao.insertSets(sets)
        }
    }

    suspend fun savePersonalRecord(pr: PersonalRecordEntity) {
        workoutDao.insertPersonalRecord(pr)
    }

    suspend fun saveRoutineEntity(routine: WorkoutRoutineEntity) {
        workoutDao.insertRoutinesIfNotExist(listOf(routine))
    }
}
