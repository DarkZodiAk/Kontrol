package com.darkzodiak.kontrol.data.block

import com.darkzodiak.kontrol.data.block.external_events.ExternalEvent
import com.darkzodiak.kontrol.data.block.external_events.ExternalEventBus
import com.darkzodiak.kontrol.domain.KontrolRepository
import com.darkzodiak.kontrol.overlay.OverlayData
import com.darkzodiak.kontrol.overlay.OverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
        scope.launch {
            ExternalEventBus.bus
                .filterIsInstance<ExternalEvent.OpenApp>()
                .onEach { processApp(it.packageName) }
        }
    }

    fun setAppCloser(closer: AppCloser) {
        appCloser = closer
    }

    private suspend fun processApp(packageName: String) {
        if (appCloser == null) return
        if (repository.isAppInProfiles(packageName)) {
            withContext(Dispatchers.Main) {
                appCloser?.closeApp(packageName)
                overlayManager.openOverlay(OverlayData.SimpleBlock(packageName)) {}
            }
        }
    }
}