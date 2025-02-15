package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.QuizOption

@Composable
fun OptionsList(
    options: List<String>,
    selectedOption: String?,
    isAnswered: Boolean,
    correctAnswer: String?,
    onOptionSelected: (String) -> Unit
) {
    val labels = listOf("A", "B", "C", "D", "E")

    val quizOptions = options.mapIndexed { index, text ->
        QuizOption(
            label = labels[index],
            text = text,
            isSelected = text == selectedOption,
            isCorrect = text == correctAnswer
        )
    }

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        quizOptions.forEach { option ->
            OptionItem(
                option = option,
                isAnswered = isAnswered,
                onClick = { onOptionSelected(option.text) },

                )
        }
    }
}
