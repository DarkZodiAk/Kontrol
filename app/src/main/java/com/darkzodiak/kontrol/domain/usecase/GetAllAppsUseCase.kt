package com.darkzodiak.kontrol.domain.usecase

import com.darkzodiak.kontrol.data.local.entity.App
import com.darkzodiak.kontrol.domain.KontrolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllAppsUseCase @Inject constructor(
    private val repository: KontrolRepository
) {
    operator fun invoke(): Flow<List<App>> {
        return repository.getAllApps()
            .map { it.filterNot { app -> app.packageName == BLOCKER_APP_ID } }
    }

    companion object {
        private const val BLOCKER_APP_ID = "com.darkzodiak.kontrol"
    }
}