package com.edwin.contacts.presentation.contactAddEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.contacts.extensions.valueOf
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditAction
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditEvent
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditViewState
import com.edwin.domain.model.Contact
import com.edwin.domain.model.PhoneType
import com.edwin.domain.usecase.UseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactAddEditViewModel(
    contact: Contact,
    private val addContactUseCase: UseCase<Flow<Result<Unit>>, Contact>,
    private val deleteContactUseCase: UseCase<Flow<Result<Unit>>, Contact>
) : ViewModel() {

    private val _viewStates = MutableStateFlow(ContactAddEditViewState(contact))
    val viewStates: StateFlow<ContactAddEditViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<ContactAddEditAction>()
    val viewActions = _viewActions.receiveAsFlow()

    var viewMode: ViewMode

    init {
        viewMode = if (contact.id != 0L) {
            ViewMode.EDIT
        } else {
            ViewMode.ADD
        }
    }

    fun obtainEvent(viewEvent: ContactAddEditEvent) = viewModelScope.launch {
        when (viewEvent) {
            is ContactAddEditEvent.UpdateFirstName ->
                _viewStates.value.contact.firstName = viewEvent.firstName
            is ContactAddEditEvent.UpdateLastName ->
                _viewStates.value.contact.lastName = viewEvent.lastName
            is ContactAddEditEvent.UpdatePhoneNumber ->
                _viewStates.value.contact.phoneNumber = viewEvent.phoneNumber
            is ContactAddEditEvent.UpdatePhoneType ->
                _viewStates.value.contact.phoneType = valueOf<PhoneType>(viewEvent.phoneType)
            is ContactAddEditEvent.UpdateRingtone ->
                _viewStates.value.contact.ringtone = viewEvent.ringtone
            is ContactAddEditEvent.UpdateNotes ->
                _viewStates.value.contact.notes = viewEvent.notes
            is ContactAddEditEvent.UpdateAvatarPath ->
                _viewStates.value.contact.avatarPath = viewEvent.avatarPath
            is ContactAddEditEvent.InsertContact -> insertContact(viewEvent.callback)
            is ContactAddEditEvent.DeleteContact -> deleteContact(viewEvent.callback)
        }
    }

    private fun insertContact(callback: () -> Unit) = viewModelScope.launch {
        val action = ContactAddEditAction.ValidateForm()
        val contact = _viewStates.value.contact
        if (validateContact(contact, action)) {
            addContactUseCase.run(contact).single()
                .onSuccess { callback() }
                .onFailure { failure -> _viewActions.send(ContactAddEditAction.ShowError(failure)) }
        } else {
            _viewActions.send(action)
        }
    }

    private fun deleteContact(callback: () -> Unit) = viewModelScope.launch {
        deleteContactUseCase.run(_viewStates.value.contact).single()
            .onSuccess { callback() }
            .onFailure { failure -> _viewActions.send(ContactAddEditAction.ShowError(failure)) }
    }

    private fun validateContact(
        contact: Contact,
        action: ContactAddEditAction.ValidateForm
    ) = with(contact) {
        var result = true
        if (firstName.isBlank()) {
            result = false
            action.isFirstNameBlank = true
        }
        if (lastName.isBlank()) {
            result = false
            action.isLastNameBlank = true
        }
        if (phoneNumber.isBlank()) {
            result = false
            action.isPhoneNumberBlank = true
        }
        if (phoneType == null) {
            result = false
            action.isPhoneTypeNull = true
        }
        if (ringtone.isBlank()) {
            result = false
            action.isRingtoneBlank = true
        }
        result
    }

    enum class ViewMode { ADD, EDIT }
}