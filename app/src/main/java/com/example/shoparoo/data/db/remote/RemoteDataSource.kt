package com.example.shoparoo.data.db.remote

import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getSmartCollections(): Flow<SmartCollections>
}