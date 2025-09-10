package com.venom.domain.model

import androidx.compose.ui.graphics.Color
import com.venom.resources.R

sealed class WordLevels(
    val range: LevelRange,
    val titleRes: Int,
    val descRes: Int,
    val id: String,
    val iconRes: Int,
    val color: Color
) {
    object Beginner : WordLevels(
        range = LevelRange(1, 1000),
        titleRes = R.string.level_beginner,
        descRes = R.string.desc_beginner,
        id = "beginner",
        iconRes = R.drawable.ic_beginner_seedling,
        color = Color(0xFF3B82F6)
    )

    object Elementary : WordLevels(
        range = LevelRange(1001, 2000),
        titleRes = R.string.level_elementary,
        descRes = R.string.desc_elementary,
        id = "elementary",
        iconRes = R.drawable.ic_elementary_school,
        color = Color(0xFF10B981)
    )

    object Intermediate : WordLevels(
        range = LevelRange(2001, 3000),
        titleRes = R.string.level_intermediate,
        descRes = R.string.desc_intermediate,
        id = "intermediate",
        iconRes = R.drawable.ic_intermediate_book,
        color = Color(0xFF8B5CF6)
    )

    object UpperIntermediate : WordLevels(
        range = LevelRange(3001, 4000),
        titleRes = R.string.level_upper_intermediate,
        descRes = R.string.desc_upper_intermediate,
        id = "upper_intermediate",
        iconRes = R.drawable.ic_upper_intermediate_brain,
        color = Color(0xFFF59E0B)
    )

    object Advanced : WordLevels(
        range = LevelRange(4001, 5000),
        titleRes = R.string.level_advanced,
        descRes = R.string.desc_advanced,
        id = "advanced",
        iconRes = R.drawable.ic_advanced_column,
        color = Color(0xFF50AF95)
    )

    object Mastery : WordLevels(
        range = LevelRange(5001, 6000),
        titleRes = R.string.level_mastery,
        descRes = R.string.desc_mastery,
        id = "mastery",
        iconRes = R.drawable.ic_mastery_trophy,
        color = Color(0xFF6366F1)
    )

    data class LevelRange(val start: Int, val end: Int)

    companion object {
        fun values() = listOf(
            Beginner, Elementary, Intermediate, UpperIntermediate, Advanced, Mastery
        )
    }
}