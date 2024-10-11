package com.example.shoparoo.ui.settingsScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.core.content.ContextCompat.getString
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
import com.example.shoparoo.ui.auth.view.ReusableLottie
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
    val isLoggedin = AuthViewModel().authState.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            //  .verticalScroll(rememberScrollState())
            .background(Color(0xFFF7F7F7))
    ) {
        // Profile Header with background
        if (isLoggedin == AuthState.UnAuthenticated) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            navController.navigate("login")
        }
        else{
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
                icon = R.drawable.ic_contact_us,
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
                        selectedCurrency = currency
                        saveCurrencyPreference(context, currency)
                        //   Toast.makeText(context, "Currency changed to $currency", Toast.LENGTH_SHORT).show()
                        showCurrencySheet = false
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
        SignOutButton(showSignOutDialog = showSignOutDialog)
        val authViewModel = AuthViewModel()
        if (showSignOutDialog.value) {
            SignOutConfirmationDialog(
                onDismiss = { showSignOutDialog.value = false },
                onConfirm = {
                    showSignOutDialog.value = false
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
}

@Composable
fun SignOutButton(showSignOutDialog: MutableState<Boolean>) {
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
                showSignOutDialog.value = true
            }
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = stringResource(R.string.sign_out),
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SignOutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sign Out",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to sign out?",
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Sign Out",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.Black
                )
            }
        },
        containerColor = Color.White,
        //   shape = RoundedCornerShape(16.dp)
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
                text = userName ?: "User Name",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUser ?: "user@example.com",
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
            Text(text = stringResource(R.string.contact_number_text), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.contact_number),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clickable {
                        val clip = ClipData.newPlainText(
                            "Contact Number",
                            getString(context, R.string.contact_number)
                        )
                        clipboardManager.setPrimaryClip(clip)
                        Toast
                            .makeText(
                                context,
                                "Copied to clipboard!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }
        Row {
            Text(text = stringResource(R.string.email_text), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.email_contact),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        val clip = ClipData.newPlainText(
                            "Email Contact",
                            getString(context, R.string.email_contact)
                        )
                        clipboardManager.setPrimaryClip(clip)
                        Toast
                            .makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT)
                            .show()
                    }
            )
        }
    }
}

@Composable
fun SettingsCardItem(title: String, icon: Int, onClick: () -> Unit) {
    // Create an animatable scale value
    val scale = remember { Animatable(0.9f) } // Start with a slightly smaller scale value

    // Trigger the animation when the item is composed
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
        // Apply scaling to the Row inside the Box
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
    val names = listOf(
        "George Michel Louis",
        "Galal Ahmed Galal",
        "Ahmed Gamal Mahmoud"
    )
    LazyColumn {
        items(names) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(text = it)
            }
        }
    }
}

@Composable
fun Language() {
    val languages = listOf(
        Pair("English", "\uD83C\uDDFA\uD83C\uDDF8")
        //Pair("Arabic", "\uD83C\uDDEA\uD83C\uDDEC")
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
        //  Pair("EGP", "\uD83C\uDDEA\uD83C\uDDEC" ),
        //  Pair("USD", "\uD83C\uDDFA\uD83C\uDDF8" ),


        Triple("EGP", "\uD83C\uDDEA\uD83C\uDDEC", "USD"),
        Triple("USD", "\uD83C\uDDFA\uD83C\uDDF8", "EGP")
    )




    LazyColumn {
        items(currencies) { (currency, flag,actual ) ->
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