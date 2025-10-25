package com.darkzodiak.kontrol.profile.presentation

import com.darkzodiak.kontrol.core.data.local.entity.App
import com.darkzodiak.kontrol.profile.domain.EditRestriction

data class ProfileState(
    val isNewProfile: Boolean = true,
    val name: String = "",
    val apps: List<App> = emptyList(),

    val selectedApps: List<App> = emptyList(),
    val selectedUnsaved: List<App> = emptyList(),

    val editRestriction: EditRestriction = EditRestriction.NoRestriction,
    val editRestrictionUnsaved: EditRestriction = EditRestriction.NoRestriction,

    val warning: String? = null,
)
