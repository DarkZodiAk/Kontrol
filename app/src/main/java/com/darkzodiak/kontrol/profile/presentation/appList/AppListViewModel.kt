package com.darkzodiak.kontrol.profile.presentation.appList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkzodiak.kontrol.core.domain.usecase.GetAllAppsUseCase
import com.darkzodiak.kontrol.profile.presentation.ProfileInterScreenBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    getAllAppsUseCase: GetAllAppsUseCase
): ViewModel() {

    private var rendered = false
    private val interScreenCache = ProfileInterScreenBus.get()

    var state by mutableStateOf(AppListState())
        private set

    init {
        getAllAppsUseCase().onEach { apps ->
            state = state.copy(apps = apps)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            state = state.copy(selectedApps = interScreenCache.appList.first())
        }
    }

    fun render() {
        rendered = true
    }

    fun onAction(action: AppListAction) {
        if (rendered.not()) return
        when (action) {
            AppListAction.Dismiss -> {
                rendered = false
            }
            AppListAction.Save -> {
                rendered = false
                interScreenCache.sendAppList(state.selectedApps)
            }
            is AppListAction.SelectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps + action.app,
                    unsaved = true
                )
            }
            is AppListAction.UnselectApp -> {
                state = state.copy(
                    selectedApps = state.selectedApps.filter { it.id != action.app.id },
                    unsaved = true
                )
            }
        }
    }
}