package com.example.data.repository

import com.example.data.db.ExtensionDao
import com.example.data.db.ExtensionStateEntity
import com.example.data.model.EXTENSION_REGISTRY
import com.example.data.model.ExtensionDescriptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ExtensionItemUi(
    val descriptor: ExtensionDescriptor,
    val isInstalled: Boolean,
    val isEnabled: Boolean,
    val installedAtMillis: Long
)

class ExtensionRepository(private val extensionDao: ExtensionDao) {

    val allExtensions: Flow<List<ExtensionItemUi>> = extensionDao.getAllExtensionStates().map { stateEntities ->
        val stateMap = stateEntities.associateBy { it.id }
        EXTENSION_REGISTRY.map { desc ->
            val state = stateMap[desc.id]
            val installed = state?.isInstalled ?: desc.defaultEnabled
            val enabled = state?.isEnabled ?: desc.defaultEnabled
            ExtensionItemUi(
                descriptor = desc,
                isInstalled = installed,
                isEnabled = enabled,
                installedAtMillis = state?.installedAtMillis ?: 0L
            )
        }
    }

    suspend fun initializeDefaultStatesIfEmpty() {
        val initialEntities = EXTENSION_REGISTRY.map { desc ->
            // Let Nutrition Lab and Habit Builder be installed and enabled as starter extensions,
            // while user can enable or disable anytime or install others!
            val shouldEnable = desc.id == "ext_nutrition" || desc.id == "ext_habits"
            ExtensionStateEntity(
                id = desc.id,
                isInstalled = shouldEnable,
                isEnabled = shouldEnable,
                installedAtMillis = if (shouldEnable) System.currentTimeMillis() else 0L
            )
        }
        extensionDao.insertDefaultExtensionsIfNotExist(initialEntities)
    }

    suspend fun setExtensionEnabled(id: String, isEnabled: Boolean) {
        val current = extensionDao.getExtensionStateById(id)
        if (current == null) {
            extensionDao.upsertExtensionState(
                ExtensionStateEntity(
                    id = id,
                    isInstalled = true,
                    isEnabled = isEnabled,
                    installedAtMillis = System.currentTimeMillis()
                )
            )
        } else {
            extensionDao.setExtensionEnabled(id, isEnabled)
        }
    }

    suspend fun installExtension(id: String) {
        extensionDao.upsertExtensionState(
            ExtensionStateEntity(
                id = id,
                isInstalled = true,
                isEnabled = true,
                installedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun uninstallExtension(id: String) {
        // Disabling an extension removes its UI interface contributions while preserving underlying user logs
        val current = extensionDao.getExtensionStateById(id)
        if (current != null) {
            extensionDao.setExtensionInstalledAndEnabled(id, isInstalled = false, isEnabled = false)
        }
    }
}
