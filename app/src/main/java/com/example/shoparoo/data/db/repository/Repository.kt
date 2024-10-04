package com.example.shoparoo.data.db.repository

import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getSmartCollections(): Flow<SmartCollections>
}