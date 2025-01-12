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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
                navController.navigate("login")
                isLoading = false
            }

            is AuthState.UnAuthenticated -> {
                isLoading = false
            }

            AuthState.Loading -> isLoading = true
            AuthState.UnVerified -> Unit
            AuthState.Error -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }
        }

    }

    val nameValue = remember { mutableStateOf("") }
    val locationValue = remember { mutableStateOf("") }
    val phoneField = remember { mutableStateOf("") }
    var phoneValidation by remember { mutableStateOf(false) }
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
                .padding(start = 15.dp, top = 30.dp)
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
                    painter = painterResource(id = R.drawable.ic_arrow_back_2),
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
                    stringResource(R.string.sign_up_to_shoparo),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            ReusableLottie(R.raw.login_anim, R.drawable.ic_bg, 200.dp, 1f)
        }


        NameFields(nameValue, "Name", nameValidation)
        NameFields(emailValue, "Email", mailValidation)
        LocationField(locationValue, "Location", nameValidation)
        PhoneField(phoneField, phoneValidation)
        PasswordField(passValue, "Password", passValidation)
        PasswordField(confirmpassValue, "Confirm Password", cPassValidation)
        Spacer(modifier = Modifier.padding(top = 30.dp))
        Button(
            colors = ButtonDefaults.buttonColors(primary),
            onClick = {
                nameValidation = nameValue.value.isEmpty()
                mailValidation = emailValue.value.isEmpty()
                passValidation = passValue.value.isEmpty()
                cPassValidation = confirmpassValue.value.isEmpty()
                phoneValidation = phoneField.value.isEmpty() || !isValidPhoneNumber(phoneField.value)


                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue.value).matches()) {
                    mailValidation = true
                }
                if (!passValue.value.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex())) {
                    passValidation = true
                }
                if (passValue.value != confirmpassValue.value) {
                    cPassValidation = true
                }

                if (!nameValidation && !mailValidation && !passValidation && !cPassValidation && !phoneValidation) {
                    viewModel.signUp(emailValue.value, passValue.value, nameValue.value, locationValue.value, phoneField.value)

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
        Row(stringResource(R.string.don_t_leave_this_field_empty))
    else if (Validation && name == "Email")
        Text(stringResource(R.string.enter_correct, name), color = Color.Red)
}

@Composable
private fun LocationField(
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
                        Icons.Filled.LocationOn, contentDescription = null,
                        tint = primary
                    )
                } else
                    Icon(
                        Icons.Filled.LocationOn, contentDescription = null,
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
        Row(stringResource(R.string.don_t_leave_this_field_empty))
    else if (Validation && name == "Email")
        Text("Enter correct $name", color = Color.Red)
}

@Composable
private fun PhoneField(
    phoneValue: MutableState<String>,
    Validation: Boolean
) {
    Column {
        OutlinedTextField(
            label = { Text(stringResource(R.string.enter_phone_number)) },
            leadingIcon = {
                Icon(
                    Icons.Filled.Phone, contentDescription = null,
                    tint = primary
                )
            },
            value = phoneValue.value,
            onValueChange = { phoneValue.value = it },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp, vertical = 10.dp),
            isError = Validation,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
    }
    // Error message if validation fails
    if (phoneValue.value.isEmpty() && Validation) {
        Row(stringResource(R.string.don_t_leave_this_field_empty))
    } else if (Validation && !isValidPhoneNumber(phoneValue.value)) {
        Text(stringResource(R.string.enter_a_valid_phone_number), color = Color.Red)
    }
}

private fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val cleanedPhoneNumber = phoneNumber.replace(Regex("[\\s-]"), "")
    if (!cleanedPhoneNumber.startsWith("+") && cleanedPhoneNumber.any { !it.isDigit() }) {
        return false
    }
    return when {
        cleanedPhoneNumber.length == 11 && cleanedPhoneNumber.all { it.isDigit() } -> true
        cleanedPhoneNumber.startsWith("+") && cleanedPhoneNumber.length in 12..15 -> true
        else -> false
    }
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
        Row(stringResource(R.string.don_t_leave_this_field_empty))
    else if (Validation && title == "password")
        Text(stringResource(R.string.enter_valid_password), color = Color.Red)
    else if (Validation && title == "Confirm Password")
        Text(stringResource(R.string.passwords_don_t_match), color = Color.Red)
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