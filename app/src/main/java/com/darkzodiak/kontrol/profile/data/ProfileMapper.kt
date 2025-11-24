package com.darkzodiak.kontrol.profile.data

import android.util.Log
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
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
            editRestriction = profile.getEditRestriction()
        )
    }

    fun profileToProfileEntity(profile: Profile): ProfileEntity {
        return ProfileEntityBuilder()
            .withId(profile.id)
            .withName(profile.name)
            .withState(profile.state)
            .withEditRestriction(profile.editRestriction)
            .build()
    }

    private class ProfileEntityBuilder {
        private var id: Long? = null
        private var name: String = ""
        private var state: ProfileStateType = ProfileStateType.STOPPED
        private var pausedUntil: LocalDateTime? = null
        private var editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION
        private var password: String? = null
        private var randomTextLength: Int? = null
        private var restrictUntilDate: LocalDateTime? = null
        private var unlockAfterReachingUntilDate: Boolean? = null

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
                    password = restriction.password
                }
                is EditRestriction.RandomText -> {
                    editRestrictionType = EditRestrictionType.RANDOM_TEXT
                    randomTextLength = restriction.length
                }
                is EditRestriction.UntilDate -> {
                    editRestrictionType = EditRestrictionType.UNTIL_DATE
                    restrictUntilDate = restriction.date
                    unlockAfterReachingUntilDate = restriction.stopAfterReachingDate
                }
            }
        }

        fun build() = ProfileEntity(
            id = id,
            name = name,
            state = state,
            pausedUntil = pausedUntil,
            editRestrictionType = editRestrictionType,
            password = password,
            randomTextLength = randomTextLength,
            restrictUntilDate = restrictUntilDate,
            unlockAfterReachingUntilDate = unlockAfterReachingUntilDate
        )
    }

    private fun ProfileEntity.getState(): ProfileState {
        return when (state) {
            ProfileStateType.ACTIVE -> ProfileState.Active
            ProfileStateType.STOPPED -> ProfileState.Stopped
            ProfileStateType.PAUSED ->
                pausedUntil?.let { ProfileState.Paused(it) }
                    ?: handleInconsistentState(state)
        }
    }

    private fun ProfileEntity.getEditRestriction(): EditRestriction {
        fun handleDataError() = handleInconsistentRestriction(editRestrictionType)

        return when (editRestrictionType) {
            EditRestrictionType.NO_RESTRICTION -> EditRestriction.NoRestriction
            EditRestrictionType.PASSWORD -> {
                if (password == null) handleDataError()
                else EditRestriction.Password(password)
            }
            EditRestrictionType.RANDOM_TEXT -> {
                if (randomTextLength == null) handleDataError()
                else EditRestriction.RandomText(randomTextLength)
            }
            EditRestrictionType.UNTIL_DATE -> {
                if (restrictUntilDate == null || unlockAfterReachingUntilDate == null) {
                    handleDataError()
                } else {
                    EditRestriction.UntilDate(restrictUntilDate, unlockAfterReachingUntilDate)
                }
            }
        }
    }

    private fun handleInconsistentState(type: ProfileStateType): ProfileState {
        Log.d("Kontrol Log", "Data inconsistency: data for profile state ${type.name} is null")
        return ProfileState.Stopped
    }

    private fun handleInconsistentRestriction(type: EditRestrictionType): EditRestriction {
        Log.d("Kontrol Log", "Data inconsistency: data for restriction ${type.name} is null")
        return EditRestriction.NoRestriction
    }
}