package com.example.shoparoo.data.db.repository

import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {
    // remote data source
    override fun getSmartCollections(): Flow<SmartCollections> {
        return remoteDataSource.getSmartCollections()
    }
}