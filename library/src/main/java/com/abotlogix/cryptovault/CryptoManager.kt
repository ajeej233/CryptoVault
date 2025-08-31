package com.abotlogix.cryptovault

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager(private val context: Context) {

    // Initialize the Keystore
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null) // Load the keystore with no parameters
    }

    companion object {
        private const val KEY_ALIAS = "CryptoVault_KeyAlias_v1" // Unique key for this app
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val IV_SIZE = 12 // GCM recommended IV size is 12 bytes
        private const val GCM_TAG_LENGTH = 128 // Galois/Counter Mode authentication tag length
    }

    private fun getOrCreateSecretKey(): SecretKey {
        // 1. Try to get the existing key from the Keystore
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) {
            return existingKey
        }

        // 2. If the key doesn't exist, generate a new one
        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEYSTORE)

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(ENCRYPTION_BLOCK_MODE)
            .setEncryptionPaddings(ENCRYPTION_PADDING)
            .setKeySize(256) // Use AES-256 for strong encryption
            .setUserAuthenticationRequired(false) // Set to 'true' for even higher security (requires auth to use key)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    fun encrypt(plaintext: String): String {
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKey = getOrCreateSecretKey()

            // Generate a random IV for this encryption operation
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv

            // Perform encryption
            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

            // Combine IV + ciphertext and encode as Base64
            val combinedData = ByteArray(iv.size + ciphertext.size)
            System.arraycopy(iv, 0, combinedData, 0, iv.size)
            System.arraycopy(ciphertext, 0, combinedData, iv.size, ciphertext.size)

            return Base64.encodeToString(combinedData, Base64.DEFAULT)
        } catch (e: Exception) {
            throw CryptoException("Encryption failed", e)
        }
    }


    fun decrypt(ciphertext: String): String {
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKey = getOrCreateSecretKey()

            // Decode Base64 and split IV from actual ciphertext
            val combinedData = Base64.decode(ciphertext, Base64.DEFAULT)
            val iv = combinedData.copyOfRange(0, IV_SIZE)
            val encryptedData = combinedData.copyOfRange(IV_SIZE, combinedData.size)

            // Initialize cipher with the same IV used for encryption
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            // Perform decryption
            val decryptedData = cipher.doFinal(encryptedData)
            return String(decryptedData, Charsets.UTF_8)
        } catch (e: Exception) {
            throw CryptoException("Decryption failed", e)
        }
    }
}