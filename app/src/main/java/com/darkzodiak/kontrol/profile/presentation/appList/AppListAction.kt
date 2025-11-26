package com.darkzodiak.kontrol.profile.presentation.appList

import com.darkzodiak.kontrol.core.data.local.entity.App

sealed interface AppListAction {
    object Dismiss: AppListAction
    object Save: AppListAction

    data class SelectApp(val app: App): AppListAction
    data class UnselectApp(val app: App): AppListAction
}