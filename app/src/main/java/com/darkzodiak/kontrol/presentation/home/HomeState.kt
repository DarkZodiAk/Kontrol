package com.darkzodiak.kontrol.presentation.home

import com.darkzodiak.kontrol.domain.model.EditRestriction
import com.darkzodiak.kontrol.domain.model.Profile

data class HomeState(
    val profiles: List<Profile> = emptyList(),

    val pendingAction: HomeAction = HomeAction.None,
    val curRestriction: EditRestriction = EditRestriction.NoRestriction,

    val hasUsageStatsPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasAlertWindowPermission: Boolean = false,
    val hasAllPermissions: Boolean = false
)
