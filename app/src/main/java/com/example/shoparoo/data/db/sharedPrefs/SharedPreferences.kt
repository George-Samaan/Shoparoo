package com.example.shoparoo.data.db.sharedPrefs

interface SharedPreferences {
    fun saveCurrencyPreference(currency: String)
    fun getCurrencyPreference(): String
}