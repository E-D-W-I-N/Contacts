package com.edwin.domain.usecase.contactsList

import com.edwin.domain.ContactRepository
import com.edwin.domain.model.Contact
import com.edwin.domain.usecase.UseCase

class AddContactsUseCase(private val contactRepository: ContactRepository) :
    UseCase<List<Long>, List<Contact>> {

    override suspend fun run(params: List<Contact>): List<Long> =
        contactRepository.insertContacts(params)
}