package com.edwin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edwin.domain.model.PhoneType

@Entity(tableName = "contacts")
data class ContactDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val phoneType: PhoneType,
    val ringtone: String,
    val notes: String,
    val avatarPath: String
)