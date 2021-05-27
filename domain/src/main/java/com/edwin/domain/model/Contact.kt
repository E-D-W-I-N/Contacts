package com.edwin.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val numberType: NumberType,
    val ringtone: Ringtone
) : Parcelable