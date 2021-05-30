package com.edwin.data.database

import androidx.room.*
import com.edwin.data.entity.ContactDTO
import com.edwin.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    fun getContacts(query: String, sortOrder: SortOrder): Flow<List<ContactDTO>> =
        when (sortOrder) {
            SortOrder.BY_FIRST_NAME -> getContactsByFirstName("%$query%")
            SortOrder.BY_LAST_NAME -> getContactsByLastName("%$query%")
        }

    @Query("SELECT * FROM contacts WHERE firstName LIKE :searchQuery OR lastName LIKE :searchQuery ORDER BY firstName")
    fun getContactsByFirstName(searchQuery: String): Flow<List<ContactDTO>>

    @Query("SELECT * FROM contacts WHERE firstName LIKE :searchQuery OR lastName LIKE :searchQuery ORDER BY lastName")
    fun getContactsByLastName(searchQuery: String): Flow<List<ContactDTO>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Long): Flow<ContactDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactDTO)

    @Delete
    suspend fun deleteContact(contact: ContactDTO)

}