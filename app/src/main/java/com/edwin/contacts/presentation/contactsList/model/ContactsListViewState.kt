package com.edwin.contacts.presentation.contactsList.model

import com.edwin.domain.model.Contact

data class ContactsListViewState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList()
)
