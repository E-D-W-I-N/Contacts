package com.edwin.domain.usecase

interface UseCase<out Type, in Params> {

    suspend fun run(params: Params): Type
}