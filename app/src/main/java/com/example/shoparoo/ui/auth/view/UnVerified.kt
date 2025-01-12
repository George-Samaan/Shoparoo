package com.example.shoparoo.ui.auth.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.shoparoo.R
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel


@Composable
fun UnVerified(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val authViewModel = viewModel<AuthViewModel>()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(lifecycleState) {
        // Do something with your state
        // You may want to use DisposableEffect or other alternatives
        // instead of LaunchedEffect
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                authViewModel.refreshVerification()
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            // Handle other states if necessary
            AuthState.Error -> {}
            AuthState.Loading -> {}
            AuthState.UnAuthenticated -> {}
            AuthState.UnVerified -> {}
        }
    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ReusableLottie(R.raw.confrim, null, size = 222.dp, 1f)
        Text(
            stringResource(R.string.please_verify_your_email),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        val context = LocalContext.current
        val viewModel = viewModel<AuthViewModel>()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 20.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF757575),
                            Color(0xFFE0E0E0),
                        )
                    )
                )
                .clickable {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(context, intent, null)

                }
                .padding(vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.open_mail),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 20.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE0E0E0),
                            Color(0xFF757575),
                        )
                    )
                )
                .clickable {
                    viewModel.refreshVerification()
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }

                }
                .padding(vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.continue_to_home),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}