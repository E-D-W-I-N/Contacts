package com.edwin.contacts.presentation.contactAddEdit.model

sealed class ContactAddEditEvent {
    data class UpdateFirstName(val firstName: String) : ContactAddEditEvent()
    data class UpdateLastName(val lastName: String) : ContactAddEditEvent()
    data class UpdatePhoneNumber(val phoneNumber: String) : ContactAddEditEvent()
    data class UpdatePhoneType(val phoneType: String) : ContactAddEditEvent()
    data class UpdateRingtone(val ringtone: String) : ContactAddEditEvent()
    data class UpdateNotes(val notes: String) : ContactAddEditEvent()
    data class UpdateAvatarPath(val avatarPath: String) : ContactAddEditEvent()
    data class InsertContact(val callback: () -> Unit) : ContactAddEditEvent()
    data class DeleteContact(val callback: () -> Unit) : ContactAddEditEvent()
}