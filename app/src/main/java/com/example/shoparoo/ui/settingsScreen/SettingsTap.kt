@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.settingsScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.MainActivity
import com.example.shoparoo.R
import com.example.shoparoo.data.network.currencyApi
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun SettingsScreen(navController: NavController) {
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showAboutUsSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val savedCurrency = getCurrencyPreference(context)
    var selectedCurrency by remember { mutableStateOf(savedCurrency) }

    val currencyIcon = if (showCurrencySheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow
    val languageIcon = if (showLanguageSheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow
    val aboutUsIcon = if (showAboutUsSheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.settings),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 16.dp)
                ) {
                    item {
                        SettingsItem(
                            stringResource(R.string.currency),
                            R.drawable.currency,
                            currencyIcon,
                            onClick = { showCurrencySheet = !showCurrencySheet }
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        SettingsItem(
                            stringResource(R.string.language),
                            R.drawable.language,
                            languageIcon,
                            onClick = { showLanguageSheet = !showLanguageSheet }
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        SettingsItem(
                            stringResource(R.string.about_us),
                            R.drawable.info,
                            aboutUsIcon,
                            onClick = { showAboutUsSheet = !showAboutUsSheet }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
    )

    if (showCurrencySheet) {
        BottomSheet(onDismiss = { showCurrencySheet = false }) {
            Currency(
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { currency ->
                    selectedCurrency = currency
                    saveCurrencyPreference(context, currency)
                    Toast.makeText(context, "Currency changed to $currency", Toast.LENGTH_SHORT).show()
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
}



@Composable
fun SettingsItem(title: String, icon: Int, arrowIcon: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = arrowIcon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun Currency(selectedCurrency: String, onCurrencySelected: (String) -> Unit) {
    val context = LocalContext.current
    val currencies = listOf(
        Pair("USD", "\uD83C\uDDFA\uD83C\uDDF8"),
        Pair("EGP", "\uD83C\uDDEA\uD83C\uDDEC")
    )

    LazyColumn {
        items(currencies) { (currency, flag) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clickable {
                        // Trigger currency conversion
                        onCurrencySelected(currency)
                        fetchConversionRate(context, currency)
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
            val response = currencyApi.getRates("db5f5601837148c482888a4cdf945326")
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
    sharedPreferences.edit().putFloat("conversionRate", conversionRate.toFloat()).apply()

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
    return sharedPreferences.getString("currency", "USD") ?: "USD"  // Default to USD
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
                    fontWeight =  FontWeight.Bold
                )
            }
        }
    }
}

//___________________________________________________________________

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
//___________________________________________________________________

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(rememberNavController())
}

