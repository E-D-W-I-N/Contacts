package com.edwin.data.entity.util

import com.edwin.data.entity.ContactDTO
import com.edwin.domain.model.Contact

fun Contact.toDTO(): ContactDTO = ContactDTO(
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    numberType = numberType,
    ringtone = ringtone
)

fun List<Contact>.toDTOList(): List<ContactDTO> = map { it.toDTO() }