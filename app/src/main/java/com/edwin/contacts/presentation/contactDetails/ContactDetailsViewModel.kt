package com.edwin.contacts.presentation.contactDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsAction
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsEvent
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsViewState
import com.edwin.domain.model.Contact
import com.edwin.domain.usecase.UseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactDetailsViewModel(
    private val contactId: Long,
    private val getContactByIdUseCase: UseCase<Flow<Result<Contact>>, Long>
) : ViewModel() {

    private val _viewStates = MutableStateFlow(ContactDetailsViewState(Contact()))
    val viewStates: StateFlow<ContactDetailsViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<ContactDetailsAction>()
    val viewActions = _viewActions.receiveAsFlow()

    fun obtainEvent(viewEvent: ContactDetailsEvent) {
        when (viewEvent) {
            is ContactDetailsEvent.FetchContact -> fetchContact()
        }
    }

    private fun fetchContact() = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        getContactByIdUseCase.run(contactId).collect { result ->
            result
                .onSuccess {
                    _viewStates.value = _viewStates.value.copy(isLoading = false, contact = it)
                }
                .onFailure {
                    _viewStates.value = _viewStates.value.copy(isLoading = false)
                    _viewActions.send(ContactDetailsAction.ShowError(it))
                }
        }

    }
}