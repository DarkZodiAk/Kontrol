package com.darkzodiak.kontrol.block

import com.darkzodiak.kontrol.block.external_events.ExternalEvent
import com.darkzodiak.kontrol.block.external_events.ExternalEventBus
import com.darkzodiak.kontrol.core.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayData
import com.darkzodiak.kontrol.overlay.OverlayManager
import com.darkzodiak.kontrol.profile.domain.ProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppBlocker @Inject constructor(
    private val repository: KontrolRepository,
    private val overlayManager: OverlayManager
) { // TODO(): Call this like Observer or what?

    private val scope = CoroutineScope(Dispatchers.IO)
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
        if (appCloser == null) return

        val profiles = repository.getProfilesWithApp(packageName)
        if (profiles.isEmpty()) return

        if (profiles.any { it.state is ProfileState.Active }) {
            withContext(Dispatchers.Main) {
                appCloser?.closeApp(packageName)
                overlayManager.openOverlay(OverlayData.SimpleBlock(packageName)) {}
            }
        }
    }
}