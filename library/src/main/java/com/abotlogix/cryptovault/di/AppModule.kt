package com.abotlogix.cryptovault.di

import android.content.Context
import com.abotlogix.cryptovault.CryptoManager
import com.abotlogix.cryptovault.CryptoVault
import com.abotlogix.cryptovault.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCryptoManager(@ApplicationContext context: Context): CryptoManager {
        return CryptoManager(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepository(context)
    }
    @Provides
    @Singleton
    fun provideCryptoVault(
        cryptoManager: CryptoManager,
        dataStoreRepository: DataStoreRepository
    ): CryptoVault {
        return CryptoVault(cryptoManager, dataStoreRepository)
    }
}