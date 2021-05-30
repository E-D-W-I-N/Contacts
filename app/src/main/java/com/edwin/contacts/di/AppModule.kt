package com.edwin.contacts.di

import com.edwin.contacts.di.util.Constants
import com.edwin.contacts.presentation.contactAddEdit.ContactAddEditViewModel
import com.edwin.contacts.presentation.contactDetails.ContactDetailsViewModel
import com.edwin.contacts.presentation.contactsList.ContactsListViewModel
import com.edwin.data.database.RoomClient
import com.edwin.data.preferences.PreferencesManager
import com.edwin.data.repository.ContactRepositoryImpl
import com.edwin.domain.ContactRepository
import com.edwin.domain.model.Contact
import com.edwin.domain.usecase.UseCase
import com.edwin.domain.usecase.contactsList.AddContactUseCase
import com.edwin.domain.usecase.contactsList.DeleteContactUseCase
import com.edwin.domain.usecase.contactsList.GetContactByIdUseCase
import com.edwin.domain.usecase.contactsList.GetContactsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModule {

    val dataModule = module {

        // WeatherRepository
        single<ContactRepository> {
            ContactRepositoryImpl(
                RoomClient(androidApplication()).contactDao
            )
        }

        single { PreferencesManager(androidContext()) }
    }

    val useCaseModule = module {
        single<UseCase<Flow<Result<List<Contact>>>, GetContactsUseCase.Params>>(
            named(Constants.getContacts)
        ) { GetContactsUseCase(get()) }

        single<UseCase<Flow<Result<Contact>>, Long>>(
            named(Constants.getContactById)
        ) { GetContactByIdUseCase(get()) }

        single<UseCase<Flow<Result<Unit>>, Contact>>(
            named(Constants.addContact)
        ) { AddContactUseCase(get()) }

        single<UseCase<Flow<Result<Unit>>, Contact>>(
            named(Constants.deleteContact)
        ) { DeleteContactUseCase(get()) }
    }

    @ExperimentalCoroutinesApi
    val viewModelModule = module {
        viewModel { ContactsListViewModel(get(named(Constants.getContacts)), get()) }
        viewModel { (contactId: Long) ->
            ContactDetailsViewModel(
                contactId,
                get(named(Constants.getContactById))
            )
        }
        viewModel { (contact: Contact) ->
            ContactAddEditViewModel(
                contact,
                get(named(Constants.addContact)),
                get(named(Constants.deleteContact))
            )
        }
    }

}