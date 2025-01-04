package com.venom.dialog.data.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.venom.domain.model.LanguageItem
import java.util.UUID

data class DialogMessage(
    val id: String = UUID.randomUUID().toString(),
    val sourceText: String,
    val translatedText: String = "",
    val sourceLanguage: LanguageItem,
    val targetLanguage: LanguageItem,
    val timestamp: Long = System.currentTimeMillis(),
    val isSender: Boolean = true
) {
    companion object {
        fun getSaver(): Saver<List<DialogMessage>, *> =
            listSaver(save = { messages: List<DialogMessage> ->
                messages.map { message ->
                    listOf(
                        message.id,
                        message.sourceText,
                        message.translatedText,
                        message.sourceLanguage.code,
                        message.sourceLanguage.name,
                        message.targetLanguage.code,
                        message.targetLanguage.name,
                        message.timestamp.toString(),
                        message.isSender.toString()
                    )
                }
            }, restore = { savedMessages: List<List<String>> ->
                savedMessages.map { saved ->
                    DialogMessage(
                        id = saved[0],
                        sourceText = saved[1],
                        translatedText = saved[2],
                        sourceLanguage = LanguageItem(
                            code = saved[3], name = saved[4]
                        ),
                        targetLanguage = LanguageItem(
                            code = saved[5], name = saved[6]
                        ),
                        timestamp = saved[7].toLong(),
                        isSender = saved[8].toBoolean()
                    )
                }
            })
    }
}
