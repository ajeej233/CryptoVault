
# CryptoVault 🔐

A secure, modern Android library for storing encrypted key-value data using Android Keystore, Hilt, and Jetpack DataStore.

## Features ✨

- **🔒 Hardware-backed Encryption**: Uses Android Keystore with AES-GCM encryption.
- **📦 Modern Architecture**: Built with Hilt (DI), Coroutines, Flow, and DataStore.
- **🌐 Java & Kotlin Support**: Full interoperability with both languages (Java via `CompletableFuture` and Kotlin via `Flow`).
- **⚡ Asynchronous Operations**: Reactive streams with Flow and Coroutines for async operations.
- **🛡️ Security First**: Random IV generation, secure key handling, and encrypted data storage.
- **✅ Production Ready**: Fully tested, with a focus on reliability and security.

## Why CryptoVault? 🤔

You might ask: *"Why not just use EncryptedSharedPreferences?"*

While Android's `EncryptedSharedPreferences` is a good solution, **CryptoVault** offers several advantages:

- **🏗 Modern Architecture**: Built with Hilt (Dependency Injection) and Jetpack DataStore, following current Android best practices.
- **⚡ Asynchronous Operations**: First-class support for asynchronous operations and reactive streams.
- **🌐 Java & Kotlin Support**: Designed for both languages with optimized APIs for each.
- **🎯 Learning Focus**: Understand how encryption actually works with Android Keystore, rather than just using a black box.

## Installation 📦

### Step 1: Add JitPack repository
Add this to your project-level `build.gradle`:
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
````

### Step 2: Add dependency

Add this to your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.ajeej233:cryptovault:latest'
}
```

## Usage 🚀

### Kotlin (Recommended)

```kotlin
// Inject CryptoVault (requires Hilt setup)
@Inject lateinit var cryptoVault: CryptoVault

// Store data
lifecycleScope.launch {
    cryptoVault.putString("api_token", "your-secret-token-here")
}

// Retrieve data (as Flow)
lifecycleScope.launch {
    cryptoVault.getStringStream("api_token").collect { token ->
        // Use your decrypted token
    }
}

// Or get once (suspending function)
val value = cryptoVault.getStringStream("api_token").first()
```

### Java Support

```java
// Simple fire-and-forget
cryptoVault.putString("user_id", "user123");

// With callback for error handling
cryptoVault.putString("user_data", "secret_value", e -> {
    if (e == null) {
        runOnUiThread(() -> Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show());
    } else {
        runOnUiThread(() -> binding.tvResult.setText("Error: " + e.getMessage()));
    }
    return null;
});

// Retrieve data with CompletableFuture (for Java clients)
CompletableFuture<String> future = cryptoVault.getString("user_id");
future.thenAccept(userId -> {
    // Use decrypted value (null if not found or error occurred)
    if (userId != null) {
        // Do something with userId
    } else {
        // Handle null case (maybe display an error)
    }
});
```

## Storing Different Data Types 💾

While the primary API uses `String` values, you can easily store any data type:

### Storing Various Types

```kotlin
// Integers
cryptoVault.putString("user_age", 25.toString())

// Doubles/Floats  
cryptoVault.putString("price", 9.99.toString())

// Booleans
cryptoVault.putString("is_premium", true.toString())

// Longs
cryptoVault.putString("timestamp", System.currentTimeMillis().toString())

// JSON Objects (complex data)
val userData = JSONObject().apply {
    put("name", "John")
    put("email", "john@email.com")
}
cryptoVault.putString("user_profile", userData.toString()) // Note: Stored as a string for encryption
```

### Retrieving and Converting Back

```kotlin
// Integer
val age = cryptoVault.getString("user_age")?.toIntOrNull()  // Integer conversion

// Double
val price = cryptoVault.getString("price")?.toDoubleOrNull()

// Boolean
val isPremium = cryptoVault.getString("is_premium")?.toBooleanStrictOrNull()

// Long  
val timestamp = cryptoVault.getString("timestamp")?.toLongOrNull()

// JSON Object
val userJson = cryptoVault.getString("user_profile")?.let {
    JSONObject(it)
}
```

## Setup Requirements ⚙️

### Hilt Configuration

Your Application class must be annotated with `@HiltAndroidApp`:

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

### Minimum SDK

* **minSdkVersion**: 24+ (Android 7.0+)
* Required for reliable Android Keystore support

## API Reference 📖

### Primary Methods

| Method            | Description            | Kotlin          | Java                |
| ----------------- | ---------------------- | --------------- | ------------------- |
| `putString`       | Store encrypted value  | `suspend fun`   | `@JvmOverloads fun` |
| `getStringStream` | Get value as Flow      | `Flow<String?>` | `CompletableFuture` |
| `delete`          | Remove encrypted value | `suspend fun`   | `@JvmOverloads fun` |

## Security Details 🔍

* **Encryption**: AES-GCM with 256-bit keys.
* **Key Storage**: Securely managed through Android Keystore (hardware-backed when available).
* **IV Generation**: Random 12-byte IV for each operation (GCM standard for enhanced security).
* **Data Storage**: Encrypted values stored using Proto DataStore.

Every app that uses this library generates a unique encryption key that remains within the secure environment, ensuring that sensitive data never leaves the secure Android Keystore.

## Sample App 📱

The repository includes a sample app demonstrating:

* Library integration with Hilt
* Encryption/decryption operations
* Java and Kotlin usage examples
* Multiple data type storage

## Contributing 🤝

Contributions are welcome! Please feel free to submit a Pull Request.

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Troubleshooting 🔧

**Q: I get "Plugin not found" errors**
A: Ensure you have the Hilt plugin applied in your app's `build.gradle`:

```gradle
plugins {
    id 'dagger.hilt.android.plugin'
}
```

**Q: Encryption/decryption fails on older Android versions**
A: This library requires **API 24+** (Android 7.0+) for reliable Keystore operation. Make sure your minimum SDK is set to 24 or higher in `build.gradle`.
