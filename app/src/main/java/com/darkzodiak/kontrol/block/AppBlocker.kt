package com.darkzodiak.kontrol.block

import android.util.Log
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.external_events.ExternalEvent
import com.darkzodiak.kontrol.external_events.ExternalEventBus
import com.darkzodiak.kontrol.overlay.OverlayDataCreator
import com.darkzodiak.kontrol.overlay.OverlayManager
import com.darkzodiak.kontrol.profile.domain.model.AppRestriction
import com.darkzodiak.kontrol.profile.domain.model.Profile
import com.darkzodiak.kontrol.profile.domain.model.ProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class AppBlocker @Inject constructor(
    private val repository: KontrolRepository,
    private val overlayManager: OverlayManager,
    private val overlayDataCreator: OverlayDataCreator
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var profileCheckJob: Job? = null
    private var appCloser: AppCloser? = null
    private var nextAppToIgnore: AtomicReference<String?> = AtomicReference()

    init {
        ExternalEventBus.bus
            .distinctUntilChanged()
            .onEach { event ->
                Log.d("Kontrol log", event.toString())
                if (event is ExternalEvent.OpenApp) {
                    processApp(event.packageName)
                } else {
                    nextAppToIgnore.set(null)
                }
                if (event is ExternalEvent.ReturnToLauncher) overlayManager.closeOverlay()
            }
            .launchIn(scope)
    }

    fun setAppCloser(closer: AppCloser) {
        appCloser = closer
    }

    private suspend fun processApp(packageName: String) {
        cancelProfileCheckJob()
        if (nextAppToIgnore.get() == packageName || appCloser == null) return
        nextAppToIgnore.set(null)

        val profiles = repository.getProfilesWithApp(packageName)
        if (profiles == null) {
            overlayManager.closeOverlayImmediately()
            return
        }

        profileCheckJob = profiles
            .onEach { profiles ->
                val activeProfiles = profiles.filter { it.state is ProfileState.Active }
                if (activeProfiles.isNotEmpty()) {
                    processProfiles(packageName, activeProfiles)
                } else {
                    withContext(Dispatchers.Main) {
                        overlayManager.closeOverlayImmediately()
                    }
                }
            }.launchIn(scope)
    }

    suspend fun processProfiles(packageName: String, activeProfiles: List<Profile>) {
        if (activeProfiles.isEmpty()) return

        val hardProfile = activeProfiles.firstOrNull { it.appRestriction.isOneOf(hardRestrictions) }

        if (hardProfile != null) {
            val data = overlayDataCreator.createDataFrom(packageName, hardProfile)
            overlayManager.openOverlay(
                data = data,
                onBlock = { appCloser?.closeApp(packageName) }
            )
        } else {
            val softProfile = activeProfiles.first()
            val data = overlayDataCreator.createDataFrom(packageName, softProfile, activeProfiles.size > 1)
            overlayManager.openOverlay(
                data = data,
                onBlock = { appCloser?.closeApp(packageName) },
                onProceed = { nextAppToIgnore.set(packageName) }
            )
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