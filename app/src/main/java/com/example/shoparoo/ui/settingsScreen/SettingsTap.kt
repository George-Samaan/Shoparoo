@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.settingsScreen

import android.content.Context
import android.content.Intent
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
import java.util.Locale


@Composable
fun SettingsScreen(navController: NavController) {
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showAboutUsSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load saved language preference
    val savedLanguage = getLanguagePreference(context)
    var selectedLanguage by remember { mutableStateOf(savedLanguage) } // Default to saved language

    val currencyIcon = if (showCurrencySheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow
    val languageIcon = if (showLanguageSheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow
    val aboutUsIcon = if (showAboutUsSheet) R.drawable.ic_arrow_down else R.drawable.ic_arrow

    Modifier.background(Color.White)
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
                )
                {
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
                        SettingsItem(stringResource(R.string.currency), R.drawable.currency, currencyIcon, onClick = {
                            showCurrencySheet = !showCurrencySheet
                        })
                        Spacer(modifier = Modifier.height(18.dp))

                        SettingsItem(stringResource(R.string.language), R.drawable.language, languageIcon, onClick = {
                            showLanguageSheet = !showLanguageSheet
                        })
                        Spacer(modifier = Modifier.height(18.dp))

                        SettingsItem(stringResource(R.string.about_us), R.drawable.info, aboutUsIcon, onClick = {
                            showAboutUsSheet = !showAboutUsSheet
                        })
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
    )

    // BottomSheet display logic
    if (showCurrencySheet) {
        BottomSheet(onDismiss = { showCurrencySheet = false }) {
            Currency()
        }
    }

    if (showLanguageSheet) {
        BottomSheet(onDismiss = { showLanguageSheet = false }) {
            Language(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    selectedLanguage = language
                    changeLanguage(context, language)
                    saveLanguagePreference(context, language)
                }
            )
        }
    }

    if (showAboutUsSheet) {
        BottomSheet(onDismiss = { showAboutUsSheet = false }) {
            AboutUs()
        }
    }
}
//___________________________________________________________________

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

//___________________________________________________________________
@Composable
fun Currency() {
    val countries = listOf(
        Pair("USD", "\uD83C\uDDFA\uD83C\uDDF8"),
        Pair("CAD", "\uD83C\uDDE8\uD83C\uDDE6"),
        Pair("EUR", "\uD83C\uDDE9\uD83C\uDDEA"),
        Pair("Japan Yen", "\uD83C\uDDEF\uD83C\uDDF5"),
        Pair("Yuan", "\uD83C\uDDE8\uD83C\uDDF3"),
        Pair("Brazil", "\uD83C\uDDE7\uD83C\uDDF7"),
        Pair("RUB", "\uD83C\uDDF7\uD83C\uDDFA"),
    )

    LazyColumn {
        items(countries) { (country, flag) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = flag,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(text = country)
            }
        }
    }
}

@Composable
fun Language(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    val languages = listOf(
        Pair("English", "\uD83C\uDDFA\uD83C\uDDF8"),
        Pair("Arabic", "\uD83C\uDDEA\uD83C\uDDEC")
    )

    LazyColumn {
        items(languages) { (language, flag) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp)
                    .clickable {
                        onLanguageSelected(language)  // Trigger language change
                    }
            ) {
                Text(
                    text = flag,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(
                    text = language,
                    fontWeight = if (language == selectedLanguage) FontWeight.Bold else FontWeight.Normal // Highlight selected language
                )
            }
        }
    }
}

fun changeLanguage(context: Context, language: String) {
    val locale = when (language) {
        "English" -> Locale("en")
        "Arabic" -> Locale("ar")
        else -> Locale.getDefault()
    }

    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}

fun saveLanguagePreference(context: Context, language: String) {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language", language).apply()
}

fun getLanguagePreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language", "English") ?: "English"  // Default to English
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

