package com.example.shoparoo.data.db.sharedPrefs

import android.content.Context

class SharedPreferencesImpl(private val context: Context): SharedPreferences {
    private val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    override fun saveLanguagePreference(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    override fun getLanguagePreference(): String {
        return sharedPreferences.getString("language", "English") ?: "English"
    }
}