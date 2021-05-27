package com.edwin.data.entity.util

import com.edwin.data.entity.ContactDTO
import com.edwin.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun ContactDTO.toDomain(): Contact = Contact(
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    numberType = numberType,
    ringtone = ringtone
)

fun List<ContactDTO>.toDomainList(): List<Contact> = map { it.toDomain() }

fun Flow<List<ContactDTO>>.toDomainFlow(): Flow<List<Contact>> = map { it.toDomainList() }