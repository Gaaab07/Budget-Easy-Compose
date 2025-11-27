package com.budgeteasy

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.data.preferences.LanguageManager
import com.budgeteasy.data.preferences.ThemeManager
import com.budgeteasy.data.preferences.ThemeMode
import com.budgeteasy.presentation.theme.BudgetEasyTheme
import com.budgeteasy.presentation.ui.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by themeManager.themeMode.collectAsState(initial = ThemeMode.LIGHT)

            val darkTheme = themeMode == ThemeMode.DARK

            BudgetEasyTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {

        val languageManager = LanguageManager(newBase)


        val savedLanguage = runBlocking {
            try {
                languageManager.appLanguage.first()
            } catch (e: Exception) {
                e.printStackTrace()
                AppLanguage.SPANISH
            }
        }

        val context = languageManager.applyLanguage(newBase, savedLanguage)
        super.attachBaseContext(context)
    }
}
