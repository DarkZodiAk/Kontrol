package com.darkzodiak.kontrol.core.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import com.darkzodiak.kontrol.core.data.local.entity.App
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao
) {
    private val packageManager = context.packageManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex()

    fun onAppInstalled(packageName: String) {
        runWithLock {
            val appInfo = getReachableAppInfo(packageName) ?: return@runWithLock

            val app = App(
                packageName = packageName,
                title = appInfo.loadLabel(packageManager).toString(),
                icon = getAppIconAndSave(packageName)
            )

            appDao.insertApp(app)
        }
    }

    fun onAppDeleted(packageName: String) {
        runWithLock {
            appDao.deleteAppByPackageName(packageName)
            deleteIcon(packageName)
        }
    }

    fun onAppReplaced(packageName: String) {
        runWithLock {
            val appInfo = getReachableAppInfo(packageName) ?: return@runWithLock
            val app = appDao.getAppByPackageName(packageName) ?: return@runWithLock
            val iconUri = getAppIconAndSave(packageName)
            appDao.upsertApp(
                app.copy(
                    title = appInfo.loadLabel(packageManager).toString(),
                    icon = iconUri
                )
            )
        }
    }

    fun updateAll() {
        runWithLock {
            scanAllApps()
        }
    }

    fun getCurrentLauncherPackageName(): String {
        val intent = Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        }
        return packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)!!
            .activityInfo
            .packageName
    }

    private suspend fun scanAllApps() {
        val currentApps = appDao.getAllApps().first()
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val newApps = apps
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                App(
                    packageName = it.packageName,
                    title = it.loadLabel(packageManager).toString(),
                )
            }.associateBy { it.packageName }

        // Remove all deleted apps from DB
        currentApps.filter { app ->
            app.packageName !in newApps
        }.forEach {
            scope.launch {
                appDao.deleteApp(it)
                deleteIcon(it.packageName)
            }
        }

        // Add/Update all scanned apps, even if they already exist in DB
        // Reason: any app might change its icon
        newApps.forEach {
            scope.launch {
                val iconUri = getAppIconAndSave(it.value.packageName)
                appDao.upsertApp(it.value.copy(icon = iconUri))
            }
        }
    }

    private fun getReachableAppInfo(packageName: String): ApplicationInfo? {
        return packageManager
            .getInstalledApplications(PackageManager.GET_META_DATA)
            .firstOrNull {
                it.packageName == packageName &&
                        packageManager.getLaunchIntentForPackage(it.packageName) != null
            }
    }

    private fun deleteIcon(packageName: String) {
        val filename = "$packageName.png"
        val file = File(context.filesDir, filename)
        file.delete()
    }

    private fun getAppIconAndSave(packageName: String): String {
        try {
            val icon = packageManager.getApplicationIcon(packageName).toBitmap()
            val filename = "$packageName.png"
            val file = File(context.filesDir, filename)
            saveBitmapToFile(icon, file)
            return Uri.fromFile(file).toString()
        } catch(e: IOException) {
            return ""
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
            }
        } catch(_: IOException) {}
    }

    private fun runWithLock(block: suspend CoroutineScope.() -> Unit) = scope.launch {
        mutex.withLock { block() }
    }
}