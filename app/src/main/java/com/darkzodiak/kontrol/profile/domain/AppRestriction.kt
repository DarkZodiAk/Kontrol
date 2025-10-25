package com.darkzodiak.kontrol.profile.domain

sealed interface AppRestriction {
    object SimpleBlock: AppRestriction
} // TODO(): Make use of the class in UI (in future)