package com.venom.stackcard.ui.screen.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun OptionsList(
    options: List<String>,
    selectedOption: String?,
    isAnswered: Boolean,
    correctAnswer: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEachIndexed { index, option ->
            OptionItem(
                option = option,
                label = ('A' + index).toString(),
                isSelected = option == selectedOption,
                isCorrect = option == correctAnswer,
                isAnswered = isAnswered,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}
