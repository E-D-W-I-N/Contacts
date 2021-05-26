package com.edwin.domain.usecase.contactsList

import com.edwin.domain.ContactRepository
import com.edwin.domain.model.Contact
import com.edwin.domain.model.SortOrder
import com.edwin.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetContactsUseCase(private val contactRepository: ContactRepository) :
    UseCase<Flow<Result<List<Contact>>>, GetContactsUseCase.Params> {

    override suspend fun run(params: Params): Flow<Result<List<Contact>>> =
        contactRepository.getContacts(params.query, params.sortOrder).map {
            Result.success(it)
        }.catch { e ->
            Result.failure<Exception>(e)
        }

    data class Params(
        val query: String,
        val sortOrder: SortOrder
    )
}
