package com.edwin.data.repository

import com.edwin.data.database.ContactDao
import com.edwin.data.entity.util.toDTOList
import com.edwin.data.entity.util.toDomainFlow
import com.edwin.domain.ContactRepository
import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImpl(private val contactDao: ContactDao) : ContactRepository {

    override fun getContacts(
        query: String,
        sortOrder: SortOrder
    ): Flow<List<Contact>> = try {
        contactDao.getContacts(query, sortOrder).toDomainFlow()
    } catch (e: Exception) {
        throw e
    }

    override fun insertContact(contact: Contact): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertContacts(contacts: List<Contact>): List<Long> =
        contactDao.insertContacts(contacts.toDTOList())

    override fun deleteContact(contact: Contact): Int {
        TODO("Not yet implemented")
    }

    override fun deleteContacts(contacts: List<Contact>): Int {
        TODO("Not yet implemented")
    }

}