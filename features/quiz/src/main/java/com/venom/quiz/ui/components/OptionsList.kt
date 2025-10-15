package com.venom.quiz.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.venom.resources.R

@Composable
fun OptionsList(
    options: List<String>,
    selectedOption: String?,
    isAnswered: Boolean,
    correctAnswer: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val optionLabels = arrayOf(
            R.string.option_a,
            R.string.option_b,
            R.string.option_c,
            R.string.option_d
        )

        options.forEachIndexed { index, option ->
            OptionItem(
                option = option,
                label = stringResource(optionLabels[index.coerceIn(0, optionLabels.size - 1)]),
                isSelected = option == selectedOption,
                isCorrect = option == correctAnswer,
                isAnswered = isAnswered,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}