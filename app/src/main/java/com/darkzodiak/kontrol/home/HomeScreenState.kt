package com.darkzodiak.kontrol.home

import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import java.time.LocalDateTime

data class HomeScreenState(
    val profiles: List<Profile> = emptyList(),
    val curTime: LocalDateTime = LocalDateTime.now(),

    val restrictionDialogVisible: Boolean = false,
    val curRestriction: EditRestriction = EditRestriction.NoRestriction,

    val pauseDialogVisible: Boolean = false,
    val activateAfterDialogVisible: Boolean = false,

    val permissions: Permissions = Permissions()
) {
    data class Permissions(
        val hasAccessibilityPermission: Boolean = false,
        val hasAlertWindowPermission: Boolean = false,
        val hasEssentialPermissions: Boolean = false
    )
}
