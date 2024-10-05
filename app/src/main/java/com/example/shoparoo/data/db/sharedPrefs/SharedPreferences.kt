package com.example.shoparoo.data.db.sharedPrefs

interface SharedPreferences {
    fun saveLanguagePreference(language: String)
    fun getLanguagePreference(): String
}