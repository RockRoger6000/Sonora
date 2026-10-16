package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.SonoraApp
import com.example.ui.SonoraViewModel
import com.example.ui.theme.SonoraTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: SonoraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val language by viewModel.language.collectAsState()

            updateAppLocale(language)

            SonoraTheme(themeMode = themeMode) {
                SonoraApp(viewModel = viewModel)
            }
        }
    }

    private fun updateAppLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
