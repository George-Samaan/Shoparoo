package com.example.shoparoo.db.local

interface SharedPreferences {
    fun saveLanguagePreference(language: String)
    fun getLanguagePreference(): String
}