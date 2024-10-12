package com.example.shoparoo.ui.auth.view

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.shoparoo.R
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.theme.primary

@Composable
fun Signup(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()
    var item = viewModel.authState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(item.value) {
        when (item.value) {
            is AuthState.Authenticated -> {
                //Toast.makeText(context, "Successful sign up, Confirm your email", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
                isLoading = false
            }

            is AuthState.UnAuthenticated -> {
               // Toast.makeText(context, "Sign up failed", Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            AuthState.Loading -> isLoading = true
            AuthState.UnVerified -> Unit
            AuthState.Error -> {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                isLoading = false
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
        Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp, start = 15.dp, top = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Sign up to Shoparo",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                    modifier = Modifier.padding(top = 20.dp, start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            ReusableLottie(R.raw.login_anim, R.drawable.ic_bg, 200.dp)
        }


        NameFields(nameValue, "Name", nameValidation)
        NameFields(emailValue, "Email", mailValidation)
        PasswordField(passValue, "password", passValidation)
        PasswordField(confirmpassValue, "Confirm Password", cPassValidation)
        Spacer(modifier = Modifier.padding(top = 30.dp))
        Button(
            colors = ButtonDefaults.buttonColors(primary),
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
                    viewModel.signUp(emailValue.value, passValue.value, nameValue.value)

                    /*                    var auth: FirebaseAuth = Firebase.auth
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
                    //                            }}
                    */

                }

            },
            modifier = Modifier
                .run {
                    if (isLoading) {
                        size(50.dp)
                    } else {
                        fillMaxWidth()
                            .padding(horizontal = 70.dp)
                    }
                }
                .animateContentSize(),
            contentPadding = PaddingValues(15.dp),
            enabled = !isLoading,
        ){
            if (isLoading) {
                Modifier.width(50.dp)
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Sign up",
                    fontSize = 20.sp
                )
            }
        }
    }

}

@Composable
private fun NameFields(
    nameValue: MutableState<String>,
    name: String,
    Validation: Boolean,

    ) {
    Column {
        OutlinedTextField(
            label = { Text("Enter $name") },
            leadingIcon = {
                if (name == "Name") {
                    Icon(
                        Icons.Filled.Person, contentDescription = null,
                        tint = primary
                    )
                } else
                    Icon(
                        Icons.Filled.Email, contentDescription = null,
                        tint = primary
                    )

            },

            value = nameValue.value,
            onValueChange = { nameValue.value = it },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp, vertical = 10.dp),
            isError = Validation,
            singleLine = true
        )
    }
    if (nameValue.value.isEmpty() && Validation)
        Row("Don't leave this field empty")
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
        leadingIcon = {
            Icon(
                Icons.Filled.Lock, contentDescription = null,
                tint = primary
            )
        },
        trailingIcon = {
            Icon(
                if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.clickable {
                    showPassword = !showPassword
                }
            )
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 45.dp, vertical = 10.dp),
        isError = Validation,
        singleLine = true

    )
    if (textValue.value.isEmpty() && Validation)
        Row("Don't leave this field empty")
    else if (Validation && title == "password")
        Text("enter valid password", color = Color.Red)
    else if (Validation && title == "Confirm Password")
        Text("passwords don't match", color = Color.Red)
}

@Composable
fun Row(mssg: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 60.dp)
    ) {
        Text(mssg, color = Color.Red)
    }
}