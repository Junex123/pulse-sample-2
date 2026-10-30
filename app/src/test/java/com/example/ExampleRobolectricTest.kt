package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.ActiveWorkoutDraftEntity
import com.example.data.db.AppConfigEntity
import com.example.data.db.NutritionEntryEntity
import com.example.data.db.PersonalRecordEntity
import com.example.data.db.PulseDatabase
import com.example.data.db.WorkoutSessionEntity
import com.example.data.db.WorkoutSetEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: PulseDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PulseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pulse", appName)
    }

    @Test
    fun `room database - separates sample and real sessions and purges demo data`() = runBlocking {
        val workoutDao = db.workoutDao()

        val realSession = WorkoutSessionEntity(
            id = "user_session_1",
            title = "Real Chest Workout",
            routineId = null,
            startTimeMillis = 1000L,
            endTimeMillis = 2000L,
            durationMinutes = 45,
            notes = "Felt strong",
            totalVolumeKg = 2500f,
            isCompleted = true,
            isDemo = false
        )

        val demoSession = WorkoutSessionEntity(
            id = "demo_session_1",
            title = "Sample Full Body",
            routineId = null,
            startTimeMillis = 500L,
            endTimeMillis = 1500L,
            durationMinutes = 30,
            notes = "Sample demo data",
            totalVolumeKg = 1800f,
            isCompleted = true,
            isDemo = true
        )

        workoutDao.insertSession(realSession)
        workoutDao.insertSession(demoSession)

        // Verify total sessions is 2
        val allSessions = workoutDao.getAllSessions().first()
        assertEquals(2, allSessions.size)

        // Verify real sessions only returns 1
        val realOnly = workoutDao.getRealSessionsOnly().first()
        assertEquals(1, realOnly.size)
        assertEquals("user_session_1", realOnly[0].id)

        // Purge demo sessions
        workoutDao.purgeDemoSessions()

        // Real session must still exist
        val afterPurge = workoutDao.getAllSessions().first()
        assertEquals(1, afterPurge.size)
        assertEquals("user_session_1", afterPurge[0].id)
    }

    @Test
    fun `room database - active workout draft persistence and restoration`() = runBlocking {
        val workoutDao = db.workoutDao()

        val draft = ActiveWorkoutDraftEntity(
            id = "active_draft",
            title = "In Progress Push Day",
            routineId = "routine_starter_fullbody",
            startTimeMillis = System.currentTimeMillis() - 600000L,
            notes = "Draft notes",
            setsJson = """[{"setNumber":1,"exerciseId":"barbell_bench_press","exerciseName":"Bench Press","weightKg":80.0,"reps":8,"rpe":8,"isCompleted":true}]"""
        )

        workoutDao.saveActiveDraft(draft)

        val retrieved = workoutDao.getActiveDraft()
        assertNotNull(retrieved)
        assertEquals("In Progress Push Day", retrieved?.title)
        assertTrue(retrieved?.setsJson?.contains("barbell_bench_press") == true)

        workoutDao.clearActiveDraft()
        val afterClear = workoutDao.getActiveDraft()
        assertNull(afterClear)
    }

    @Test
    fun `room database - seed config flag prevents duplicate data seeding`() = runBlocking {
        val userDataDao = db.userDataDao()

        // Before seed
        val before = userDataDao.getConfig("seed_version")
        assertNull(before)

        // Mark seeded
        userDataDao.setConfig(AppConfigEntity("seed_version", "1"))

        // Check seed
        val after = userDataDao.getConfig("seed_version")
        assertNotNull(after)
        assertEquals("1", after?.value)
    }

    @Test
    fun `room database - nutrition entries demo flag and purge`() = runBlocking {
        val userDataDao = db.userDataDao()

        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = System.currentTimeMillis(),
                mealType = "Breakfast",
                foodName = "Real Eggs & Toast",
                calories = 350,
                proteinGrams = 22,
                carbsGrams = 30,
                fatGrams = 14,
                isDemo = false
            )
        )
        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = System.currentTimeMillis(),
                mealType = "Lunch",
                foodName = "Sample Meal",
                calories = 500,
                proteinGrams = 30,
                carbsGrams = 50,
                fatGrams = 10,
                isDemo = true
            )
        )

        assertEquals(2, userDataDao.getAllNutritionEntries().first().size)
        assertEquals(1, userDataDao.getRealNutritionEntries().first().size)

        userDataDao.purgeDemoNutrition()
        val remaining = userDataDao.getAllNutritionEntries().first()
        assertEquals(1, remaining.size)
        assertEquals("Real Eggs & Toast", remaining[0].foodName)
    }

    @Test
    fun `room database - personal records demo isolation and purge`() = runBlocking {
        val workoutDao = db.workoutDao()

        workoutDao.insertPersonalRecord(
            PersonalRecordEntity(
                exerciseId = "barbell_bench_press",
                exerciseName = "Barbell Bench Press",
                maxWeightKg = 100f,
                maxReps = 5,
                estimatedOneRepMaxKg = 116f,
                achievedDateMillis = 1000L,
                isDemo = false
            )
        )
        workoutDao.insertPersonalRecord(
            PersonalRecordEntity(
                exerciseId = "barbell_deadlift",
                exerciseName = "Barbell Deadlift",
                maxWeightKg = 140f,
                maxReps = 5,
                estimatedOneRepMaxKg = 162f,
                achievedDateMillis = 500L,
                isDemo = true
            )
        )

        assertEquals(2, workoutDao.getAllPersonalRecords().first().size)
        assertEquals(1, workoutDao.getRealPersonalRecords().first().size)

        workoutDao.purgeDemoPersonalRecords()
        val prsAfterPurge = workoutDao.getAllPersonalRecords().first()
        assertEquals(1, prsAfterPurge.size)
        assertEquals("barbell_bench_press", prsAfterPurge[0].exerciseId)
    }

    @Test
    fun `room database - completeWorkoutTransaction saves session and sets relation`() = runBlocking {
        val workoutDao = db.workoutDao()

        val session = WorkoutSessionEntity(
            id = "tx_session_1",
            title = "Push Day",
            routineId = "routine_upper_power",
            startTimeMillis = 1000L,
            endTimeMillis = 3000L,
            durationMinutes = 33,
            notes = "Good pump",
            totalVolumeKg = 1200f,
            isCompleted = true,
            isDemo = false
        )
        val sets = listOf(
            WorkoutSetEntity(
                sessionId = "tx_session_1",
                setNumber = 1,
                exerciseId = "bench_press",
                exerciseName = "Bench Press",
                weightKg = 80f,
                reps = 10,
                rpe = 8,
                isCompleted = true,
                isDemo = false
            ),
            WorkoutSetEntity(
                sessionId = "tx_session_1",
                setNumber = 2,
                exerciseId = "bench_press",
                exerciseName = "Bench Press",
                weightKg = 85f,
                reps = 8,
                rpe = 9,
                isCompleted = true,
                isDemo = false
            )
        )

        workoutDao.completeWorkoutTransaction(session, sets, emptyList())

        val workoutsWithSets = workoutDao.getAllWorkoutsWithSets().first()
        assertEquals(1, workoutsWithSets.size)
        assertEquals("tx_session_1", workoutsWithSets[0].session.id)
        assertEquals(2, workoutsWithSets[0].sets.size)
    }

    @Test
    fun `room database - wipeAllUserDataTransaction clears tables cleanly`() = runBlocking {
        val userDataDao = db.userDataDao()

        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = System.currentTimeMillis(),
                mealType = "Breakfast",
                foodName = "Oatmeal",
                calories = 250,
                proteinGrams = 10,
                carbsGrams = 45,
                fatGrams = 4,
                isDemo = false
            )
        )
        userDataDao.setConfig(AppConfigEntity("test_key", "test_val"))

        assertEquals(1, userDataDao.getAllNutritionEntries().first().size)
        assertNotNull(userDataDao.getConfig("test_key"))

        userDataDao.wipeAllUserDataTransaction()

        assertEquals(0, userDataDao.getAllNutritionEntries().first().size)
        assertNull(userDataDao.getConfig("test_key"))
    }

    @Test
    fun `extension registry - 3d exercise studio is registered and enabled by default`() {
        val studioExt = com.example.data.model.EXTENSION_REGISTRY.find { it.id == "ext_exercise_studio_3d" }
        assertNotNull("3D Exercise Studio extension must be registered in EXTENSION_REGISTRY", studioExt)
        assertEquals("3D Exercise Studio", studioExt?.name)
        assertEquals("Training", studioExt?.category)
        assertTrue("3D Exercise Studio should be enabled by default", studioExt?.defaultEnabled == true)
    }

    @Test
    fun `3d exercise studio repository - all 14 exercises loaded with keyframes, muscles, steps, and licensing`() {
        val repo = com.example.data.repository.ExerciseStudioRepository()
        val exercises = repo.getAllExercises()

        assertEquals("Should provide 14 representative 3D exercises", 14, exercises.size)

        // Verify key representative exercises exist
        val benchPress = exercises.find { it.id == "barbell_bench_press" }
        assertNotNull("Barbell bench press must exist", benchPress)
        assertEquals("Chest", benchPress?.category)
        assertTrue("Bench press keyframes must not be empty", benchPress!!.keyframes.isNotEmpty())
        assertTrue("Primary muscles must include chest", benchPress.primaryMuscles.any { it.name.contains("Pectoralis") || it.commonName.contains("Chest") })
        assertTrue("Execution steps must not be empty", benchPress.executionSteps.isNotEmpty())
        assertTrue("License type must be permissive", benchPress.assetMetadata.licenseType.contains("CC-BY") || benchPress.assetMetadata.licenseType.contains("Apache"))
        assertTrue("Bench press has genuine local GLB asset ready", benchPress.assetMetadata.isLocalAssetReady)

        val inclinePress = exercises.find { it.id == "incline_dumbbell_press" }
        assertNotNull(inclinePress)
        assertTrue("Incline press has genuine local GLB asset ready", inclinePress!!.assetMetadata.isLocalAssetReady)
        assertEquals(com.example.data.model.AnimationEngineType.GLB_ASSET, inclinePress.assetMetadata.engineType)

        val squat = exercises.find { it.id == "barbell_squat" }
        assertNotNull("Barbell squat must exist", squat)
        assertEquals("Legs", squat?.category)
        assertTrue(squat!!.keyframes.size >= 3)
        assertTrue("Squat has genuine local GLB asset ready", squat.assetMetadata.isLocalAssetReady)

        // Verify all 14 exercises have genuine 3D GLB assets ready
        assertEquals("All 14 exercises must have genuine GLB asset ready", 14, exercises.count { it.assetMetadata.isLocalAssetReady })
    }

    @Test
    fun `3d exercise studio repository - filtering by search query and category works accurately`() {
        val repo = com.example.data.repository.ExerciseStudioRepository()

        // Filter by category "Back"
        val backExercises = repo.filterExercises(categoryFilter = "Back")
        assertEquals(2, backExercises.size)
        assertTrue(backExercises.any { it.id == "lat_pulldown" })
        assertTrue(backExercises.any { it.id == "seated_cable_row" })

        // Filter by search query "dumbbell"
        val dumbbellExercises = repo.filterExercises(query = "dumbbell")
        assertTrue(dumbbellExercises.isNotEmpty())
        assertTrue(dumbbellExercises.all { 
            it.name.contains("dumbbell", ignoreCase = true) || 
            it.equipment.contains("dumbbell", ignoreCase = true) 
        })

        // Filter by difficulty "Intermediate"
        val intermediate = repo.filterExercises(difficultyFilter = "Intermediate")
        assertEquals(4, intermediate.size)
        assertTrue(intermediate.all { it.difficulty == "Intermediate" })
    }
}

