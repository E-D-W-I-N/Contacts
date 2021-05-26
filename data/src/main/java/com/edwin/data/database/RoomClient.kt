package com.edwin.data.database

import android.app.Application
import androidx.room.Room

class RoomClient(application: Application) {

    val contactDao: ContactDao by lazy {
        Room.databaseBuilder(application, ContactDatabase::class.java, "ContactDatabase")
            .build().contactDao()
    }

}