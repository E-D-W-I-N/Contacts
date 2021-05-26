package com.edwin.domain

import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun getContacts(query: String, sortOrder: SortOrder): Flow<List<Contact>>

    fun insertContact(contact: Contact): Long

    suspend fun insertContacts(contacts: List<Contact>): List<Long>

    fun deleteContact(contact: Contact): Int

    fun deleteContacts(contacts: List<Contact>): Int
}