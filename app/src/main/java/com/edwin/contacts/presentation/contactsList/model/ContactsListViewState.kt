package com.edwin.contacts.presentation.contactsList.model

import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder

data class ContactsListViewState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val sortOrder: SortOrder? = null
)
