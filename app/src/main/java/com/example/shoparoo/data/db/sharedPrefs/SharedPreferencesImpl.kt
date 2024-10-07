package com.example.shoparoo.data.db.sharedPrefs

import android.content.Context

class SharedPreferencesImpl(private val context: Context): SharedPreferences {
    private val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)


    override fun saveCurrencyPreference(currency: String) {
        sharedPreferences.edit().putString("currency", currency).apply()
    }

    override fun getCurrencyPreference(): String {
        return sharedPreferences.getString("currency", "USD") ?: "USD"
    }
}