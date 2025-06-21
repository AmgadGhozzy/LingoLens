package com.venom.lingolens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.venom.data.model.SplashPreferences
import com.venom.data.repo.SettingsRepository
import com.venom.resources.R
import com.venom.ui.theme.LingoLensTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var keepSplashOpened = true
    private var splashPrefs: SplashPreferences? = null

    companion object {
        private const val SPLASH_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up the splash screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { keepSplashOpened }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Load splash preferences
        lifecycleScope.launch {
            splashPrefs = settingsRepository.settings.first().splashPrefs

            // For Android versions below 12, show custom splash screen
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                setContent {
                    LingoLensTheme {
                        CustomSplashScreen(splashPrefs ?: SplashPreferences())
                    }
                }
            }

            // Handle splash screen timing and navigation
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToNextScreen()
            }, (splashPrefs?.splashAnimationDuration?.toLong() ?: SPLASH_DELAY))
        }
    }

    @Composable
    private fun CustomSplashScreen(splashPrefs: SplashPreferences) {
        val scale = remember { Animatable(0f) }
        val alpha = remember { Animatable(0f) }
        val backgroundColor = Color(splashPrefs.splashBackgroundColor)
        val textColor = Color(splashPrefs.splashTextColor)

        // Apply splash theme
        LaunchedEffect(Unit) {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Animate the splash screen
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "LingoLens Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale.value)
                            .alpha(alpha.value)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "LingoLens",
                        style = MaterialTheme.typography.headlineMedium,
                        color = textColor,
                        modifier = Modifier.alpha(alpha.value)
                    )
                }
            }
        }
    }

    private fun navigateToNextScreen() {
        keepSplashOpened = false

        // Check first launch status from DataStore
        lifecycleScope.launch {
            val isFirstLaunch = settingsRepository.isFirstLaunch()

            val intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                putExtra("show_onboarding", isFirstLaunch)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            if (isFirstLaunch) {
                // Mark that the app has been launched before
                settingsRepository.markFirstLaunchComplete()
            }

            startActivity(intent)
            finish()
        }
    }
}