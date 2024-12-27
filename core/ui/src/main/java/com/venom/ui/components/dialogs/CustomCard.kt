package com.venom.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A highly customizable dialog component with sensible defaults
 *
 * @param modifier Modifier to be applied to the dialog's content
 * @param shape Shape of the dialog's card
 * @param containerColor Background color of the dialog
 * @param elevation Elevation of the dialog card
 * @param contentAlignment Horizontal alignment of the dialog's content
 * @param content Composable content of the dialog
 */
@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    elevation: Dp = 5.dp,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation, pressedElevation = elevation + 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = contentAlignment,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}