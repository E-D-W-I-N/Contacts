package com.edwin.domain

import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun getContacts(query: String, sortOrder: SortOrder): Flow<List<Contact>>

    fun getContactById(id: Long): Flow<Contact>

    suspend fun insertContact(contact: Contact)

    suspend fun deleteContact(contact: Contact)
}