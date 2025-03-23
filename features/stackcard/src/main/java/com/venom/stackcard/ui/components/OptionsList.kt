package com.venom.stackcard.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.venom.stackcard.ui.viewmodel.QuizOption
import com.venom.utils.SoundManager

@Composable
fun OptionsList(
    options: List<String>,
    selectedOption: String?,
    isAnswered: Boolean,
    correctAnswer: String?,
    onOptionSelected: (String) -> Unit,
    playAnswerSounds: Boolean = true,
    onSpeakOptionClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val labels = listOf("A", "B", "C", "D", "E")

    val quizOptions = options.mapIndexed { index, text ->
        QuizOption(
            label = labels.getOrElse(index) { "?" },
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
                onClick = {
                    onOptionSelected(option.text)

                    // Play appropriate sound based on answer status
                    if (playAnswerSounds) {
                        if (option.text == correctAnswer) {
                            soundManager.playSound("right_answer")
                        } else {
                            soundManager.playSound("wrong_answer")
                        }
                    } else {
                        // Play a regular key sound for selection
                        soundManager.playSound("key_sound")
                    }
                },
                onSpeakClick = onSpeakOptionClick?.let { speakFn ->
                    { speakFn(option.text) }
                }
            )
        }
    }
}
