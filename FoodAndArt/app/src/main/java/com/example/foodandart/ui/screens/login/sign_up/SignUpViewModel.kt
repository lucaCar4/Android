package com.example.foodandart.ui.screens.login.sign_up

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.foodandart.data.firestore.cloud_database.addUserDocument
import com.example.foodandart.data.firestore.storage.updateUserImage
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.InternalCoroutinesApi

class SignUpViewModel( private val accountService: AccountService) : ViewModel() {

    private val LENGTH = 6

    var city by mutableStateOf("")
    var cityGeoPoint by mutableStateOf(GeoPoint(0.0,0.0))
    var existDestination by mutableStateOf(false)

    var image: Uri by mutableStateOf(Uri.EMPTY)

    var name by mutableStateOf("")

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isWrong by  mutableStateOf(false)
    var emailAlreadyUsed by  mutableStateOf(false)
    var passwordLength by  mutableStateOf(false)

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updateName(newName: String) {
        name = newName
    }

    fun updateCity(newCity: String) {
        city = newCity
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    private fun validate(exception : String) {
        Log.d("Login", exception)
        if (exception.contains("email")) {
            emailAlreadyUsed = true
            passwordLength = password.length < LENGTH
        } else if (exception.contains("password")) {
            passwordLength = true
            emailAlreadyUsed = false
        }
    }


    @OptIn(InternalCoroutinesApi::class)
    fun onSignUpClick(navController: NavController) {
        viewModelScope.launch {
            isWrong = password != confirmPassword
            val result = accountService.signUp(email, password)
            Log.d("Storage", "$result, ${!isWrong}, ${existDestination}")
            if (result == "" && !isWrong && existDestination ) {
                updateUserImage(accountService.currentUserId, image)
                addUserDocument(name, cityGeoPoint, city)
                navController.navigate(FoodAndArtRoute.Home.route) {
                    navController.popBackStack()
                }
            } else {
                validate(result)
            }
        }
    }

}