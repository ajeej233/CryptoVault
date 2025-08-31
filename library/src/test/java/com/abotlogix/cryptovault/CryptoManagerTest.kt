package com.abotlogix.cryptovault

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.abotlogix.cryptovault.CryptoManager
import com.abotlogix.cryptovault.CryptoException
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Test against API 34
class CryptoManagerTest {

    private lateinit var context: Context
    private lateinit var cryptoManager: CryptoManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        cryptoManager = CryptoManager(context)
    }

    @Test
    fun `encrypt and decrypt should return original text`() {
        // Arrange
        val originalText = "Test secret message 123! @#\$"

        // Act
        val encryptedText = cryptoManager.encrypt(originalText)
        val decryptedText = cryptoManager.decrypt(encryptedText)

        // Assert
        assertEquals("Decrypted text should match original", originalText, decryptedText)
        assertTrue("Encrypted text should be different from original", encryptedText != originalText)
    }

    @Test
    fun `encrypt should produce different output for same input`() {
        // Arrange
        val text = "Same input text"

        // Act
        val encrypted1 = cryptoManager.encrypt(text)
        val encrypted2 = cryptoManager.encrypt(text)

        // Assert
        assertNotEquals("Encrypted outputs should be different due to different IVs", encrypted1, encrypted2)
    }

    @Test
    fun `decrypt should work with encrypted text from previous session`() {
        // Arrange
        val originalText = "Persistent secret"
        val encryptedText = cryptoManager.encrypt(originalText)

        // Create a new instance to simulate app restart
        val newCryptoManager = CryptoManager(context)

        // Act
        val decryptedText = newCryptoManager.decrypt(encryptedText)

        // Assert
        assertEquals("New CryptoManager instance should decrypt old encrypted text", originalText, decryptedText)
    }

    @Test(expected = CryptoException::class)
    fun `decrypt should throw CryptoException for invalid ciphertext`() {
        // Arrange
        val invalidCiphertext = "not-a-valid-encrypted-string"

        // Act & Assert
        cryptoManager.decrypt(invalidCiphertext)
    }

    @Test(expected = CryptoException::class)
    fun `decrypt should throw CryptoException for empty string`() {
        // Act & Assert
        cryptoManager.decrypt("")
    }

    @Test
    fun `encrypt should handle empty string`() {
        // Arrange
        val emptyText = ""

        // Act
        val encryptedText = cryptoManager.encrypt(emptyText)
        val decryptedText = cryptoManager.decrypt(encryptedText)

        // Assert
        assertEquals("Empty string should be encrypted and decrypted correctly", emptyText, decryptedText)
    }

    @Test
    fun `encrypt should handle special characters`() {
        // Arrange
        val textWithSpecialChars = "Hello! @#\$%^&*()_+{}|:\"<>?~`-=[]\\;',./"

        // Act
        val encryptedText = cryptoManager.encrypt(textWithSpecialChars)
        val decryptedText = cryptoManager.decrypt(encryptedText)

        // Assert
        assertEquals("Special characters should be preserved", textWithSpecialChars, decryptedText)
    }
}