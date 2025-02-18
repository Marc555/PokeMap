package cat.copernic.pokemap.utils
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val biometricPrefs = EncryptedSharedPreferences.create(
        context,
        "biometric_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveBiometricCredentials(email: String, password: String) {
        val storedEmail = biometricPrefs.getString("email", null)

        if (storedEmail == null) { // ✅ Only store the first registered user
            biometricPrefs.edit().apply {
                putString("email", email)
                putString("password", password)
                apply()
            }
        }
    }

    fun getBiometricEmail(): String? {
        return biometricPrefs.getString("email", null)
    }

    fun getBiometricPassword(): String? {
        return biometricPrefs.getString("password", null)
    }

    fun clearBiometricCredentials() {
        biometricPrefs.edit().clear().apply()
    }

    fun saveGoogleAuthToken(token: String) {
        sharedPreferences.edit().putString("google_auth_token", token).apply()
    }

    fun getGoogleAuthToken(): String? {
        return sharedPreferences.getString("google_auth_token", null)
    }

    fun clearGoogleAuthToken() {
        sharedPreferences.edit().remove("google_auth_token").apply()
    }

    // ✅ Remove a specific key
    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }
    // ✅ Clear all data
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun editKey(oldKey: String, newKey: String) {
        val value = sharedPreferences.all[oldKey] // ✅ Retrieve value from the old key
        if (value != null) {
            when (value) {
                is String -> saveString(newKey, value)
                is Boolean -> saveBoolean(newKey, value)
                is Int -> saveInt(newKey, value)
                is Float -> sharedPreferences.edit().putFloat(newKey, value).apply()
                is Long -> sharedPreferences.edit().putLong(newKey, value).apply()
                else -> return // Do nothing if the type is unsupported
            }
            removeKey(oldKey) // ✅ Remove the old key
        }
    }

}
