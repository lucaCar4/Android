package com.example.foodandart

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodandart.data.repositories.HomeChipsRepositories
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import com.example.foodandart.ui.screens.splash.SplashViewModel
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
val accountService = AccountService()
val Context.dataStore by preferencesDataStore("chips")
val Context.themeStore by preferencesDataStore("museums")

val appModule = module {
    // DataStore
    single { get<Context>().dataStore }
    single { get<Context>().themeStore }
    //Repository
    single { HomeChipsRepositories(get()) }
    // ViewModel
    viewModel{ HomeViewModel(get()) }

    viewModel{ SignInViewModel(accountService) }

    viewModel{ SignUpViewModel(accountService) }

    viewModel{ SplashViewModel(accountService) }

    viewModel{ ProfileViewModel(accountService, get()) }

}