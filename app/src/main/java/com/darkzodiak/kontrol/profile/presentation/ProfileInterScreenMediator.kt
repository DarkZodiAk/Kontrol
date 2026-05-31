package com.darkzodiak.kontrol.profile.presentation

import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.profile.domain.model.AppRestriction
import com.darkzodiak.kontrol.profile.domain.model.EditRestriction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ProfileInterScreenMediator {
    private val appListFlow = MutableSharedFlow<List<App>>(replay = 1)
    private val editRestrictionFlow = MutableSharedFlow<EditRestriction>(replay = 1)
    private val appRestrictionFlow = MutableSharedFlow<AppRestriction>(replay = 1)

    val appList = appListFlow.asSharedFlow()
    val editRestriction = editRestrictionFlow.asSharedFlow()
    val appRestriction = appRestrictionFlow.asSharedFlow()

    suspend fun sendAppList(apps: List<App>) {
        appListFlow.emit(apps)
    }

    suspend fun sendEditRestriction(restriction: EditRestriction) {
        editRestrictionFlow.emit(restriction)
    }

    suspend fun sendAppRestriction(restriction: AppRestriction) {
        appRestrictionFlow.emit(restriction)
    }

    companion object {
        @Volatile private var INSTANCE: ProfileInterScreenMediator? = null
        fun get(): ProfileInterScreenMediator = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ProfileInterScreenMediator().also { INSTANCE = it }
        }
        fun clear() { INSTANCE = null }
    }
}