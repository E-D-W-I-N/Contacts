package com.edwin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edwin.domain.model.NumberType
import com.edwin.domain.model.Ringtone

@Entity(tableName = "contacts")
data class ContactDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val numberType: NumberType,
    val ringtone: Ringtone
)