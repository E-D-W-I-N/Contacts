package com.edwin.contacts.presentation.contactsList.model

sealed class ContactsListAction {
    data class ShowError(val throwable: Throwable) : ContactsListAction()
}