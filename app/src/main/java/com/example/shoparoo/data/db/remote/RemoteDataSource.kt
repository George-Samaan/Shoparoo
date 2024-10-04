package com.example.shoparoo.data.db.remote

import com.example.shoparoo.model.ForYou
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getSmartCollections(): Flow<SmartCollections>
    fun getForYouProducts(): Flow<ForYou>
}