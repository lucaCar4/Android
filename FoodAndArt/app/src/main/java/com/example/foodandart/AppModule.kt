package com.example.foodandart

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.foodandart.data.database.BasketDatabase
import com.example.foodandart.data.remote.OSMDataSource
import com.example.foodandart.data.repositories.BasketRepository
import com.example.foodandart.data.repositories.HomeChipsRepository
import com.example.foodandart.data.repositories.ThemeRepository
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.screens.cardDetails.CardDetailsViewModel
import com.example.foodandart.ui.screens.charts.ChartsViewModel
import com.example.foodandart.ui.screens.favorites.FavoritesViewModel
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import com.example.foodandart.ui.screens.splash.SplashViewModel
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import com.example.foodandart.ui.screens.purchases.PurchasesViewModel
import com.example.foodandart.ui.screens.shopping_cart.BasketViewModel
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
    single {
        Room.databaseBuilder(
            get(),
            BasketDatabase::class.java,
            "basket"
        ).build()
    }
    single { BasketRepository(get<BasketDatabase>().basketDAO()) }
    // DataStore
    single { get<Context>().dataStore }
    single { get<Context>().themeStore }
    //Repository
    single { HomeChipsRepository(get()) }

    single { ThemeRepository(get()) }

    viewModel{ HomeViewModel(get()) }

    viewModel{ FavoritesViewModel() }

    viewModel{ SignInViewModel(accountService) }

    viewModel{ SignUpViewModel(accountService) }

    viewModel{ SplashViewModel(accountService) }

    viewModel{ ProfileViewModel(accountService, get()) }

    viewModel { CardDetailsViewModel(get()) }

    viewModel { BasketViewModel(get()) }

    viewModel { PurchasesViewModel() }

    viewModel { ChartsViewModel() }

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