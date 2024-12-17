package com.venom.textsnap.utils

import android.util.Log
import com.venom.textsnap.data.model.BoundingBlock
import com.venom.textsnap.data.model.OriginalResponse
import com.venom.textsnap.data.model.ParagraphBox
import com.venom.textsnap.utils.Extensions.sanitize

fun convertToParagraphBoxes(
    response: OriginalResponse,
    useFullText: Boolean = true
): List<ParagraphBox> = when (useFullText) {
    true -> convertFromFullText(response)
    false -> convertFromTextAnnotations(response)
}

private fun convertFromFullText(response: OriginalResponse): List<ParagraphBox> {
    return response.fullTextAnnotation.pages.flatMap { page ->
        page.blocks.flatMap { block ->
            block.paragraphs.map { paragraph ->
                val paragraphText = paragraph.words.joinToString(" ") { word ->
                    word.symbols.joinToString("") { it.text }
                }.sanitize()

                val avgWordHeight = paragraph.words
                    .map { it.boundingBlock.height() }
                    .average()
                    .toFloat()
                Log.d("BoundingBox", "avgWordHeight: ${paragraph.words.map { it.boundingBlock.height() }}")
                ParagraphBox(
                    text = paragraphText,
                    boundingBlock = paragraph.boundingBlock,
                    avgWordHeight = avgWordHeight
                )
            }
        }
    }
}

private fun convertFromTextAnnotations(response: OriginalResponse): List<ParagraphBox> {
    return response.textAnnotations
        .drop(1)
        .map { annotation ->
            ParagraphBox(
                text = annotation.description,
                boundingBlock = annotation.boundingBlock
            )
        }
}

fun BoundingBlock.height(): Float =
    vertices.takeIf { it.isNotEmpty() }?.let { points ->
        points.maxOf { it.y.toFloat() } - points.minOf { it.y.toFloat() }
    } ?: 0f