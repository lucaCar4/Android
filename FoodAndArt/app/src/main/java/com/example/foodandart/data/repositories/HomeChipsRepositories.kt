package com.example.foodandart.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map


class HomeChipsRepositories (private val dataStore : DataStore<Preferences>) {
    companion object {
        private val RESTAURANTS_KEY = stringPreferencesKey("restaurants")
        private val MUSEUMS_KEY = stringPreferencesKey("museums")
        private val PACKAGES_KEY = stringPreferencesKey("packages")
        private val POSITION_KEY = stringPreferencesKey("position")
    }

    val restaurants = dataStore.data.map { it[RESTAURANTS_KEY] ?: "" }

    val museums = dataStore.data.map { it[MUSEUMS_KEY] ?: "" }
    val packages = dataStore.data.map { it[PACKAGES_KEY] ?: "" }
    val position = dataStore.data.map { it[POSITION_KEY] ?: "" }

    suspend fun setChip(value : String, name: String) {
        Log.d("Chips", "$name, $value")
        when(name.lowercase()) {
            "restaurants" ->  { dataStore.edit { it[RESTAURANTS_KEY] = value }}
            "museums" -> { dataStore.edit { it[MUSEUMS_KEY] = value }}
            "packages" -> { dataStore.edit { it[PACKAGES_KEY] = value }}
            "position" -> { dataStore.edit { it[POSITION_KEY] = value }}
        }
    }
}