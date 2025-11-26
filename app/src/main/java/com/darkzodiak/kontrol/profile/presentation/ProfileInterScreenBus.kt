package com.darkzodiak.kontrol.profile.presentation

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileInterScreenBus {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val appListFlow = MutableSharedFlow<List<App>>(replay = 1)
    private val editRestrictionFlow = MutableSharedFlow<EditRestriction>(replay = 1)
    private val appRestrictionFlow = MutableSharedFlow<AppRestriction>(replay = 1)

    val appList = appListFlow.asSharedFlow()
    val editRestriction = editRestrictionFlow.asSharedFlow()
    val appRestriction = appRestrictionFlow.asSharedFlow()

    fun sendAppList(apps: List<App>) = scope.launch {
        appListFlow.emit(apps)
    }

    fun sendEditRestriction(restriction: EditRestriction) = scope.launch {
        editRestrictionFlow.emit(restriction)
    }

    fun sendAppRestriction(restriction: AppRestriction) = scope.launch {
        appRestrictionFlow.emit(restriction)
    }

    companion object {
        private var INSTANCE: ProfileInterScreenBus? = null

        fun get(): ProfileInterScreenBus {
            if (INSTANCE == null) INSTANCE = ProfileInterScreenBus()
            return requireNotNull(INSTANCE)
        }

        fun clear() { INSTANCE = null }
    }
}