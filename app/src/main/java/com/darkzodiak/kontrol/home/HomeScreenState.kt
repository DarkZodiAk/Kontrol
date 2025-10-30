package com.darkzodiak.kontrol.home

import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile

data class HomeScreenState(
    val profiles: List<Profile> = emptyList(),

    val restrictionDialogVisible: Boolean = false,
    val curRestriction: EditRestriction = EditRestriction.NoRestriction,

    val pauseDialogVisible: Boolean = false,
    val activateAfter

    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
