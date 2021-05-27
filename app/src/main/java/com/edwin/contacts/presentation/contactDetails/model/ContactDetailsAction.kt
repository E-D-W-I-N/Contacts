package com.edwin.contacts.presentation.contactDetails.model

sealed class ContactDetailsAction {
    data class ShowError(val throwable: Throwable) : ContactDetailsAction()
}