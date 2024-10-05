package com.example.shoparoo.ui.auth.view


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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


@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()
    var item = viewModel.authState.collectAsState()



    LaunchedEffect(item.value) {
        when (item.value) {
            is AuthState.Success -> {
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
              //  navController.navigate("login")
            }
            is AuthState.Failed -> {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
            }

            AuthState.Authenticated ->{
                navController.navigate("home")
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
            }
            AuthState.Loading -> Unit
        }
    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val emailValue = remember { mutableStateOf("") }
        val passwordValue = remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }
        var passValidation by remember { mutableStateOf(false) }
        var emailValidation by remember { mutableStateOf(false) }

        Text(
            "Login to Shoparoo",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 100.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                    ) },
                value = emailValue.value,
                onValueChange = { emailValue.value = it },
                label = { Text(stringResource(R.string.enter_your_email)) },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 10.dp),
                isError = emailValidation
            )
            if (emailValidation)
                Text("Enter correct email", color = Color.Red)
        }



        Column(
            modifier = Modifier.padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = passwordValue.value,
                onValueChange = {
                    passwordValue.value = it
                },
                label = { Text(stringResource(R.string.enter_your_password)) },

                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary

                ) },
                trailingIcon = {
                   Icon(
                       if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                       contentDescription = null,
                       tint = MaterialTheme.colorScheme.primary,
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
                    .padding(start = 25.dp, end = 25.dp, bottom = 10.dp),
                isError = passValidation
            )
            if (passValidation)
                Text("Don't leave your password empty", color = Color.Red)
        }



        Button(
            onClick = {

                if (passwordValue.value == "") {
                    passValidation = true
                } else
                    passValidation = false

                if (emailValue.value == "") {
                    emailValidation = true
                } else
                    emailValidation = false
            //    if (!passValidation && !emailValidation) {
                    viewModel.login(emailValue.value, passwordValue.value)
              //  }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 70.dp),
            contentPadding = PaddingValues(15.dp)
        ) {
            Text(
                "Login",
                fontSize = 20.sp
            )
        }
        Text("Don't hava an account", Modifier.padding(top = 25.dp))
        Text("Register Now", fontWeight = FontWeight.Bold, modifier = Modifier
            .padding(top = 10.dp)
            .clickable {
                navController.navigate("signup")
            } )
            Text("or")
        Text("Continue as a guest", fontWeight = FontWeight.Bold, modifier = Modifier
            .padding(vertical = 1.dp)
            .clickable {
//                navController.navigate("home")
//                MainScreen("George", {}, query = TextFieldValue(""), onQueryChange = {})

            } )

        //  Text("Or use another service", modifier = Modifier.fillMaxWidth().padding(start = 5.dp))

    }
}

