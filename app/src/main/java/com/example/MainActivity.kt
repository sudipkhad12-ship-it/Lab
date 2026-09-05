package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.LabPreferences
import com.example.ui.screens.MainLabScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacySecurityScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val prefs = LabPreferences(this)

    setContent {
      var currentThemePref by remember { mutableStateOf(prefs.themeMode) }
      val isDark = when (currentThemePref.uppercase()) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
      }

      var showSplash by remember { mutableStateOf(true) }
      var hasCompletedOnboarding by remember { mutableStateOf(prefs.hasCompletedOnboarding) }
      var hasAcceptedPrivacy by remember { mutableStateOf(prefs.hasAcceptedPrivacy) }
      var reviewingPrivacyFromSettings by remember { mutableStateOf(false) }

      MyApplicationTheme(darkTheme = isDark) {
        Surface(modifier = Modifier.fillMaxSize()) {
          when {
            showSplash -> {
              SplashScreen(
                onTimeout = {
                  showSplash = false
                }
              )
            }
            !hasCompletedOnboarding -> {
              OnboardingScreen(
                onFinish = {
                  prefs.hasCompletedOnboarding = true
                  hasCompletedOnboarding = true
                }
              )
            }
            !hasAcceptedPrivacy || reviewingPrivacyFromSettings -> {
              PrivacySecurityScreen(
                onAccepted = {
                  prefs.hasAcceptedPrivacy = true
                  hasAcceptedPrivacy = true
                  reviewingPrivacyFromSettings = false
                }
              )
            }
            else -> {
              MainLabScreen(
                prefs = prefs,
                currentTheme = currentThemePref,
                onThemeChanged = { newTheme ->
                  currentThemePref = newTheme
                  prefs.themeMode = newTheme
                },
                onReviewPrivacy = {
                  reviewingPrivacyFromSettings = true
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
