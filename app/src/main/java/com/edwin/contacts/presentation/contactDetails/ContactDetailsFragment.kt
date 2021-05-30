package com.edwin.contacts.presentation.contactDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.edwin.contacts.R
import com.edwin.contacts.databinding.ContactDetailsFragmentBinding
import com.edwin.contacts.extensions.showSnackbar
import com.edwin.contacts.presentation.GlideImageLoader
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsAction
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsEvent
import com.edwin.contacts.presentation.contactDetails.model.ContactDetailsViewState
import com.edwin.domain.exceptions.ContactException
import com.edwin.domain.model.Contact
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ContactDetailsFragment : Fragment(R.layout.contact_details_fragment) {

    private val args: ContactDetailsFragmentArgs by navArgs()
    private val viewModel: ContactDetailsViewModel by viewModel { parametersOf(args.contactId) }
    private val binding by viewBinding(ContactDetailsFragmentBinding::bind)
    private lateinit var contact: Contact

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewStates = viewModel.viewStates.flowWithLifecycle(lifecycle)
        viewStates.onEach { bindViewState(it) }.launchIn(lifecycleScope)
        val viewActions = viewModel.viewActions.flowWithLifecycle(lifecycle)
        viewActions.onEach { bindViewAction(it) }.launchIn(lifecycleScope)
        handleButtonClicks()
        viewModel.obtainEvent(ContactDetailsEvent.FetchContact)
        setHasOptionsMenu(true)
    }

    private fun bindViewState(viewState: ContactDetailsViewState) = with(binding) {
        progressBar.isVisible = viewState.isLoading
        contact = viewState.contact
        contactName.text = getString(R.string.contact_name, contact.firstName, contact.lastName)
        phoneNumber.text = contact.phoneNumber
        phoneType.text = contact.phoneType?.name
        ringtone.text = contact.ringtone
        notes.text = contact.notes
        GlideImageLoader().loadImage(requireContext(), contactImage, Uri.parse(contact.avatarPath))
    }

    private fun bindViewAction(action: ContactDetailsAction) {
        when (action) {
            is ContactDetailsAction.ShowError -> {
                if (action.throwable is ContactException.DatabaseError) {
                    showSnackbar(getString(R.string.database_error))
                }
            }
        }
    }

    private fun handleButtonClicks() = with(binding) {
        floatingActionButton.setOnClickListener {
            val action =
                ContactDetailsFragmentDirections.actionDetailsFragmentToAddEditFragment(contact)
            findNavController().navigate(action)
        }

        iconPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact.phoneNumber}")
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_contact_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                showSnackbar(getString(R.string.coming_soon))
                true
            }
            R.id.action_more -> {
                showSnackbar(getString(R.string.coming_soon))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}