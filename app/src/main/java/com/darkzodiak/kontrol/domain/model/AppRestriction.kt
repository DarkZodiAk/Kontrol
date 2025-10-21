package com.darkzodiak.kontrol.domain.model

sealed interface AppRestriction {
    object SimpleBlock: AppRestriction
} // TODO(): Make use of the class in UI (in future)