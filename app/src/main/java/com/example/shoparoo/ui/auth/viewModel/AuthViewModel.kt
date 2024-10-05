package com.example.shoparoo.ui.auth.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private var _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Loading)
    val authState: MutableStateFlow<AuthState> = _authState
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    init {
        checkUser()
    }

    private fun checkUser() {
        if (firebaseAuth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        }
    }


    fun signUp(email: String, pass: String, name: String) {
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "signUp: success")
                    val user = firebaseAuth.currentUser

                    saveUserDataFireBase(user!!.uid, name)

                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email sent.")
                                _authState.value = AuthState.Success
                            }
                        }

                } else {
                    _authState.value = AuthState.Failed
                    Log.d(TAG, "signUp: failed")
                }
            }
        }
    }

    private fun saveUserDataFireBase(uid: String, name: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to firebaseAuth.currentUser!!.email
        )
        db.collection("users").document(uid).set(user)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "login: success")
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Failed
                    Log.d(TAG, "login: failed")
                }
            }
        }
    }

}

sealed class AuthState {
    object Loading : AuthState()
    object Success : AuthState()
    object Failed : AuthState()
    object Authenticated : AuthState()
}