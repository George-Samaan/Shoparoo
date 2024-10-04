package com.example.shoparoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.text.input.TextFieldValue
import com.example.shoparoo.ui.homeScreen.view.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen("George", {}, query = TextFieldValue(""), onQueryChange = {})
        }
    }
}

