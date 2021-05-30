package com.edwin.contacts.extensions

inline fun <reified T : Enum<T>> valueOf(type: String): T? = try {
    java.lang.Enum.valueOf(T::class.java, type)
} catch (e: IllegalArgumentException) {
    null
}