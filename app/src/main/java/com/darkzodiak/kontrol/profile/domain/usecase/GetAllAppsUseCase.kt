package com.darkzodiak.kontrol.profile.domain.usecase

import com.darkzodiak.kontrol.apps.domain.App
import com.darkzodiak.kontrol.apps.domain.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllAppsUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<List<App>> {
        return repository.getAllApps()
            .map { it.filterNot { app -> app.packageName in protectedPackages }.sortedBy { it.title } }
    }

    companion object {
        private val protectedPackages = setOf(
            BLOCKER_PACKAGE_NAME,
            SETTINGS_PACKAGE_NAME,
            DIALER_PACKAGE_NAME,
            CONTACTS_PACKAGE_NAME,
            MESSAGES_PACKAGE_NAME
        )

        private const val BLOCKER_PACKAGE_NAME = "com.darkzodiak.kontrol"
        private const val SETTINGS_PACKAGE_NAME = "com.android.settings"
        private const val DIALER_PACKAGE_NAME = "com.android.dialer"
        private const val CONTACTS_PACKAGE_NAME = "com.android.contacts"
        private const val MESSAGES_PACKAGE_NAME = "com.android.messaging"
    }
}