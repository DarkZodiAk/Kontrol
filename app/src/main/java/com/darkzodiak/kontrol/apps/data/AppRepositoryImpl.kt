package com.darkzodiak.kontrol.apps.data

import android.content.Context
import com.darkzodiak.kontrol.apps.data.AppMapper.entityToDomain
import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.apps.domain.AppRepository
import com.darkzodiak.kontrol.core.data.local.dao.AppDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDao: AppDao,
    private val appScanner: AppScanner
): AppRepository {

    override fun initializeAppSync() {
        appScanner.syncInstalledApps()
        AppChangedReceiver(appScanner).register(context)
    }

    override fun getAllApps(): Flow<List<App>> {
        return appDao.getAllApps().map {
            AppMapper.entityListToDomainList(it)
        }
    }

    override suspend fun getAppById(id: Long): App? {
        val app = appDao.getAppById(id)
        return if (app?.id == null) null else app.entityToDomain()
    }

    override suspend fun getAppByPackageName(packageName: String): App? {
        val app = appDao.getAppByPackageName(packageName)
        return if (app?.id == null) null else app.entityToDomain()
    }

    override suspend fun deleteAppById(appId: Long) {
        appDao.deleteAppById(appId)
    }
}