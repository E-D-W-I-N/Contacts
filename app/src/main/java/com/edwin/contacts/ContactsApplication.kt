package com.edwin.contacts

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.edwin.contacts.di.AppModule
import com.edwin.data.preferences.AppTheme
import com.edwin.data.preferences.PreferencesManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ContactsApplication : Application() {

    private val preferencesManager: PreferencesManager by inject()

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        MainScope().launch {
            when (preferencesManager.appTheme.first()) {
                AppTheme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                AppTheme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                AppTheme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        startKoin {
            androidContext(this@ContactsApplication)
            modules(
                AppModule.dataModule,
                AppModule.useCaseModule,
                AppModule.viewModelModule
            )
        }
    }
}