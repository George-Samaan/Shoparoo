package com.example.shoparoo.ui.theme.db.local

interface SharedPreferences {
    fun saveLanguagePreference(language: String)
    fun getLanguagePreference(): String
}