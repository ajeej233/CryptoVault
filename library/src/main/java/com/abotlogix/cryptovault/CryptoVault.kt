package com.abotlogix.cryptovault

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoVault @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val dataStoreRepository: DataStoreRepository
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // PRIMARY API - KOTLIN COROUTINES (for Kotlin clients)
    suspend fun putString(key: String, value: String) {
        val encryptedValue = cryptoManager.encrypt(value)
        dataStoreRepository.putEncryptedValue(key, encryptedValue)
    }

    fun getStringStream(key: String): Flow<String?> {
        return dataStoreRepository.userPreferencesFlow.map { preferences ->
            preferences.encryptedData[key]?.let { encryptedValue ->
                cryptoManager.decrypt(encryptedValue)
            }
        }
    }

    suspend fun delete(key: String) {
        dataStoreRepository.deleteEncryptedValue(key)
    }

    // SECONDARY API -  (for Java clients)
    @JvmOverloads
    fun putString(key: String, value: String, callback: ((Exception?) -> Unit)? = null) {
        ioScope.launch {
            try {
                val encryptedValue = cryptoManager.encrypt(value)
                dataStoreRepository.putEncryptedValue(key, encryptedValue)
                callback?.invoke(null)
            } catch (e: Exception) {
                callback?.invoke(e)
            }
        }
    }

    fun getStringFuture(key: String): CompletableFuture<String?> {
        val future = CompletableFuture<String?>()
        ioScope.launch {
            try {
                // Get the first value from the flow
                val value = getStringStream(key).first()
                future.complete(value)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }
        return future
    }

    @JvmOverloads
    fun delete(key: String, callback: ((Exception?) -> Unit?)? = null) {
        ioScope.launch {
            try {
                dataStoreRepository.deleteEncryptedValue(key)
                callback?.invoke(null)
            } catch (e: Exception) {
                callback?.let { it(e) }
            }
        }
    }
}