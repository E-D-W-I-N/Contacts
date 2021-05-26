package com.edwin.contacts.presentation.contactsList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.edwin.contacts.R
import com.edwin.contacts.databinding.ContactsListFragmentBinding
import com.edwin.contacts.extensions.showSnackbar
import com.edwin.contacts.presentation.MainActivity
import com.edwin.contacts.presentation.contactsList.extensions.onQueryTextChanged
import com.edwin.contacts.presentation.contactsList.model.ContactsListAction
import com.edwin.contacts.presentation.contactsList.model.ContactsListEvent
import com.edwin.contacts.presentation.contactsList.model.ContactsListViewState
import com.edwin.data.preferences.AppTheme
import com.edwin.domain.model.Contact
import com.edwin.domain.model.NumberType
import com.edwin.domain.model.Ringtone
import com.edwin.domain.model.SortOrder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class ContactsListFragment() : Fragment(R.layout.contacts_list_fragment) {

    private val viewModel: ContactsListViewModel by viewModel()
    private val binding by viewBinding(ContactsListFragmentBinding::bind)
    private lateinit var searchView: SearchView
    private lateinit var toggle: ActionBarDrawerToggle

    private val contactsListAdapter = ContactsListAdapter { contact ->
        showSnackbar("Contact - ${contact.firstName} ${contact.lastName}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        setupNavToggle()

        val viewStates = viewModel.viewStates.flowWithLifecycle(lifecycle)
        viewStates.onEach { bindViewState(it) }.launchIn(lifecycleScope)
        val viewActions = viewModel.viewActions.flowWithLifecycle(lifecycle)
        viewActions.onEach { bindViewAction(it) }.launchIn(lifecycleScope)

        recyclerViewContacts.adapter = contactsListAdapter
        floatingActionButton.setOnClickListener {
            viewModel.obtainEvent(
                ContactsListEvent.AddContacts(
                    listOf(
                        Contact(
                            firstName = "Anya",
                            lastName = "Pepega",
                            phoneNumber = "111",
                            numberType = NumberType.HOME,
                            ringtone = Ringtone.MUSIC1
                        ),
                        Contact(
                            firstName = "Pepega",
                            lastName = "Clap",
                            phoneNumber = "222",
                            numberType = NumberType.HOME,
                            ringtone = Ringtone.MUSIC1
                        ),
                        Contact(
                            firstName = "KEKW",
                            lastName = "ZULUL",
                            phoneNumber = "333",
                            numberType = NumberType.HOME,
                            ringtone = Ringtone.MUSIC1
                        )
                    )
                )
            )
        }

        setHasOptionsMenu(true)
    }

    private fun setupNavToggle() = with(binding) {
        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.theme_switch) {
                showThemeDialog()
            }
            true
        }
    }

    private fun bindViewState(viewState: ContactsListViewState) = with(binding) {
        progressBar.isVisible = viewState.isLoading
        contactsListAdapter.submitList(viewState.contacts)
    }

    private fun bindViewAction(viewAction: ContactsListAction) {
        when (viewAction) {
            is ContactsListAction.ShowError -> showSnackbar("ERROR + ${viewAction.throwable}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_contacts_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.onQueryTextChanged {
            viewModel.obtainEvent(ContactsListEvent.ChangeSearchQuery(it))
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            toggle.onOptionsItemSelected(item) -> true
            item.itemId == R.id.sort_by_first_name -> {
                viewModel.obtainEvent(ContactsListEvent.ChangeSortOrder(SortOrder.BY_FIRST_NAME))
                true
            }
            item.itemId == R.id.sort_by_last_name -> {
                viewModel.obtainEvent(ContactsListEvent.ChangeSortOrder(SortOrder.BY_LAST_NAME))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showThemeDialog() {
        var checkedTheme = viewModel.appTheme.value.ordinal
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.choose_app_theme))
            .setNeutralButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val appTheme = AppTheme.values()[checkedTheme]
                viewModel.obtainEvent(ContactsListEvent.SaveAppTheme(appTheme))
                when (appTheme) {
                    AppTheme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    AppTheme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    AppTheme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                dialog.dismiss()
            }
            .setSingleChoiceItems(AppTheme.valuesAsString(), checkedTheme) { _, which ->
                checkedTheme = which
            }
            .show()
    }

}