package com.edwin.data.entity.util

import com.edwin.data.entity.ContactDTO
import com.edwin.domain.model.Contact
import com.edwin.domain.model.PhoneType

fun Contact.toDTO(): ContactDTO = ContactDTO(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    phoneType = phoneType ?: PhoneType.HOME,
    ringtone = ringtone,
    notes = notes,
    avatarPath = avatarPath
)