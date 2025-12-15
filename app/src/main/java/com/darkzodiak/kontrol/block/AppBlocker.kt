package com.darkzodiak.kontrol.block

import com.darkzodiak.kontrol.block.external_events.ExternalEvent
import com.darkzodiak.kontrol.block.external_events.ExternalEventBus
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayData
import com.darkzodiak.kontrol.overlay.OverlayDataCreator
import com.darkzodiak.kontrol.overlay.OverlayManager
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.ProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class AppBlocker @Inject constructor(
    private val repository: KontrolRepository,
    private val overlayManager: OverlayManager
) { // TODO(): Call this like Observer or what?

    private val overlayDataCreator = OverlayDataCreator()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var profileCheckJob: Job? = null
    private var appCloser: AppCloser? = null

    init {
        ExternalEventBus.bus
            .filterIsInstance<ExternalEvent.OpenApp>()
            .onEach { processApp(it.packageName) }
            .launchIn(scope)
    }

    fun setAppCloser(closer: AppCloser) {
        appCloser = closer
    }

    private suspend fun processApp(packageName: String) {
        cancelProfileCheckJob()
        if (appCloser == null) return

        profileCheckJob = repository.getProfilesWithApp(packageName)
            .filter { it.isNotEmpty() }
            .onEach { profiles ->
                val activeProfiles = profiles.filter { it.state is ProfileState.Active }
                if (activeProfiles.isEmpty()) return@onEach

                val hardProfile = activeProfiles.firstOrNull { it.appRestriction.isOneOf(hardRestrictions) }
                if (hardProfile != null) {
                    withContext(Dispatchers.Main) {
                        appCloser?.closeApp(packageName)
                        val data = overlayDataCreator.createDataFrom(packageName, hardProfile)
                        overlayManager.openOverlay(data) { }
                    }
                } else {
                    val softProfile = activeProfiles.first()
                    withContext(Dispatchers.Main) {
                        val data = overlayDataCreator.createDataFrom(packageName, softProfile)
                        overlayManager.openOverlay(data) { appCloser?.closeApp(packageName) }
                    }
                }
            }.launchIn(scope)
    }

    private fun cancelProfileCheckJob() {
        profileCheckJob?.cancel()
        profileCheckJob = null
    }

    companion object {
        private val hardRestrictions = setOf(
            AppRestriction.SimpleBlock::class
        )

        private fun AppRestriction.isOneOf(types: Set<KClass<out AppRestriction>>): Boolean {
            return types.any { it.isInstance(this) }
        }
    }
}