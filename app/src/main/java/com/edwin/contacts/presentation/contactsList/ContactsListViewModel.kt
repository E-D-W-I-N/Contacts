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

@ExperimentalCoroutinesApi
class ContactsListViewModel(
    private val getContactsUseCase: UseCase<Flow<Result<List<Contact>>>, Params>,
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
            is ContactsListEvent.ChangeSortOrder -> preferences.updateSortOrder(viewEvent.sortOrder)
            is ContactsListEvent.ChangeSearchQuery -> searchQuery.value = viewEvent.searchQuery
            is ContactsListEvent.SaveAppTheme -> saveAppTheme(viewEvent.appTheme)
        }
    }

    private fun getContacts() = viewModelScope.launch {
        _viewStates.value = _viewStates.value.copy(isLoading = true)
        combine(searchQuery, preferences.sortOrder) { query, sortOrder ->
            Pair(query, sortOrder)
        }.flatMapLatest { (query, sortOrder) ->
            _viewStates.value = _viewStates.value.copy(sortOrder = sortOrder)
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

    private fun saveAppTheme(appTheme: AppTheme) = viewModelScope.launch {
        preferences.updateAppTheme(appTheme)
        this@ContactsListViewModel.appTheme.value = appTheme
    }
}