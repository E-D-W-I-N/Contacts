package com.edwin.contacts.presentation.contactDetails.model

sealed class ContactDetailsEvent {
    object DeleteContact : ContactDetailsEvent()
}