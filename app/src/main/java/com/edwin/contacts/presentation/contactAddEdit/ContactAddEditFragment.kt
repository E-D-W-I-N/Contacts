package com.edwin.contacts.presentation.contactAddEdit

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.edwin.contacts.R
import com.edwin.contacts.databinding.ContactAddEditFragmentBinding
import com.edwin.contacts.extensions.getFileName
import com.edwin.contacts.extensions.makeDialog
import com.edwin.contacts.extensions.showSnackbar
import com.edwin.contacts.presentation.GlideImageLoader
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditAction
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditEvent
import com.edwin.contacts.presentation.contactAddEdit.model.ContactAddEditViewState
import com.edwin.domain.exceptions.ContactException
import com.edwin.domain.model.Contact
import com.edwin.domain.model.PhoneType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import lv.chi.photopicker.PhotoPickerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ContactAddEditFragment : Fragment(), PhotoPickerFragment.Callback {

    private val args: ContactAddEditFragmentArgs by navArgs()
    private val viewModel: ContactAddEditViewModel by viewModel { parametersOf(args.contact) }
    private var _binding: ContactAddEditFragmentBinding? = null
    private val binding get() = _binding!!
    private val glideImageLoader = GlideImageLoader()

    private val getRingtone = registerForActivityResult(RingtonePicker()) { uri: Uri? ->
        uri?.let {
            val fileName = context?.getFileName(it).toString()
            viewModel.obtainEvent(ContactAddEditEvent.UpdateRingtone(fileName))
            binding.apply {
                ringtoneLayout.error = null
                ringtoneEditText.setText(fileName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContactAddEditFragmentBinding.inflate(inflater, container, false)
        val phoneAdapter = ArrayAdapter(requireContext(), R.layout.list_item, PhoneType.values())
        binding.phoneTypeEditText.setAdapter(phoneAdapter)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            val title = when (viewModel.viewMode) {
                ContactAddEditViewModel.ViewMode.ADD -> getString(R.string.add_contact)
                ContactAddEditViewModel.ViewMode.EDIT -> getString(R.string.edit_contact)
            }
            setTitle(title)
            setHomeAsUpIndicator(R.drawable.ic_done)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        val viewStates = viewModel.viewStates.flowWithLifecycle(lifecycle)
        viewStates.onEach { bindViewState(it) }.launchIn(lifecycleScope)
        val viewActions = viewModel.viewActions.flowWithLifecycle(lifecycle)
        viewActions.onEach { bindViewAction(it) }.launchIn(lifecycleScope)
        handleForm()
        handleBackButton()
        setHasOptionsMenu(true)
    }

    private fun bindViewState(viewState: ContactAddEditViewState) {
        fillForm(viewState.contact)
    }

    private fun bindViewAction(action: ContactAddEditAction) {
        when (action) {
            is ContactAddEditAction.ShowError -> {
                if (action.throwable is ContactException.DatabaseError) {
                    showSnackbar(getString(R.string.database_error))
                }
            }
            is ContactAddEditAction.ValidateForm -> validateForm(action)
        }
    }

    private fun validateForm(action: ContactAddEditAction.ValidateForm) = with(binding) {
        if (action.isFirstNameBlank) {
            firstNameLayout.error = getString(R.string.field_is_blank_or_empty)
        }
        if (action.isLastNameBlank) {
            lastNameLayout.error = getString(R.string.field_is_blank_or_empty)
        }
        if (action.isPhoneNumberBlank) {
            phoneNumberLayout.error = getString(R.string.field_is_blank_or_empty)
        }
        if (action.isPhoneTypeNull) {
            phoneTypeLayout.error = getString(R.string.field_is_blank_or_empty)
        }
        if (action.isRingtoneBlank) {
            ringtoneLayout.error = getString(R.string.field_is_blank_or_empty)
        }
    }

    private fun fillForm(contact: Contact) = with(binding) {
        contact.apply {
            firstNameEditText.setText(firstName)
            lastNameEditText.setText(lastName)
            phoneNumberEditText.setText(phoneNumber)
            phoneTypeEditText.setText(phoneType?.name, false)
            ringtoneEditText.setText(ringtone)
            notesEditText.setText(notes)
            glideImageLoader.loadImage(requireContext(), avatarView, Uri.parse(avatarPath))
        }
    }

    private fun handleForm() = with(binding) {
        avatarView.setOnClickListener { openPicker() }
        changeAvatarButton.setOnClickListener { openPicker() }
        firstNameEditText.doAfterTextChanged {
            firstNameLayout.error = null
            viewModel.obtainEvent(ContactAddEditEvent.UpdateFirstName(it.toString()))
        }
        lastNameEditText.doAfterTextChanged {
            lastNameLayout.error = null
            viewModel.obtainEvent(ContactAddEditEvent.UpdateLastName(it.toString()))
        }
        phoneNumberEditText.doAfterTextChanged {
            phoneNumberLayout.error = null
            viewModel.obtainEvent(ContactAddEditEvent.UpdatePhoneNumber(it.toString()))
        }
        phoneTypeEditText.doAfterTextChanged {
            phoneTypeLayout.error = null
            viewModel.obtainEvent(ContactAddEditEvent.UpdatePhoneType(it.toString()))
        }
        ringtoneEditText.setOnClickListener {
            getRingtone.launch(RingtoneManager.TYPE_RINGTONE)
        }
        notesEditText.doAfterTextChanged {
            viewModel.obtainEvent(ContactAddEditEvent.UpdateNotes(it.toString()))
        }
    }

    private fun handleBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            makeDialog(
                title = R.string.are_you_sure,
                message = R.string.unsaved_data,
                positiveCallback = { findNavController().popBackStack() }
            ).show()
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        val avatarPath = photos.first()
        viewModel.obtainEvent(ContactAddEditEvent.UpdateAvatarPath(avatarPath.toString()))
        glideImageLoader.loadImage(requireContext(), binding.avatarView, avatarPath)
    }

    private fun openPicker() {
        val theme =
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                R.style.ChiliPhotoPicker_Light
            } else {
                R.style.ChiliPhotoPicker_Dark
            }

        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            maxSelection = 1,
            theme = theme
        ).show(childFragmentManager, "picker")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_contact_add_edit, menu)
        val deleteVisibility = viewModel.viewMode == ContactAddEditViewModel.ViewMode.EDIT
        menu.findItem(R.id.action_delete).isVisible = deleteVisibility
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.obtainEvent(ContactAddEditEvent.InsertContact {
                    findNavController().popBackStack()
                })
                true
            }
            R.id.action_delete -> {
                makeDialog(
                    title = R.string.delete_contact,
                    message = R.string.delete_contact_message,
                    positiveCallback = {
                        viewModel.obtainEvent(ContactAddEditEvent.DeleteContact {
                            val action = ContactAddEditFragmentDirections
                                .actionAddEditFragmentToListFragment()
                            findNavController().navigate(action)
                        })
                    }
                ).show()
                true
            }
            R.id.action_more -> {
                showSnackbar(getString(R.string.coming_soon))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}