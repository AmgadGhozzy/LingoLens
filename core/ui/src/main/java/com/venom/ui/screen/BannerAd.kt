package com.venom.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.venom.data.AdsManager

@Composable
fun BannerAd(
    adUnitId: String = AdsManager.Companion.TEST_AD_UNIT_ID, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Calculate ad size based on screen width
    val adSize = remember(density) {
        val displayMetrics = context.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val adWidth = (widthPixels / density.density).toInt()

        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    // Initialize AdView
    val adView = remember {
        AdView(context).apply {
            this.adUnitId = adUnitId
            this.setAdSize(adSize)
        }
    }

    // Lifecycle management
    DisposableEffect(key1 = true) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        onDispose {
            adView.destroy()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { adView }, modifier = Modifier.fillMaxWidth()
        )
    }
}