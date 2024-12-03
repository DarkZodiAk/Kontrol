package com.darkzodiak.kontrol.data.mappers

import com.darkzodiak.kontrol.data.local.EditRestrictionType
import com.darkzodiak.kontrol.data.local.entity.ProfileEntity
import com.darkzodiak.kontrol.domain.model.EditRestriction
import com.darkzodiak.kontrol.domain.model.Profile

object ProfileMapper {
    fun profileEntityToProfile(profile: ProfileEntity): Profile {
        return Profile(
            id = profile.id,
            name = profile.name,
            isEnabled = profile.isEnabled,
            editRestriction = getEditRestrictionFromData(
                type = profile.editRestrictionType,
                value = profile.editRestrictionValue
            )
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

    private fun editRestrictionToType(restriction: EditRestriction): EditRestrictionType {
        return when(restriction) {
            EditRestriction.NoRestriction -> EditRestrictionType.NO_RESTRICTION
            is EditRestriction.Password -> EditRestrictionType.PASSWORD
            is EditRestriction.RandomPassword -> EditRestrictionType.RANDOM_PASSWORD
        }
    }

    private fun editRestrictionToValue(restriction: EditRestriction): String {
        return when(restriction) {
            EditRestriction.NoRestriction -> ""
            is EditRestriction.Password -> restriction.password
            is EditRestriction.RandomPassword -> restriction.length.toString()
        }
    }

    private fun getEditRestrictionFromData(
        type: EditRestrictionType,
        value: String
    ): EditRestriction {
        return when(type) {
            EditRestrictionType.NO_RESTRICTION -> {
                EditRestriction.NoRestriction
            }
            EditRestrictionType.RANDOM_PASSWORD -> {
                EditRestriction.RandomPassword(value.toInt())
            }
            EditRestrictionType.PASSWORD -> {
                EditRestriction.Password(value)
            }
        }
    }
}