package com.edwin.contacts.presentation.contactDetails

import androidx.lifecycle.ViewModel
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsAction
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsViewState
import com.edwin.domain.model.Contact
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ContactDetailsViewModel(
    contact: Contact
) : ViewModel() {

    private val _viewStates = MutableStateFlow(ContactDetailsViewState(contact = contact))
    val viewStates: StateFlow<ContactDetailsViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<ContactDetailsAction>()
    val viewActions = _viewActions.receiveAsFlow()
}