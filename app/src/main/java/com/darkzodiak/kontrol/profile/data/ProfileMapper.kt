package com.darkzodiak.kontrol.profile.data

import android.util.Log
import com.darkzodiak.kontrol.profile.data.local.AppRestrictionType
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
import com.darkzodiak.kontrol.profile.domain.AppRestriction
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.profile.domain.ProfileState
import java.time.LocalDateTime

object ProfileMapper {
    fun profileEntityToProfile(profile: ProfileEntity): Profile {
        return Profile(
            id = profile.id,
            name = profile.name,
            state = profile.getState(),
            appRestriction = profile.getAppRestriction(),
            editRestriction = profile.getEditRestriction()
        )
    }

    fun profileToProfileEntity(profile: Profile): ProfileEntity {
        return ProfileEntityBuilder()
            .withId(profile.id)
            .withName(profile.name)
            .withState(profile.state)
            .withAppRestriction(profile.appRestriction)
            .withEditRestriction(profile.editRestriction)
            .build()
    }

    private class ProfileEntityBuilder {
        private var id: Long? = null
        private var name: String = ""
        private var state: ProfileStateType = ProfileStateType.STOPPED
        private var pausedUntil: LocalDateTime? = null
        private var appRestrictionType: AppRestrictionType = AppRestrictionType.SIMPLE_BLOCK
        private var aPassword: String? = null
        private var aRandomTextLength: Int? = null
        private var editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION
        private var ePassword: String? = null
        private var eRandomTextLength: Int? = null
        private var eRestrictUntilDate: LocalDateTime? = null
        private var eStopAfterReachingUntilDate: Boolean? = null
        private var eStopAfterReboot: Boolean? = null

        fun withId(id: Long?) = apply { this.id = id }
        fun withName(name: String) = apply { this.name = name }
        fun withState(state: ProfileState) = apply {
            when (state) {
                ProfileState.Active -> {
                    this.state = ProfileStateType.ACTIVE
                }
                ProfileState.Stopped -> {
                    this.state = ProfileStateType.STOPPED
                }
                is ProfileState.Paused -> {
                    this.state = ProfileStateType.PAUSED
                    this.pausedUntil = state.until
                }
            }
        }
        fun withEditRestriction(restriction: EditRestriction) = apply {
            when (restriction) {
                EditRestriction.NoRestriction -> {
                    editRestrictionType = EditRestrictionType.NO_RESTRICTION
                }
                is EditRestriction.Password -> {
                    editRestrictionType = EditRestrictionType.PASSWORD
                    ePassword = restriction.password
                }
                is EditRestriction.RandomText -> {
                    editRestrictionType = EditRestrictionType.RANDOM_TEXT
                    eRandomTextLength = restriction.length
                }
                is EditRestriction.UntilDate -> {
                    editRestrictionType = EditRestrictionType.UNTIL_DATE
                    eRestrictUntilDate = restriction.date
                    eStopAfterReachingUntilDate = restriction.stopAfterReachingDate
                }
                is EditRestriction.UntilReboot -> {
                    editRestrictionType = EditRestrictionType.UNTIL_REBOOT
                    eStopAfterReboot = restriction.stopAfterReboot
                }
            }
        }
        fun withAppRestriction(restriction: AppRestriction) = apply {
            when (restriction) {
                is AppRestriction.Password -> {
                    appRestrictionType = AppRestrictionType.PASSWORD
                    aPassword = restriction.password
                }
                is AppRestriction.RandomText -> {
                    appRestrictionType = AppRestrictionType.RANDOM_TEXT
                    aRandomTextLength = restriction.length
                }
                AppRestriction.SimpleBlock -> {
                    appRestrictionType = AppRestrictionType.SIMPLE_BLOCK
                }
            }
        }

        fun build() = ProfileEntity(
            id = id,
            name = name,
            state = state,
            pausedUntil = pausedUntil,
            appRestrictionType = appRestrictionType,
            aPassword = aPassword,
            aRandomTextLength = aRandomTextLength,
            editRestrictionType = editRestrictionType,
            ePassword = ePassword,
            eRandomTextLength = eRandomTextLength,
            eRestrictUntilDate = eRestrictUntilDate,
            eStopAfterReachingUntilDate = eStopAfterReachingUntilDate,
            eStopAfterReboot = eStopAfterReboot
        )
    }

    private fun ProfileEntity.getState(): ProfileState {
        return when (state) {
            ProfileStateType.ACTIVE -> ProfileState.Active
            ProfileStateType.STOPPED -> ProfileState.Stopped
            ProfileStateType.PAUSED ->
                pausedUntil?.let { ProfileState.Paused(it) }
                    ?: handleInvalidState(state)
        }
    }
    
    private fun ProfileEntity.getAppRestriction(): AppRestriction {
        fun handleDataError() = handleInvalidAppRestriction(appRestrictionType)
        
        return when (appRestrictionType) {
            AppRestrictionType.SIMPLE_BLOCK -> AppRestriction.SimpleBlock
            AppRestrictionType.RANDOM_TEXT -> {
                if (aRandomTextLength == null) handleDataError()
                else AppRestriction.RandomText(aRandomTextLength)
            }
            AppRestrictionType.PASSWORD -> {
                if (aPassword == null) handleDataError()
                else AppRestriction.Password(aPassword)
            }
        }
    }

    private fun ProfileEntity.getEditRestriction(): EditRestriction {
        fun handleDataError() = handleInvalidEditRestriction(editRestrictionType)

        return when (editRestrictionType) {
            EditRestrictionType.NO_RESTRICTION -> EditRestriction.NoRestriction
            EditRestrictionType.PASSWORD -> {
                if (ePassword == null) handleDataError()
                else EditRestriction.Password(ePassword)
            }
            EditRestrictionType.RANDOM_TEXT -> {
                if (eRandomTextLength == null) handleDataError()
                else EditRestriction.RandomText(eRandomTextLength)
            }
            EditRestrictionType.UNTIL_DATE -> {
                if (eRestrictUntilDate == null || eStopAfterReachingUntilDate == null) {
                    handleDataError()
                } else {
                    EditRestriction.UntilDate(eRestrictUntilDate, eStopAfterReachingUntilDate)
                }
            }
            EditRestrictionType.UNTIL_REBOOT -> {
                if (eStopAfterReboot == null) {
                    handleDataError()
                } else {
                    EditRestriction.UntilReboot(eStopAfterReboot)
                }
            }
        }
    }

    private fun handleInvalidState(type: ProfileStateType): ProfileState {
        Log.d("Kontrol Log", "Invalid data: data for profile state ${type.name} is null")
        return ProfileState.Stopped
    }

    private fun handleInvalidEditRestriction(type: EditRestrictionType): EditRestriction {
        Log.d("Kontrol Log", "Invalid data: data for restriction ${type.name} is null")
        return EditRestriction.NoRestriction
    }

    private fun handleInvalidAppRestriction(type: AppRestrictionType): AppRestriction {
        Log.d("Kontrol Log", "Invalid data: data for restriction ${type.name} is null")
        return AppRestriction.SimpleBlock
    }
}