package com.example.shoparoo.ui.settingsScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.currencyApi
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _conversionRate = MutableStateFlow<Double?>(null)
    val conversionRate: StateFlow<Double?> = _conversionRate

    fun fetchConversionRate(fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            try {
                val response = currencyApi.getRates(fromCurrency)
                if (response.isSuccessful) {
                    _conversionRate.value = response.body()?.rates?.get(toCurrency)
                } else {
                    _conversionRate.value = null
                }
            } catch (e: Exception) {
                _conversionRate.value = null
            }
        }
    }


}

