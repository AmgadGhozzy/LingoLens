package com.venom.domain.model

import com.venom.resources.R

sealed class WordLevels(
    val range: LevelRange,
    val titleRes: Int,
    val descRes: Int,
    val id: String,
    val iconRes: Int  // Changed to use resource ID instead of ImageVector
) {
    object Beginner : WordLevels(
        range = LevelRange(1, 1000),
        titleRes = R.string.level_beginner,
        descRes = R.string.desc_beginner,
        id = "beginner",
        iconRes = R.drawable.ic_beginner_emoji_people
    )

    object Elementary : WordLevels(
        range = LevelRange(1001, 2000),
        titleRes = R.string.level_elementary,
        descRes = R.string.desc_elementary,
        id = "elementary",
        iconRes = R.drawable.ic_elementary_school
    )

    object Intermediate : WordLevels(
        range = LevelRange(2001, 3000),
        titleRes = R.string.level_intermediate,
        descRes = R.string.desc_intermediate,
        id = "intermediate",
        iconRes = R.drawable.ic_intermediate_auto_stories
    )

    object UpperIntermediate : WordLevels(
        range = LevelRange(3001, 4000),
        titleRes = R.string.level_upper_intermediate,
        descRes = R.string.desc_upper_intermediate,
        id = "upper_intermediate",
        iconRes = R.drawable.ic_upper_intermediate_psychology
    )

    object Advanced : WordLevels(
        range = LevelRange(4001, 5000),
        titleRes = R.string.level_advanced,
        descRes = R.string.desc_advanced,
        id = "advanced",
        iconRes = R.drawable.ic_advanced_architecture
    )

    object Mastery : WordLevels(
        range = LevelRange(5001, 6000),
        titleRes = R.string.level_mastery,
        descRes = R.string.desc_mastery,
        id = "mastery",
        iconRes = R.drawable.ic_mastery_trophy
    )

    data class LevelRange(val start: Int, val end: Int)

    companion object {
        fun values() = listOf(
            Beginner, Elementary, Intermediate, UpperIntermediate, Advanced, Mastery
        )
    }
}