package com.darkzodiak.kontrol.data

import android.content.Context
import android.content.pm.PackageManager
import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.entity.App
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao
) {
    private val packageManager = context.packageManager
    private val scope = CoroutineScope(Dispatchers.IO)

    private suspend fun getAllInstalledApps() {
        val currentApps = appDao.getAllApps().first()
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val newApps = apps
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                App(
                    packageName = it.packageName,
                    title = it.loadLabel(packageManager).toString()
                )
            }

        val appsToDelete = currentApps.filter { app ->
            newApps.none { it.packageName == app.packageName }
        }
        val appsToInsert = newApps.filter { app ->
            currentApps.none { it.packageName == app.packageName }
        }

        appsToDelete.forEach {
            appDao.deleteApp(it)
        }
        appsToInsert.forEach {
            appDao.insertApp(it)
        }
    }

    fun update() {
        scope.launch {
            getAllInstalledApps()
        }
    }
}