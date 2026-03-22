package com.venom.lingolens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.venom.data.repo.SyncManager
import com.venom.domain.provider.AppConfigProvider
import com.venom.lingolens.ui.LingoLensApp
import com.venom.lingolens.viewmodel.LingoLensRootViewModel   // NEW
import com.venom.ui.screen.OnboardingScreens
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.viewmodel.OnboardingViewModel
import com.venom.ui.viewmodel.SettingsViewModel
import com.venom.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// REMOVED:
//   import androidx.compose.runtime.collectAsState       ← use collectAsStateWithLifecycle
//   import androidx.compose.runtime.mutableStateOf       ← replaced by StateFlow in ViewModel
//   import androidx.compose.runtime.remember             ← no longer needed for showOnboarding

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ── ViewModels ──────────────────────────────────────────────────

    // NEW: owns showOnboarding as StateFlow (survives rotation)
    // OLD: private val showOnboarding = mutableStateOf(false)
    //      └─ recreated on every rotation → onboarding resets mid-flow
    private val rootViewModel:       LingoLensRootViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel    by viewModels()

    @Inject lateinit var syncManager:       SyncManager
    @Inject lateinit var appConfigProvider: AppConfigProvider

    // ── Media launchers ─────────────────────────────────────────────

    private var currentPhotoUri:    Uri?              = null
    private var pendingUriCallback: ((Uri?) -> Unit)? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        pendingUriCallback?.invoke(uri)
        pendingUriCallback = null
    }

    private val documentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        pendingUriCallback?.invoke(uri)
        pendingUriCallback = null
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val resultUri = if (success) currentPhotoUri else null
        pendingUriCallback?.invoke(resultUri)
        pendingUriCallback = null
        currentPhotoUri   = null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) showToast("Notification permission denied.")
    }

    // ── onCreate ────────────────────────────────────────────────────

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(syncManager)

        // Replaces: showOnboarding.value = intent.getBooleanExtra(...)
        // ViewModel reads intent once; StateFlow survives rotation.
        rootViewModel.checkOnboarding(intent)

        lifecycleScope.launch { appConfigProvider.initialize() }

        setContent {
            val userPrefs  = settingsViewModel.uiState.collectAsStateWithLifecycle().value
            val themePrefs = userPrefs.themePrefs

            // NEW: observe StateFlow — no more `remember { showOnboarding }`
            val showOnboarding by rootViewModel.showOnboarding.collectAsStateWithLifecycle()

            ApplySelectedLanguage(userPrefs.appLanguage.code)

            val windowSizeClass = calculateWindowSizeClass(this)
            @Suppress("UNUSED_VARIABLE")
            val useNavRail = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

            LingoLensTheme(
                primaryColor    = Color(themePrefs.primaryColor.color),
                isAmoledBlack   = themePrefs.isAmoledBlack,
                materialYou     = themePrefs.materialYou,
                appTheme        = themePrefs.appTheme,
                colorStyle      = themePrefs.colorStyle,
                fontFamilyStyle = themePrefs.fontFamily,
            ) {
                if (showOnboarding) {
                    OnboardingScreens(
                        onGetStarted = {
                            onboardingViewModel.restoreUserProgress {
                                rootViewModel.completeOnboarding()
                            }
                        },
                        onSkip = {
                            onboardingViewModel.restoreUserProgress {
                                rootViewModel.completeOnboarding()
                            }
                        },
                        onGoogleSignIn = {
                            startGoogleSignIn(isFromOnboarding = true)
                        },
                    )
                } else {
                    LingoLensApp(
                        startCamera    = ::startCamera,
                        imageSelector  = ::selectImageFromGallery,
                        fileSelector   = ::selectDocumentFromFileManager,
                        onGoogleSignIn = { startGoogleSignIn(isFromOnboarding = false) },
                    )
                    setupPermissions()
                }
            }
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────

    @Composable
    private fun ApplySelectedLanguage(languageCode: String) {
        val locale = if (languageCode.isEmpty()) Locale.getDefault() else Locale(languageCode)
        val config = LocalConfiguration.current
        config.setLocale(locale)
        val context = LocalContext.current
        context.createConfigurationContext(config)
    }

    private fun selectImageFromGallery(callback: (Uri?) -> Unit) {
        pendingUriCallback = callback
        galleryLauncher.launch("image/*")
    }

    private fun selectDocumentFromFileManager(callback: (Uri?) -> Unit) {
        pendingUriCallback = callback
        documentLauncher.launch(arrayOf("image/*", "application/pdf"))
    }

    private fun startCamera(callback: (Uri?) -> Unit) {
        pendingUriCallback = callback
        val photoFile = createImageCacheFile()
        val photoUri  = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
        currentPhotoUri = photoUri
        cameraLauncher.launch(photoUri)
    }

    private fun createImageCacheFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir   = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${stamp}_", ".jpg", dir).apply { deleteOnExit() }
    }

    private fun setupPermissions() { askNotificationPermission() }

    private fun runtimeEnableAutoInit() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Firebase.messaging.isAutoInitEnabled = true
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to enable Firebase auto-init", e)
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> Unit

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                    showToast("This app needs notification permission for important updates")

                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startGoogleSignIn(isFromOnboarding: Boolean = false) {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("482771743461-er7fil93cgv5tcf9t6m28c2sahb2iium.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result     = credentialManager.getCredential(request = request, context = this@MainActivity)
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    if (isFromOnboarding) {
                        onboardingViewModel.onGoogleSignInResult(idToken) {
                            rootViewModel.completeOnboarding()
                        }
                    } else {
                        onboardingViewModel.onGoogleSignInResult(idToken) {}
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Credential Manager failed", e)
                showToast("Sign-in failed: ${e.message}")
            }
        }
    }
}