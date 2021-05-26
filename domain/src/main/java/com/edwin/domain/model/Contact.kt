package com.edwin.domain.model


data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val numberType: NumberType,
    val ringtone: Ringtone
)