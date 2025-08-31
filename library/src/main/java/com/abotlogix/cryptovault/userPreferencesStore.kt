package com.abotlogix.cryptovault

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.abotlogix.cryptovault.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Define the serializer for our Proto object
private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

class DataStoreRepository @Inject constructor(private val context: Context) {

    // Flow to read all data
    val userPreferencesFlow: Flow<UserPreferences> = context.userPreferencesStore.data

    // Function to get a specific value by key
    suspend fun getEncryptedValue(key: String): String? {
        return context.userPreferencesStore.data.map { preferences ->
            preferences.encryptedData[key]
        }.first() // Gets the first emission from the flow
    }

    // Function to store a key-value pair
    suspend fun putEncryptedValue(key: String, value: String) {
        context.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .putEncryptedData(key, value)
                .build()
        }
    }

    // Function to delete a key-value pair
    suspend fun deleteEncryptedValue(key: String) {
        context.userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .removeEncryptedData(key)
                .build()
        }
    }
}