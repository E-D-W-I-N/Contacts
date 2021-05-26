package com.edwin.contacts.presentation.contactsList.model

import com.edwin.data.preferences.AppTheme
import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder

sealed class ContactsListEvent {
    object GetContacts : ContactsListEvent()
    data class ChangeSortOrder(val sortOrder: SortOrder) : ContactsListEvent()
    data class ChangeSearchQuery(val searchQuery: String) : ContactsListEvent()
    data class AddContacts(val contacts: List<Contact>) : ContactsListEvent()
    data class SaveAppTheme(val appTheme: AppTheme) : ContactsListEvent()
}