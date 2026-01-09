package com.darkzodiak.kontrol.block

import com.darkzodiak.kontrol.external_events.ExternalEvent
import com.darkzodiak.kontrol.external_events.ExternalEventBus
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayDataCreator
import com.darkzodiak.kontrol.overlay.OverlayManager
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.profile.domain.ProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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
) {

    private val overlayDataCreator = OverlayDataCreator()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var profileCheckJob: Job? = null
    private var appCloser: AppCloser? = null
    private var nextAppToIgnore: String? = null
    private var prevEvent: ExternalEvent = ExternalEvent.ReturnToLauncher

    init {
        ExternalEventBus.bus
            .distinctUntilChanged()
            .onEach { event ->
                if (event is ExternalEvent.OpenApp) {
                    if (prevEvent is ExternalEvent.ReturnToLauncher) nextAppToIgnore = null
                    processApp(event.packageName)
                }
                prevEvent = event
            }
            .launchIn(scope)
    }

    fun setAppCloser(closer: AppCloser) {
        appCloser = closer
    }

    private suspend fun processApp(packageName: String) {
        cancelProfileCheckJob()
        val appToIgnore = nextAppToIgnore
        nextAppToIgnore = null
        if (appToIgnore == packageName || appCloser == null) return

        profileCheckJob = repository.getProfilesWithApp(packageName)
            .filter { it.isNotEmpty() }
            .onEach { profiles ->
                processProfiles(packageName, profiles)
            }.launchIn(scope)
    }

    suspend fun processProfiles(packageName: String, profiles: List<Profile>) {
        val activeProfiles = profiles.filter { it.state is ProfileState.Active }
        if (activeProfiles.isEmpty()) return

        val hardProfile = activeProfiles.firstOrNull { it.appRestriction.isOneOf(hardRestrictions) }

        withContext(Dispatchers.Main) {
            if (hardProfile != null) {
                val data = overlayDataCreator.createDataFrom(packageName, hardProfile)
                overlayManager.openOverlay(
                    data = data,
                    onBlock = { appCloser?.closeApp(packageName) }
                )
            } else {
                val softProfile = activeProfiles.first()
                val data = overlayDataCreator.createDataFrom(packageName, softProfile)
                overlayManager.openOverlay(
                    data = data,
                    onBlock = { appCloser?.closeApp(packageName) },
                    onProceed = { nextAppToIgnore = packageName }
                )
            }
        }
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