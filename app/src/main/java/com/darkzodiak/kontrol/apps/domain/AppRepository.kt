package com.darkzodiak.kontrol.apps.domain

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun syncInstalledApps()
    fun getAllApps(): Flow<List<App>>
    suspend fun getAppById(id: Long): App?
    suspend fun getAppByPackageName(packageName: String): App?
    suspend fun deleteAppById(appId: Long)
}