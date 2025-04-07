package com.darkzodiak.kontrol.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.darkzodiak.kontrol.data.local.dao.AppDao
import com.darkzodiak.kontrol.data.local.entity.App
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao
) {
    private val packageManager = context.packageManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    private suspend fun getAllInstalledApps() {
        val currentApps = appDao.getAllApps().first()
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val newApps = apps
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                App(
                    packageName = it.packageName,
                    title = it.loadLabel(packageManager).toString(),
                )
            }

        val appsToDelete = currentApps.filter { app ->
            newApps.none { it.packageName == app.packageName }
        }
        val appsToInsert = newApps.filter { app ->
            currentApps.none { it.packageName == app.packageName }
        }

        appsToDelete.forEach {
            scope.launch {
                appDao.deleteApp(it)
                deleteIcon(it.packageName)
            }
        }

        appsToInsert.forEach {
            scope.launch {
                val uri = getAppIconAndSave(it.packageName)
                appDao.insertApp(it.copy(icon = uri))
            }
        }
    }

    fun update() {
        scope.launch {
            getAllInstalledApps()
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
}