package com.venom.lingolens

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.venom.lingolens.ui.LingoLensApp
import com.venom.textsnap.ui.viewmodel.ImageInput.FromUri
import com.venom.textsnap.ui.viewmodel.OcrViewModel
import com.venom.ui.theme.LingoLensTheme
import com.venom.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ocrViewModel: OcrViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var currentPhotoUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val selectedLanguage = settingsViewModel.uiState.value.appLanguage

        @Composable
        fun UpdateLanguage(iso: String) {
            val locale = if (iso.isEmpty()) Locale.getDefault() else Locale(iso)
            val config = LocalConfiguration.current
            config.setLocale(locale)
            val res = LocalContext.current.resources
            res.updateConfiguration(config, res.displayMetrics)
        }
        setContent {

            val userPrefs = settingsViewModel.uiState.collectAsState().value
            val themePrefs = userPrefs.themePrefs
            UpdateLanguage(selectedLanguage.isoCode)
            LingoLensTheme(
                primaryColor = Color(themePrefs.primaryColor),
                isAmoledBlack = themePrefs.isAmoledBlack,
                materialYou = themePrefs.extractWallpaperColor,
                appTheme = themePrefs.appTheme,
                colorStyle = themePrefs.colorStyle,
                fontFamilyStyle = themePrefs.fontFamily
            ) {
                //AppNavigation()
                LingoLensApp(
                    ocrViewModel = ocrViewModel,
                    startCamera = { startCamera() },
                    imageSelector = { imageSelector.launch("image/*") }
                )
            }
        }
    }

    private val imageSelector =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                ocrViewModel.processImage(FromUri(uri), processOcrAfter = true)
            }
        }

    private val cameraCapture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoUri?.let { uri ->
                    ocrViewModel.processImage(FromUri(uri), processOcrAfter = false)
                }
            }
        }

    private fun startCamera() {
        val photoFile = createImageCacheFile()
        val photoUri = FileProvider.getUriForFile(
            this, "${packageName}.provider", photoFile
        )
        currentPhotoUri = photoUri
        cameraCapture.launch(photoUri)
    }

    private fun createImageCacheFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO_${timeStamp}_", ".jpg", storageDir).apply {
            deleteOnExit()
        }
    }
}