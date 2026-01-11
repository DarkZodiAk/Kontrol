package com.darkzodiak.kontrol.profile.presentation

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.core.presentation.warning.WarningType
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.EditRestriction

data class ProfileScreenState(
    val isNewProfile: Boolean = true,
    val name: String = "",
    val apps: List<App> = emptyList(),

    val appRestriction: AppRestriction = AppRestriction.SimpleBlock,
    val editRestriction: EditRestriction = EditRestriction.NoRestriction,

    val unsaved: Boolean = false,
    val protectedMode: Boolean = false,
    val warnings: List<WarningType> = emptyList()
)
