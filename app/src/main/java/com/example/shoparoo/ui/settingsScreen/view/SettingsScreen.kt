package com.example.shoparoo.ui.settingsScreen.view

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.currencyApi
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.theme.grey
import com.example.shoparoo.ui.theme.primary
import com.example.shoparoo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import networkListener


@Composable
fun ProfileScreen(navController: NavController) {
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showAboutUsSheet by remember { mutableStateOf(false) }
    val showContactUsSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val savedCurrency = getCurrencyPreference(context)
    var selectedCurrency by remember { mutableStateOf(savedCurrency) }
    val showSignOutDialog = remember { mutableStateOf(false) }
    val isNetworkAvailable = networkListener()
    val authViewModel = viewModel<AuthViewModel>()
    val isSignedIn by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF7F7F7))
    ) {
        // Profile Header with background
        ProfileHeader()

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SettingsCardItem(
                title = stringResource(R.string.currency),
                icon = R.drawable.ic_currency,
                onClick = { showCurrencySheet = !showCurrencySheet }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsCardItem(
                title = stringResource(R.string.language),
                icon = R.drawable.ic_language,
                onClick = { showLanguageSheet = !showLanguageSheet }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsCardItem(
                title = stringResource(R.string.about_us),
                icon = R.drawable.ic_about_us,
                onClick = { showAboutUsSheet = !showAboutUsSheet }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsCardItem(
                title = stringResource(R.string.contact_us),
                icon = R.drawable.baseline_email_24,
                onClick = { showContactUsSheet.value = !showContactUsSheet.value }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        // Bottom Sheets and Dialogs
        if (showCurrencySheet) {
            BottomSheet(onDismiss = { showCurrencySheet = false }) {
                Currency(
                    selectedCurrency = selectedCurrency,
                    onCurrencySelected = { currency ->
                        if (!isNetworkAvailable.value) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            selectedCurrency = currency
                            saveCurrencyPreference(context, currency)
                            Toast.makeText(
                                context,
                                "Currency changed to $currency",
                                Toast.LENGTH_SHORT
                            ).show()
                            showCurrencySheet = false
                        }
                    }
                )
            }
        }

        if (showLanguageSheet) {
            BottomSheet(onDismiss = { showLanguageSheet = false }) {
                Language()
            }
        }

        if (showAboutUsSheet) {
            BottomSheet(onDismiss = { showAboutUsSheet = false }) {
                AboutUs()
            }
        }

        if (showContactUsSheet.value) {
            BottomSheet(onDismiss = { showContactUsSheet.value = false }) {
                ContactUs()
            }
        }
        SignOutButton(showSignOutDialog = showSignOutDialog, isSignedIn, navController)
        if (showSignOutDialog.value) {
            SignOutConfirmationDialog(
                onDismiss = { showSignOutDialog.value = false },
                onConfirm = {
                    if (!isNetworkAvailable.value) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.no_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.signing_out), Toast.LENGTH_SHORT
                        ).show()
                        showSignOutDialog.value = false
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SignOutButton(
    showSignOutDialog: MutableState<Boolean>,
    isSignedIn: AuthState,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 20.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE0E0E0),
                        Color(0xFF757575)
                    )
                )
            )
            .clickable {
                if (isSignedIn == AuthState.Authenticated || isSignedIn == AuthState.UnVerified)
                    showSignOutDialog.value = true
                else {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            .padding(vertical = 14.dp)
    ) {
        if (isSignedIn == AuthState.Authenticated || isSignedIn == AuthState.UnVerified) {
            Text(
                text = stringResource(R.string.sign_out),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(
                text = stringResource(R.string.sign_in),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun SignOutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.sign_outt),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.are_you_sure_you_want_to_sign_outt),
                color = Color.Black
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    primary
                )
            )
            {
                Text(
                    text = stringResource(R.string.sign_outt),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(grey)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White
                )
            }
        },
        containerColor = Color.White,
    )
}

@Composable
fun ProfileHeader() {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    LaunchedEffect(Unit) {
        viewModel.getName()
    }
    val userName by viewModel.userName.collectAsState()
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser?.email

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 25.dp)
            .height(300.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFA6A5A5)) // Grey gradient
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.profile_animation)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userName ?: "Guest",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUser ?: "guest@shoparoo.com",
                color = Color(0xFF494949),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ContactUs() {
    val context = LocalContext.current
    val clipboardManager =
        remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    Column(modifier = Modifier.padding(10.dp)) {


        Row {
            Text(text = "Contact Us: ", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Here",
                //stringResource(R.string.email_contact),
                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:Shoparoo@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Subject here")
                            putExtra(Intent.EXTRA_TEXT, "Body of the email here")
                        }
                        startActivity(context, intent, null)
                    }
            )
        }
    }
}

@Composable
fun SettingsCardItem(title: String, icon: Int, onClick: () -> Unit) {
    // Create an animatable scale value
    val scale = remember { Animatable(0.9f) } // Start with a slightly smaller scale value
    LaunchedEffect(Unit) {
        // Animate to the target value of 1f over the specified duration
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFD7D6D6)) // Set the background color
            .padding(20.dp) // Padding around content
            .clickable(
                onClick = {
                    onClick()
                },
                onClickLabel = title,
            )
    ) {
        Row(
            modifier = Modifier
                .scale(scale.value) // Scale the Row
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF000000)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val alpha by animateFloatAsState(targetValue = if (modalBottomSheetState.isVisible) 1f else 0f)
    ModalBottomSheet(
        containerColor = Color(0xFFFFFFFF),
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Box(
            Modifier
                .graphicsLayer(alpha = alpha) // Animate the alpha
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun AboutUs() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
    ) {
        Text(
            text = "Developed by: ",
        )
        Column {
            Text(
                "Ahmed Gamal ",
                Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:agamal00500@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Subject here")
                        putExtra(Intent.EXTRA_TEXT, "Body of the email here")
                    }
                    startActivity(context, intent, null)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,

                )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "Galal Ahmed ",
                Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:ga71387@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Subject here")
                        putExtra(Intent.EXTRA_TEXT, "Body of the email here")
                    }
                    startActivity(context, intent, null)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,

                )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "George Michel ", Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:georgesmichel2009@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Subject here")
                        putExtra(Intent.EXTRA_TEXT, "Body of the email here")
                    }
                    startActivity(context, intent, null)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold

            )
        }
    }


}

@Composable
fun Language() {
    val languages = listOf(
        Pair("English", "\uD83C\uDDFA\uD83C\uDDF8")
    )

    LazyColumn {
        items(languages) { (language, flag) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clickable {}
            ) {
                Text(
                    text = flag,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(
                    text = language,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Currency(selectedCurrency: String, onCurrencySelected: (String) -> Unit) {
    val context = LocalContext.current
    val currencies = listOf(
        Triple("EGP", "\uD83C\uDDEA\uD83C\uDDEC", "USD"),
        Triple("USD", "\uD83C\uDDFA\uD83C\uDDF8", "EGP")
    )

    LazyColumn {
        items(currencies) { (currency, flag, actual) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clickable {
                        // Trigger currency conversion
                        onCurrencySelected(actual)
                        fetchConversionRate(context, actual)

                    }
            ) {
                Text(
                    text = flag,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(text = currency)
            }
        }
    }
}

fun fetchConversionRate(context: Context, selectedCurrency: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = currencyApi.getRates(Constants.currencyApi)
            if (response.isSuccessful) {
                val rates = response.body()?.rates
                rates?.let {
                    val conversionRate = it[selectedCurrency] ?: 1.0
                    updatePrices(context, conversionRate)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun updatePrices(context: Context, conversionRate: Double) {
    //store the conversion rate in SharedPreferences and update product prices across the app
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    var cc = 1 / conversionRate
    sharedPreferences.edit().putFloat("conversionRate", cc.toFloat()).apply()

    CoroutineScope(Dispatchers.Main).launch {
        // Call necessary composables to update UI
    }
}

fun saveCurrencyPreference(context: Context, currency: String) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("currency", currency).apply()
}

fun getCurrencyPreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("currency", "EGP") ?: "EGP"  // Default to USD
}