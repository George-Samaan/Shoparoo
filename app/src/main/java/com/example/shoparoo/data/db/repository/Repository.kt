package com.example.shoparoo.data.db.repository

import com.example.shoparoo.model.ForYou
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getSmartCollections(): Flow<SmartCollections>
    fun getForYouProducts(): Flow<ForYou>
}