package com.example.data.repository

import com.example.data.model.EXERCISE_3D_LIBRARY
import com.example.data.model.Exercise3d
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface Exercise3dProvider {
    val providerId: String
    val providerName: String
    val isAvailableOffline: Boolean
    fun getExercises(): List<Exercise3d>
    fun getExerciseById(id: String): Exercise3d?
}

class LocalBiomechanical3dProvider : Exercise3dProvider {
    override val providerId: String = "local_biomechanical_engine"
    override val providerName: String = "Pulse Biomechanical 3D Engine"
    override val isAvailableOffline: Boolean = true

    override fun getExercises(): List<Exercise3d> = EXERCISE_3D_LIBRARY

    override fun getExerciseById(id: String): Exercise3d? {
        return EXERCISE_3D_LIBRARY.find { it.id == id }
    }
}

/**
 * Repository coordinating 3D exercise retrieval, filtering, and provider switching.
 * Architecture is designed to plug in external commercial APIs (e.g. WorkoutX, RepDB, MoveKit)
 * or local GLB asset bundles without touching UI screens.
 */
class ExerciseStudioRepository(
    private val activeProvider: Exercise3dProvider = LocalBiomechanical3dProvider()
) {
    private val _exercises = MutableStateFlow(activeProvider.getExercises())
    val exercises: Flow<List<Exercise3d>> = _exercises.asStateFlow()

    fun getAllExercises(): List<Exercise3d> = _exercises.value

    fun getExerciseById(id: String): Exercise3d? {
        return activeProvider.getExerciseById(id) ?: _exercises.value.find { it.id == id }
    }

    fun filterExercises(
        query: String = "",
        categoryFilter: String = "All",
        equipmentFilter: String = "All",
        difficultyFilter: String = "All"
    ): List<Exercise3d> {
        val trimmedQuery = query.trim().lowercase()
        return _exercises.value.filter { ex ->
            val matchesQuery = trimmedQuery.isEmpty() ||
                ex.name.lowercase().contains(trimmedQuery) ||
                ex.category.lowercase().contains(trimmedQuery) ||
                ex.equipment.lowercase().contains(trimmedQuery) ||
                ex.primaryMuscles.any { it.name.lowercase().contains(trimmedQuery) || it.commonName.lowercase().contains(trimmedQuery) }

            val matchesCategory = categoryFilter == "All" || ex.category.equals(categoryFilter, ignoreCase = true)
            val matchesEquipment = equipmentFilter == "All" || ex.equipment.equals(equipmentFilter, ignoreCase = true)
            val matchesDifficulty = difficultyFilter == "All" || ex.difficulty.equals(difficultyFilter, ignoreCase = true)

            matchesQuery && matchesCategory && matchesEquipment && matchesDifficulty
        }
    }

    fun getAvailableCategories(): List<String> {
        return listOf("All") + _exercises.value.map { it.category }.distinct().sorted()
    }

    fun getAvailableEquipment(): List<String> {
        return listOf("All") + _exercises.value.map { it.equipment }.distinct().sorted()
    }

    fun getAvailableDifficulties(): List<String> {
        return listOf("All", "Beginner", "Intermediate", "Advanced")
    }
}
