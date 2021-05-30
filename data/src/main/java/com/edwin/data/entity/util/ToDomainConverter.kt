package com.edwin.data.entity.util

import com.edwin.data.entity.ContactDTO
import com.edwin.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun ContactDTO.toDomain(): Contact = Contact(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    phoneType = phoneType,
    ringtone = ringtone,
    notes = notes,
    avatarPath = avatarPath
)

fun List<ContactDTO>.toDomainList(): List<Contact> = map { it.toDomain() }

fun Flow<List<ContactDTO>>.toDomainFlowList(): Flow<List<Contact>> = map { it.toDomainList() }

fun Flow<ContactDTO>.toDomainFlow(): Flow<Contact> = map { it.toDomain() }