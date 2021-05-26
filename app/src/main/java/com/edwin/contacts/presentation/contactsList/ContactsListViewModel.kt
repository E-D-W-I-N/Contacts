package com.edwin.contacts.presentation.contactsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edwin.contacts.presentation.contactsList.model.ContactsListAction
import com.edwin.contacts.presentation.contactsList.model.ContactsListEvent
import com.edwin.contacts.presentation.contactsList.model.ContactsListViewState
import com.edwin.data.preferences.AppTheme
import com.edwin.data.preferences.PreferencesManager
import com.edwin.domain.model.Contact
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.contactsList.GetContactsUseCase.Params
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

@ExperimentalCoroutinesApi
class ContactsListViewModel(
    private val getContactsUseCase: UseCase<Flow<Result<List<Contact>>>, Params>,
    private val addContactsUseCase: UseCase<List<Long>, List<Contact>>,
    private val preferences: PreferencesManager
) : ViewModel() {
    private val _viewStates = MutableStateFlow(ContactsListViewState())
    val viewStates: StateFlow<ContactsListViewState> = _viewStates.asStateFlow()

    private val _viewActions = Channel<ContactsListAction>()
    val viewActions = _viewActions.receiveAsFlow()

    val searchQuery = MutableStateFlow("")
    lateinit var appTheme: MutableStateFlow<AppTheme>

    init {
        viewModelScope.launch {
            val stateFlowValue = preferences.appTheme.stateIn(viewModelScope).value
            appTheme = MutableStateFlow(stateFlowValue)
        }
        getContacts()
    }

    fun obtainEvent(viewEvent: ContactsListEvent) = viewModelScope.launch {
        when (viewEvent) {
            is ContactsListEvent.GetContacts -> getContacts()
            is ContactsListEvent.ChangeSortOrder -> preferences.updateSortOrder(viewEvent.sortOrder)
            is ContactsListEvent.ChangeSearchQuery -> searchQuery.value = viewEvent.searchQuery
            is ContactsListEvent.AddContacts -> addContacts(viewEvent.contacts)
            is ContactsListEvent.SaveAppTheme -> saveAppTheme(viewEvent.appTheme)
        }
    }

    private fun getContacts() = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        combine(searchQuery, preferences.sortOrder) { query, sortOrder ->
            Pair(query, sortOrder)
        }.flatMapLatest { (query, sortOrder) ->
            getContactsUseCase.run(Params(query, sortOrder))
        }.collect { result ->
            result
                .onSuccess {
                    _viewStates.value = _viewStates.value.copy(isLoading = false, contacts = it)
                }
                .onFailure {
                    _viewStates.value = _viewStates.value.copy(isLoading = false)
                    _viewActions.send(ContactsListAction.ShowError(it))
                }
        }
    }

    private fun addContacts(contacts: List<Contact>) = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        val ids = addContactsUseCase.run(contacts)
        if (ids.isEmpty()) {
            _viewStates.value = _viewStates.value.copy(isLoading = false)
            _viewActions.send(ContactsListAction.ShowError(IOException()))
        }
    }

    private fun saveAppTheme(appTheme: AppTheme) = viewModelScope.launch {
        preferences.updateAppTheme(appTheme)
        this@ContactsListViewModel.appTheme.value = appTheme
    }
}