package com.venom.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.venom.ui.theme.BrandColors as B

@Immutable
data class SemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,

    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,

    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color
)

val LightSemanticColors = SemanticColors(
    success = B.Emerald600,
    onSuccess = B.White,
    successContainer = B.Emerald100,
    onSuccessContainer = B.Emerald800,

    warning = B.Amber600,
    onWarning = B.White,
    warningContainer = B.Amber100,
    onWarningContainer = B.Amber900,

    info = B.Blue600,
    onInfo = B.White,
    infoContainer = B.Blue100,
    onInfoContainer = B.Blue900
)

val DarkSemanticColors = SemanticColors(
    success = B.Emerald400,
    onSuccess = B.Slate950,
    successContainer = B.Emerald500.copy(alpha = 0.2f),
    onSuccessContainer = B.Emerald100,

    warning = B.Amber400,
    onWarning = B.Slate950,
    warningContainer = B.Amber500.copy(alpha = 0.1f),
    onWarningContainer = B.Amber100,

    info = B.Blue500,
    onInfo = B.Slate950,
    infoContainer = B.Blue500.copy(alpha = 0.2f),
    onInfoContainer = B.Blue100
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }
