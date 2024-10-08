package com.example.shoparoo.ui.auth.view


import android.widget.Toast
import androidx.annotation.RawRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.shoparoo.R
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.theme.primary

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()
    var item = viewModel.authState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(item.value) {
        when (item.value) {
            is AuthState.Success -> {
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
                isLoading = false
            }

            is AuthState.Failed -> {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            AuthState.Authenticated -> {
                navController.navigate("home")
                Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            AuthState.Loading -> isLoading = true
            AuthState.UnAuthenticated -> Unit
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Existing User ? Sign in",
                    fontSize = 37.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary,
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            ReusableLottie(R.raw.login_anim, R.drawable.ic_bg, 200.dp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email, contentDescription = null,
                        tint = primary
                    )
                },
                value = emailValue.value,
                onValueChange = { emailValue.value = it },
                label = { Text(stringResource(R.string.enter_email)) },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 45.dp, vertical = 10.dp),
                isError = emailValidation,
            )
            if (emailValidation)
                Row("Don't leave your email empty")
            OutlinedTextField(
                value = passwordValue.value,
                onValueChange = {
                    passwordValue.value = it
                },
                label = { Text(stringResource(R.string.enter_your_password)) },

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
                isError = passValidation
            )
            if (passValidation)
                Row("Don't leave your password empty")
            Spacer(modifier = Modifier.height(25.dp))
            val buttonSize by animateDpAsState(
                targetValue = if (isLoading) 50.dp else 444.dp,
                animationSpec = tween(durationMillis = 500)
            )


            Button(
                colors = ButtonDefaults.buttonColors(primary),
                onClick = {
                    if (passwordValue.value.isEmpty()) {
                        passValidation = true
                    } else {
                        passValidation = false
                    }

                    if (emailValue.value.isEmpty()) {
                        emailValidation = true
                    } else {
                        emailValidation = false
                    }

                    if (!passValidation && !emailValidation) {
                        viewModel.login(emailValue.value, passwordValue.value)
                    }
                },
                modifier = Modifier
                    .run {
                        if (isLoading) {
                            size(50.dp) // Smaller size when loading
                        } else {
                            fillMaxWidth() // Full width when not loading
                                .padding(horizontal = 70.dp)
                        }
                    }
                    .animateContentSize(),
                contentPadding = PaddingValues(15.dp),
                enabled = !isLoading,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = primary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        "Login",
                        fontSize = 20.sp
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account?", Modifier.padding(end = 5.dp),
                    fontSize = 17.sp
                )
                Text(
                    "Register Now", fontSize = 18.sp, color = primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("signup")
                        }
                        .padding(start = 4.dp)
                )
            }

            Text(
                "Continue as a guest", fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable {
                        navController.navigate("home")
                    },
                textAlign = TextAlign.Right // Aligning text to the right
            )
        }
    }
}

@Composable
fun ReusableLottie(
    @RawRes lottieRes: Int,
    backgroundImageRes: Int?,
    size: Dp

) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true // Auto-play
    )

    Box(
        modifier = Modifier
            .size(size)
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
    ) {
        // Background Image
        Image(
            painter = painterResource(backgroundImageRes!!),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Lottie Animation
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.fillMaxSize()
        )
    }
}