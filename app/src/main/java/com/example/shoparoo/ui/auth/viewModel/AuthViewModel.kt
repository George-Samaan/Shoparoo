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
    private var _authState: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.UnAuthenticated)
    val authState: MutableStateFlow<AuthState> = _authState

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    init {
        checkUser()
    }

     private fun checkUser() {
        if (firebaseAuth.currentUser != null) {
            // _authState.value = AuthState.Authenticated
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.UnVerified
            }
        }
    }


    fun signUp(email: String, pass: String, name: String) {
        _authState.value = AuthState.Loading
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
                                _authState.value = AuthState.Authenticated
                            }
                        }

                } else {
                    _authState.value = AuthState.UnAuthenticated
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
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "login: success")
                    if (firebaseAuth.currentUser!!.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.UnVerified
                        val user = firebaseAuth.currentUser
                        user!!.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Email sent.")
                                    _authState.value = AuthState.Authenticated
                                }
                            }
                    }

                } else {
                    _authState.value = AuthState.UnAuthenticated
                    Log.d(TAG, "login: failed")
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Loading
    }

    fun refreshVerification() {
        firebaseAuth.currentUser!!.reload().addOnCompleteListener {
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.UnVerified
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object UnVerified : AuthState()
}