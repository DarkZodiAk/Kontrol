package com.darkzodiak.kontrol.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.CoroutineScope

/**
 * Ensures UI responsiveness is enabled immediately upon composition and guarantees
 * it remains enabled when the screen resumes, specifically addressing rapid navigation
 * edge cases (e.g., user quickly opens and closes a subsequent screen).
 *
 * **Behavior:**
 * - Calls [render] immediately on first composition via [LaunchedEffect].
 * - Re-invokes [render] when the associated [Lifecycle] reaches `RESUMED` via [LifecycleResumeEffect].
 * - Executes [block] in a coroutine after rendering.
 * - `onPauseOrDispose` is intentionally empty to avoid race conditions with navigation callbacks.
 *
 * **Important:** Disabling UI responsiveness should be managed by your `ViewModel`.
 *
 * @param key1 Controls when the effects are invalidated and restarted (standard Compose effect key).
 * @param render Synchronous callback to enable UI responsiveness or trigger immediate layout passes.
 *               ⚠️ Should be idempotent, as it may be called twice in quick succession.
 * @param block Suspending code to run asynchronously once the UI is ready.
 */
@Composable
fun RenderedLaunchEffect(key1: Any?, render: () -> Unit, block: suspend CoroutineScope.() -> Unit) {
    LifecycleResumeEffect(key1) {
        render()
        onPauseOrDispose { }
    }

    LaunchedEffect(key1) {
        render()
        block()
    }
}