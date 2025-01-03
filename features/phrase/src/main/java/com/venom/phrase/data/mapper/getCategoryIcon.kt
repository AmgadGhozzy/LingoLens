package com.venom.phrase.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Work

fun getCategoryIcon(iconName: String) = when (iconName) {
    "Essentials" -> Icons.Rounded.Star
    "Travel" -> Icons.Rounded.FlightTakeoff
    "Medical" -> Icons.Rounded.LocalHospital
    "Hotel" -> Icons.Rounded.Hotel
    "Restaurant" -> Icons.Rounded.Restaurant
    "Bar" -> Icons.Rounded.LocalBar
    "Store" -> Icons.Rounded.Store
    "Work" -> Icons.Rounded.Work
    "TimeDate" -> Icons.Rounded.Schedule
    "Education" -> Icons.Rounded.School
    "Entertainment" -> Icons.Rounded.Movie
    "AroundTown" -> Icons.Rounded.Directions
    "CommonProblem" -> Icons.Rounded.Warning
    else -> Icons.Rounded.FormatListNumbered
}