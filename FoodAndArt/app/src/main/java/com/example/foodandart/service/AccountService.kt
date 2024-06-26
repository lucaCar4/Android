package com.example.foodandart.service

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class User(
    val id: String = ""
)

class AccountService {
    val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    suspend fun signIn(email: String, password: String) : String {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            ""
        } catch (e: Exception) {
            e.message ?: ""
        }
    }

    suspend fun signUp(email: String, password: String) : String {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            ""
        } catch (e: Exception) {
            e.message ?: ""
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

    suspend fun deleteAccount(password: String) {
        Firebase.auth.signInWithEmailAndPassword(Firebase.auth.currentUser?.email.toString(), password).await()
        Firebase.auth.currentUser!!.delete().await()
    }
    suspend fun signInDelete(password: String): String {
        return try {
            Firebase.auth.signInWithEmailAndPassword(Firebase.auth.currentUser?.email.toString(), password).await()
            ""
        } catch (e: Exception) {
            e.message ?: ""
        }
    }

    fun resetPass() {
        Firebase.auth.sendPasswordResetEmail(Firebase.auth.currentUser?.email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }
}