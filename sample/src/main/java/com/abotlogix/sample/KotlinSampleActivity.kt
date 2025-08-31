package com.abotlogix.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abotlogix.cryptovault.CryptoVault
import com.abotlogix.sample.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KotlinSampleActivity : AppCompatActivity() {
//    @Inject
//    lateinit var cryptoManager: CryptoManager
    @Inject lateinit var cryptoVault: CryptoVault

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        cryptoManager = CryptoManager(this)
//        testEncryptionDecryption1()
        setupClickListeners()
        observeStoredData()
    }

  /*  private fun testEncryptionDecryption() {
        try {
            val originalText = "This is a secret message! 🔐"
            Log.d("CryptoTest", "Original: $originalText")

            // Encrypt
            val encryptedText = cryptoManager.encrypt(originalText)
            Log.d("CryptoTest", "Encrypted: $encryptedText")

            // Decrypt
            val decryptedText = cryptoManager.decrypt(encryptedText)
            Log.d("CryptoTest", "Decrypted: $decryptedText")

            // Verify
            if (originalText == decryptedText) {
                Log.d("CryptoTest", "✅ SUCCESS: Encryption/Decryption works perfectly!")
            } else {
                Log.d("CryptoTest", "❌ FAILED: Decrypted text doesn't match original")
            }

        } catch (e: Exception) {
            Log.e("CryptoTest", "Error during crypto test: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun testEncryptionDecryption1() {
        try {
            val originalText = "This is a secret message! 🔐"
            Log.d("CryptoTest", "Original: $originalText")

            val encryptedText = cryptoManager.encrypt(originalText)
            Log.d("CryptoTest", "Encrypted: $encryptedText")

            val decryptedText = cryptoManager.decrypt(encryptedText)
            Log.d("CryptoTest", "Decrypted: $decryptedText")

            if (originalText == decryptedText) {
                Log.d("CryptoTest", "✅ SUCCESS: Hilt Injection + Encryption/Decryption works!")
            }

        } catch (e: Exception) {
            Log.e("CryptoTest", "Error: ${e.message}")
            e.printStackTrace()
        }
    }
*/
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            val key = binding.etKey.text.toString()
            val value = binding.etValue.text.toString()

            if (key.isNotEmpty() && value.isNotEmpty()) {
                lifecycleScope.launch {
                    cryptoVault.putString(key, value)
                    Toast.makeText(this@KotlinSampleActivity, "Saved securely!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnRetrieve.setOnClickListener {
            val key = binding.etKey.text.toString()
            if (key.isNotEmpty()) {
                lifecycleScope.launch {
                    val value = cryptoVault.getStringStream(key).first()
                    binding.tvResult.text = "Retrieved: $value"
                }
            }
        }
      binding.btndelete.setOnClickListener {
          val key = binding.etKey.text.toString()
          if (key.isNotEmpty()) {
              lifecycleScope.launch {
                  val value = cryptoVault.delete(key)
                  Toast.makeText(this@KotlinSampleActivity, "Deleted securely!", Toast.LENGTH_SHORT).show()
              }
          }
      }
    }

    private fun observeStoredData() {
        // This shows reactive programming with Flow
        lifecycleScope.launch {
            cryptoVault.getStringStream("test_key").collect { value ->
                binding.tvLiveData.text = "Live Value: $value"
            }
        }
    }
}
