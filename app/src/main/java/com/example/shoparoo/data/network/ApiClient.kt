package com.example.shoparoo.data.network

import com.example.shoparoo.utils.Constants.ADMIN_API_ACCESS_TOKEN
import com.example.shoparoo.utils.Constants.SHOPAROO_APIKEY
import com.example.shoparoo.utils.Constants.SHOPAROO_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val request = original.newBuilder()
            .header(
                "Authorization", "Basic ${
                    android.util.Base64.encodeToString(
                        ("$SHOPAROO_APIKEY:$ADMIN_API_ACCESS_TOKEN").toByteArray(),
                        android.util.Base64.NO_WRAP
                    )
                }"
            )
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: ApiServices = Retrofit.Builder()
        .baseUrl(SHOPAROO_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiServices::class.java)
}