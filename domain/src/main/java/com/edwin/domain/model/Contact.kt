package com.edwin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long = 0,
    var firstName: String = "",
    var lastName: String = "",
    var phoneNumber: String = "",
    var phoneType: PhoneType? = null,
    var ringtone: String = "",
    var notes: String = "",
    var avatarPath: String = ""
) : Parcelable