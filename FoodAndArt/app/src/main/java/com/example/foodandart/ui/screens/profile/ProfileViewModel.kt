package com.example.foodandart.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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

    init {
        viewModelScope.launch {
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
}