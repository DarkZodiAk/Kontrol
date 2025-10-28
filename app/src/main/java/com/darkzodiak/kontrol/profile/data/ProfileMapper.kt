package com.darkzodiak.kontrol.profile.data

import com.darkzodiak.kontrol.profile.data.local.EditRestrictionType
import com.darkzodiak.kontrol.profile.data.local.entity.ProfileEntity
import com.darkzodiak.kontrol.profile.domain.EditRestriction
import com.darkzodiak.kontrol.profile.domain.Profile

object ProfileMapper {
    fun profileEntityToProfile(profile: ProfileEntity): Profile {
        return Profile(
            id = profile.id,
            name = profile.name,
            isEnabled = profile.isEnabled,
            editRestriction = getEditRestrictionFromProfile(profile)
        )
    }
    fun profileToProfileEntity(profile: Profile): ProfileEntity {
        return ProfileEntity(
            id = profile.id,
            name = profile.name,
            isEnabled = profile.isEnabled,
            editRestrictionType = editRestrictionToType(profile.editRestriction),
            editRestrictionValue = editRestrictionToValue(profile.editRestriction)
        )
    }

    private fun ProfileEntity.applyEditRestriction(
        restriction: EditRestriction
    ): ProfileEntity {
        return when(restriction) {
            EditRestriction.NoRestriction -> EditRestrictionType.NO_RESTRICTION
            is EditRestriction.Password -> EditRestrictionType.PASSWORD
            is EditRestriction.RandomText -> EditRestrictionType.RANDOM_TEXT
        }
    }

    private fun editRestrictionToValue(restriction: EditRestriction): String {
        return when(restriction) {
            EditRestriction.NoRestriction -> ""
            is EditRestriction.Password -> restriction.password
            is EditRestriction.RandomText -> restriction.length.toString()
        }
    }

    private fun getEditRestrictionFromProfile(
        profile: ProfileEntity
    ): EditRestriction {
        return when(profile.editRestrictionType) {
            EditRestrictionType.NO_RESTRICTION -> {
                EditRestriction.NoRestriction
            }
            EditRestrictionType.RANDOM_TEXT -> {
                EditRestriction.RandomText(profile.randomTextLength!!)
            }
            EditRestrictionType.PASSWORD -> {
                EditRestriction.Password(profile.password!!)
            }
        }
    }

    private
}