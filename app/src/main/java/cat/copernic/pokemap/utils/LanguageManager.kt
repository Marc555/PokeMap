package cat.copernic.pokemap.utils


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import java.io.InputStreamReader

object LanguageManager {
    private lateinit var preferences: SharedPreferences
    private const val PREF_LANGUAGE_KEY = "app_language"
    private val translations = mutableStateOf<Map<String, String>>(emptyMap()) // Store translations

    // Current selected language
    var currentLanguage = mutableStateOf("es")
        private set

    fun init(context: Context) {
        preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedLanguage = preferences.getString(PREF_LANGUAGE_KEY, "es") ?: "es"
        setLanguage(context, savedLanguage)

    }

    fun setLanguage(context: Context, languageCode: String) {
        currentLanguage.value = languageCode
        preferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply()

        // Load translations dynamically
        loadTranslations(context, languageCode)
    }

    private fun loadTranslations(context: Context, languageCode: String) {
        val fileName = when (languageCode) {
            "ca" -> "lang_ca.json"
            "en" -> "lang_en.json"
            else -> "lang_es.json"
        }

        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val mapType = object : com.google.gson.reflect.TypeToken<Map<String, String>>() {}.type
            val translationsMap: Map<String, String> = Gson().fromJson(reader, mapType)
            translations.value = translationsMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getText(key: String): String {
        return translations.value[key] ?: key // Default to the key if missing
    }
}
