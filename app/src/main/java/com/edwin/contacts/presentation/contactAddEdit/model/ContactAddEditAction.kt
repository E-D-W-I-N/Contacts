package com.edwin.contacts.presentation.contactAddEdit.model

sealed class ContactAddEditAction {
    data class ShowError(val throwable: Throwable) : ContactAddEditAction()
    data class ValidateForm(
        var isFirstNameBlank: Boolean = false,
        var isLastNameBlank: Boolean = false,
        var isPhoneNumberBlank: Boolean = false,
        var isPhoneTypeNull: Boolean = false,
        var isRingtoneBlank: Boolean = false,
    ) : ContactAddEditAction()
}