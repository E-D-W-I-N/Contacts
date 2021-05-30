package com.edwin.domain.usecase.contactsList

import com.edwin.domain.ContactRepository
import com.edwin.domain.model.Contact
import com.edwin.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddContactUseCase(private val contactRepository: ContactRepository) :
    UseCase<Flow<Result<Unit>>, Contact> {

    override suspend fun run(params: Contact): Flow<Result<Unit>> = flow {
        val result = try {
            Result.success(contactRepository.insertContact(params))
        } catch (e: Throwable) {
            Result.failure(e)
        }
        emit(result)
    }.flowOn(Dispatchers.IO)

}