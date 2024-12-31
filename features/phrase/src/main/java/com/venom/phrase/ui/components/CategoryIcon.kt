package com.venom.phrase.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CategoryIcon(icon: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        modifier = Modifier.size(52.dp)
    ) {
        Icon(
            imageVector = getCategoryIcon(icon),
            contentDescription = null,
            modifier = Modifier.padding(14.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun getCategoryIcon(iconName: String) = when (iconName) {
    "categoryEssentials" -> Icons.Rounded.Star
    "categoryTraveling" -> Icons.Rounded.FlightTakeoff
    "categoryMedical" -> Icons.Rounded.LocalHospital
    "categoryHotel" -> Icons.Rounded.Hotel
    "categoryRestaurant" -> Icons.Rounded.Restaurant
    else -> Icons.AutoMirrored.Default.FormatListBulleted
}