package com.example.shoparoo.ui.splash

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.auth.view.ReusableLottie
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavController) {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        showSplash = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ReusableLottie(R.raw.splash, null, size = 400.dp)
    }
    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()
    var item = viewModel.authState.collectAsState()


    LaunchedEffect(showSplash) {
        if (!showSplash) {
            when (item.value) {
                is AuthState.Success -> {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                    Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                }

                AuthState.Authenticated -> {
                    navController.navigate("home"){
                        popUpTo("splash") { inclusive = true }
                    }
                    Toast.makeText(context, "Welcome Back!", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    navController.navigate("login"){
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }

        }
    }
}
