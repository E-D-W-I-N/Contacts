package com.edwin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.edwin.data.entity.ContactDTO

@Database(entities = [ContactDTO::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}