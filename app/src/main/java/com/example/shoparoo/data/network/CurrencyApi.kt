package com.example.shoparoo.data.network

import com.example.shoparoo.model.CurrencyResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("rates/latest")
    suspend fun getRates(@Query("apikey") apiKey: String): Response<CurrencyResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.currencyfreaks.com/v2.0/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val currencyApi: CurrencyApi = retrofit.create(CurrencyApi::class.java)
