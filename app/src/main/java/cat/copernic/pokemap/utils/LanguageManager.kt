package cat.copernic.pokemap.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import cat.copernic.pokemap.MyApp
import cat.copernic.pokemap.presentation.viewModel.UsersViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.io.InputStreamReader

object LanguageManager {

    private val translations = mutableStateOf<Map<String, String>>(emptyMap()) // Store translations
    val viewModel: UsersViewModel = UsersViewModel()
    private val userLang = viewModel.user.value?.language

    fun setLanguage(context: Context) {
        Log.d("LanguageManager", "language from user:${userLang}")
        var userUid = FirebaseAuth.getInstance().currentUser?.uid

        if (userUid != null) {
            viewModel.fetchUserByUid(userUid)
        }
        val userLanguage = viewModel.user.value?.language

        if (userLanguage != null) {
            MyApp.prefs.saveString("PREF_LANGUAGE_KEY", userLanguage)
        }else if(MyApp.prefs.getString("PREF_LANGUAGE_KEY") != null){
            loadTranslations(context, MyApp.prefs.getString("PREF_LANGUAGE_KEY")?:"es")
        }
        else{
            val initialLanguage : String = context.resources.configuration.locales[0].language
            MyApp.prefs.saveString("PREF_LANGUAGE_KEY",initialLanguage)
        }
        // Load translations dynamically
        val storedLang = MyApp.prefs.getString("PREF_LANGUAGE_KEY")
        Log.d("LanguageManager", "Stored language after update: $storedLang")

        val languageCode:String = MyApp.prefs.getString("PREF_LANGUAGE_KEY").toString()

        loadTranslations(context, languageCode)
    }

    private fun loadTranslations(context: Context, languageCode: String) {
        val fileName = when (languageCode) {
            "ca" -> "lang_ca.json"
            "es" -> "lang_es.json"
            else -> "lang_en.json"
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

    fun setLanguageInit(context: Context, lang : String) {
        MyApp.prefs.saveString("PREF_LANGUAGE_KEY",lang)
        loadTranslations(context, lang)
    }
}
