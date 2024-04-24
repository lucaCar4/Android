package com.example.foodandart

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FoodAndArtApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin() {
            androidLogger()
            androidContext(this@FoodAndArtApplication)
            modules(appModule)
        }
    }
}