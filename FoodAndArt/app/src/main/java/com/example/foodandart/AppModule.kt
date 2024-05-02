package com.example.foodandart

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodandart.data.remote.OSMDataSource
import com.example.foodandart.data.repositories.HomeChipsRepositories
import com.example.foodandart.data.repositories.ThemeRepository
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.screens.CardDetails.CardDetailsViewModel
import com.example.foodandart.ui.screens.favorites.FavoritesViewModel
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import com.example.foodandart.ui.screens.splash.SplashViewModel
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
val accountService = AccountService()
val Context.dataStore by preferencesDataStore("chips")
val Context.themeStore by preferencesDataStore("themes")

val appModule = module {
    // DataStore
    single { get<Context>().dataStore }
    single { get<Context>().themeStore }
    //Repository
    single { HomeChipsRepositories(get()) }
    single { ThemeRepository(get()) }
    // ViewModel
    viewModel{ HomeViewModel(get()) }

    viewModel{ FavoritesViewModel() }

    viewModel{ SignInViewModel(accountService) }

    viewModel{ SignUpViewModel(accountService) }

    viewModel{ SplashViewModel(accountService) }

    viewModel{ ProfileViewModel(accountService, get()) }

    viewModel { CardDetailsViewModel() }

    /*HTTP REQUESTS*/
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    single { OSMDataSource(get()) }

}