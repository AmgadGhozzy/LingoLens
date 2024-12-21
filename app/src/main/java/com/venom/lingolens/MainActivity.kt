package com.venom.lingolens

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import com.venom.lingolens.ui.LingoLensApp
import com.venom.textsnap.ui.screens.OcrViewModel
import com.venom.ui.theme.LingoLensTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val ocrViewModel: OcrViewModel by viewModels()
    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LingoLensTheme {
                updateStatusBarColor(MaterialTheme.colorScheme.background, !isSystemInDarkTheme())
                LingoLensApp(
                    ocrViewModel = ocrViewModel,
                    startCamera = { startCamera() },
                    imageSelector = { imageSelector.launch("image/*") })
            }
        }
    }

    private val imageSelector =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                ocrViewModel.setUri(it)
                ocrViewModel.processImage()
            }
        }

    private val cameraCapture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoUri?.let { uri ->
                    ocrViewModel.setUri(uri)
                    ocrViewModel.processImage()
                }
            }
        }

    private fun updateStatusBarColor(color: Color, isDarkTheme: Boolean) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = isDarkTheme
        windowInsetsController.isAppearanceLightNavigationBars = isDarkTheme
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color.toArgb()
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