package com.edwin.data.repository

import com.edwin.data.database.ContactDao
import com.edwin.data.entity.util.toDTO
import com.edwin.data.entity.util.toDomainFlow
import com.edwin.data.entity.util.toDomainFlowList
import com.edwin.domain.ContactRepository
import com.edwin.domain.exceptions.ContactException
import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImpl(private val contactDao: ContactDao) : ContactRepository {

    override fun getContacts(
        query: String,
        sortOrder: SortOrder
    ): Flow<List<Contact>> = try {
        contactDao.getContacts(query, sortOrder).toDomainFlowList()
    } catch (e: Exception) {
        throw ContactException.DatabaseError
    }

    override fun getContactById(id: Long): Flow<Contact> = try {
        contactDao.getContactById(id).toDomainFlow()
    } catch (e: Exception) {
        throw ContactException.DatabaseError
    }

    override suspend fun insertContact(contact: Contact) {
        try {
            contactDao.insertContact(contact.toDTO())
        } catch (e: Exception) {
            throw ContactException.DatabaseError
        }
    }

    override suspend fun deleteContact(contact: Contact) {
        try {
            contactDao.deleteContact(contact.toDTO())
        } catch (e: Exception) {
            throw ContactException.DatabaseError
        }
    }

}