package com.example.foodandart.data.firestore.storage

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@Composable
fun getURIFromPath(path: String) : Uri {
    val storage = Firebase.storage
    val storageRef = storage.reference
    val spaceRef = storageRef.child(path)
    var uriRef by remember { mutableStateOf(Uri.EMPTY) }
    spaceRef.downloadUrl.addOnSuccessListener { uri ->
        uriRef = uri
    }.addOnFailureListener { exception ->
    }
    return uriRef
}