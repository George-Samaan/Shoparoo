package com.example.shoparoo.set3

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Signup(navController: NavHostController) {

    val viewModel = viewModel<AuthViewModel>()
    LaunchedEffect(key1 = true) {
        viewModel.authState.collect {
            if (it) {
                navController.navigate("login")
            }
        }
    }

    val nameValue = remember { mutableStateOf("") }
    val passValue = remember { mutableStateOf("") }
    val confirmpassValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    var nameValidation by remember { mutableStateOf(false) }
    var mailValidation by remember { mutableStateOf(false) }
    var passValidation by remember { mutableStateOf(false) }
    var cPassValidation by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Sign up to Shoparoo",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 50.dp),
        )
        nameFields(nameValue, "Name", nameValidation)
        nameFields(emailValue, "Email", mailValidation)
        PasswordField(passValue, "password", passValidation)
        PasswordField(confirmpassValue, "confirmPassword", cPassValidation)
        Spacer(modifier = Modifier.padding(top = 50.dp))
        Button(
            onClick = {
                nameValidation = nameValue.value.isEmpty()
                mailValidation = emailValue.value.isEmpty()
                passValidation = passValue.value.isEmpty()
                cPassValidation = confirmpassValue.value.isEmpty()
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue.value).matches()) {
                    mailValidation = true
                }
                if (!passValue.value.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex())) {
                    passValidation = true
                }
                if (passValue.value != confirmpassValue.value) {
                    cPassValidation = true
                }


                if (!nameValidation && !mailValidation && !passValidation && !cPassValidation) {
                    viewModel.signUp(emailValue.value, passValue.value)


//                    var auth: FirebaseAuth = Firebase.auth
//                    auth.createUserWithEmailAndPassword(emailValue.value, passValue.value)
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                val user = Firebase.auth.currentUser
//
//                                user!!.sendEmailVerification()
//                                    .addOnCompleteListener { task ->
//                                        if (task.isSuccessful) {
//                                            Log.d(TAG, "Email sent.")
//                                        }
//                                    }
//                                navController.navigate("login")
//                            } else {
//                                mailValidation = true
//                            }
//                        }

                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 70.dp),
            contentPadding = PaddingValues(15.dp)
        ) {
            Text(
                "Sign up",
                fontSize = 20.sp
            )
        }


    }

}

@Composable
private fun nameFields(
    nameValue: MutableState<String>,
    name: String,
    Validation: Boolean,

    ) {
    Column {
        OutlinedTextField(
            label = { Text("Enter your $name") },
            leadingIcon = {
                if (name == "Name") {
                    Icon(Icons.Filled.Person, contentDescription = null)
                } else
                    Icon(Icons.Filled.Email, contentDescription = null)

            },

            value = nameValue.value,
            onValueChange = { nameValue.value = it },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.padding(vertical = 20.dp),
            isError = Validation
        )
    }
    if (nameValue.value.isEmpty() && Validation)
        Text("Don't leave any field empty", color = Color.Red)
    else if (Validation && name == "Email")
        Text("Enter correct $name", color = Color.Red)
}


@Composable
fun PasswordField(
    textValue: MutableState<String>,
    title: String,
    Validation: Boolean

) {
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        label = { Text(title) },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
        trailingIcon = {
            val image =
                if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    image,
                    contentDescription = if (showPassword) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.padding(vertical = 20.dp),
        isError = Validation

    )
    if (textValue.value.isEmpty() && Validation)
        Text("Don't leave any field empty", color = Color.Red)
    else if (Validation && title == "password")
        Text("enter valid password", color = Color.Red)
    else if (Validation && title == "confirmPassword")
        Text("passwords don't match", color = Color.Red)
}