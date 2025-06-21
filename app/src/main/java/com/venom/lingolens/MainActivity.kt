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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.venom.lingolens.ui.LingoLensApp
import com.venom.resources.R
import com.venom.settings.presentation.screen.UpdateScreen
import com.venom.textsnap.ui.viewmodel.ImageInput.FromUri
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.screen.OnboardingScreens
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.viewmodel.SettingsViewModel
import com.venom.ui.viewmodel.UpdateViewModel
import com.venom.utils.Extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // ViewModels
    private val ocrViewModel: OcrViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val updateViewModel: UpdateViewModel by viewModels()

    // UI State
    private val showUpdateDialog = mutableStateOf(false)
    private val showOnboarding = mutableStateOf(false)

    // Camera and file handling
    private var currentPhotoUri: Uri? = null
    private lateinit var remoteConfig: FirebaseRemoteConfig

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if we should show onboarding
        showOnboarding.value = intent.getBooleanExtra("show_onboarding", false)

        setupPermissions()
        setupRemoteConfig()

        setContent {
            val userPrefs = settingsViewModel.uiState.collectAsState().value
            val themePrefs = userPrefs.themePrefs
            val showDialog = remember { showUpdateDialog }
            val shouldShowOnboarding = remember { showOnboarding }

            ApplySelectedLanguage(userPrefs.appLanguage.code)

            LingoLensTheme(
                primaryColor = Color(themePrefs.primaryColor.color),
                isAmoledBlack = themePrefs.isAmoledBlack,
                materialYou = themePrefs.extractWallpaperColor,
                appTheme = themePrefs.appTheme,
                colorStyle = themePrefs.colorStyle,
                fontFamilyStyle = themePrefs.fontFamily
            ) {
                if (shouldShowOnboarding.value) {
                    OnboardingScreens(
                        onGetStarted = {
                            shouldShowOnboarding.value = false
                        },
                        onSkip = {
                            shouldShowOnboarding.value = false
                        }
                    )
                } else {
                    LingoLensApp(
                        ocrViewModel = ocrViewModel,
                        startCamera = { startCamera() },
                        imageSelector = { selectImageFromGallery() },
                        fileSelector = { selectDocumentFromFileManager() }
                    )
                }

                if (showDialog.value) {
                    UpdateScreen(
                        viewModel = updateViewModel,
                        onDismiss = {
                            showDialog.value = false
                            finish()
                        }
                    )
                }
            }

            CheckForForceUpdate()
        }
    }

    @Composable
    private fun ApplySelectedLanguage(languageCode: String) {
        val locale = if (languageCode.isEmpty()) Locale.getDefault() else Locale(languageCode)
        val config = LocalConfiguration.current
        config.setLocale(locale)
        val res = LocalContext.current.resources
        res.updateConfiguration(config, res.displayMetrics)
    }

    @Composable
    private fun CheckForForceUpdate() {
        LaunchedEffect(key1 = Unit) {
            updateViewModel.state.collectLatest { updateState ->
                if (updateState.isForceUpdate) {
                    showUpdateDialog.value = true
                }
            }
        }
    }

    private fun setupPermissions() {
        askNotificationPermission()
    }

    private fun setupRemoteConfig() {
        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            fetchTimeoutInSeconds = 10
        }

        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }

        fetchAndActivateRemoteConfig()
        runtimeEnableAutoInit()
        addConfigUpdateListener()
    }

    // Image selection from gallery
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            ocrViewModel.processImage(FromUri(uri), processOcrAfter = true)
        }
    }

    // Document selection from file manager
    private val documentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            // Request persistent permission to access the file
            contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            ocrViewModel.processImage(FromUri(uri), processOcrAfter = true)
        }
    }

    // Camera capture
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                ocrViewModel.processImage(FromUri(uri), processOcrAfter = false)
            }
        }
    }

    private fun selectImageFromGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun selectDocumentFromFileManager() {
        documentLauncher.launch(arrayOf("image/*", "application/pdf"))
    }

    private fun startCamera() {
        val photoFile = createImageCacheFile()
        val photoUri = FileProvider.getUriForFile(
            this, "${packageName}.provider", photoFile
        )
        currentPhotoUri = photoUri
        cameraLauncher.launch(photoUri)
    }

    private fun createImageCacheFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${timeStamp}_", ".jpg", storageDir).apply {
            deleteOnExit()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showToast("Notification permission denied. Your app will not show notifications.")
        }
    }

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
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showToast("This app needs notification permission for important updates")
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun fetchAndActivateRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val message = remoteConfig.getString("welcome_message")
                    Log.d("RemoteConfig", "Config fetched successfully: $message")
                } else {
                    Log.e("RemoteConfig", "Fetch failed", task.exception)
                }
            }
    }

    private fun addConfigUpdateListener() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("RemoteConfig", "Updated keys: ${configUpdate.updatedKeys}")

                if (configUpdate.updatedKeys.contains("welcome_message")) {
                    remoteConfig.activate().addOnCompleteListener {
                        val message = remoteConfig.getString("welcome_message")
                        Log.d("RemoteConfig", "New Welcome Message: $message")
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w("RemoteConfig", "Config update error: ${error.code}", error)
            }
        })
    }
}