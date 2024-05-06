package com.example.foodandart.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.foodandart.data.firestore.cloud_database.userInfo
import com.example.foodandart.data.firestore.storage.getUserImage
import com.example.foodandart.data.models.Theme
import com.example.foodandart.data.repositories.ThemeRepository
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountService: AccountService,
    private val repository: ThemeRepository
) : ViewModel() {

    var state by mutableStateOf(Theme.System)

    var name by mutableStateOf("")
    var city by mutableStateOf("")

    var imageUri: Uri by mutableStateOf(Uri.EMPTY)
    init {
        viewModelScope.launch {
            getUserInfo()
            state = repository.theme.first()
        }
    }

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        state = theme
        repository.setTheme(theme)
    }

    fun initialize(navController: NavController) {
        viewModelScope.launch {
            accountService.currentUser.collect { user ->
                if (user == null) {
                    navController.navigate(FoodAndArtRoute.Splash.route) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    fun onDeleteAccountClick() {
        viewModelScope.launch {
            accountService.deleteAccount()
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            val document = userInfo()
            if (document != null && document.data != null) {
                    for (data in document.data!!) {
                        when(data.key.toString()) {
                            "name" -> name = data.value.toString()
                            "city" -> city = data.value.toString()
                        }
                    }
            }
            imageUri = getUserImage() ?: Uri.EMPTY
            Log.d("Uri", imageUri.path.toString())
        }
    }

}