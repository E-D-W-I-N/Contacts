package com.edwin.domain.exceptions

sealed class ContactException : Exception() {

    object DatabaseError : ContactException()
}