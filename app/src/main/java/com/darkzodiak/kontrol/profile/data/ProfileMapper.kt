package com.darkzodiak.kontrol.profile.data

import android.util.Log
import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.ProfileStateType
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile
import com.darkzodiak.kontrol.profile.domain.ProfileState

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
        private var pausedUntil: Long? = null
        private var editRestrictionType: EditRestrictionType = EditRestrictionType.NO_RESTRICTION
        private var password: String? = null
        private var randomTextLength: Int? = null

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
            }
        }

        fun build() = ProfileEntity(
            id = id,
            name = name,
            state = state,
            pausedUntil = pausedUntil,
            editRestrictionType = editRestrictionType,
            password = password,
            randomTextLength = randomTextLength
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
            EditRestrictionType.PASSWORD ->
                password?.let { EditRestriction.Password(it) }
                    ?: handleDataError()
            EditRestrictionType.RANDOM_TEXT ->
                randomTextLength?.let { EditRestriction.RandomText(it) }
                    ?: handleDataError()
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