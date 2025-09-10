package com.venom.data.mapper

import android.util.Log
import com.venom.data.remote.respnod.BoundingBlock
import com.venom.data.remote.respnod.OriginalResponse
import com.venom.data.remote.respnod.ParagraphBox
import com.venom.utils.Extensions.sanitize

object ParagraphBoxMapper {

    fun convertToParagraphBoxes(
        response: OriginalResponse, useFullText: Boolean = true
    ): List<ParagraphBox> = when (useFullText) {
        true -> convertFromFullText(response)
        false -> convertFromTextAnnotations(response)
    }

    private fun convertFromFullText(response: OriginalResponse): List<ParagraphBox> {
        return runCatching {
            response.fullTextAnnotation.pages.flatMap { page ->
                page.blocks.flatMap { block ->
                    block.paragraphs.map { paragraph ->
                        val paragraphText = paragraph.words.joinToString(" ") { word ->
                            word.symbols.joinToString("") { it.text }
                        }.sanitize()

                        val avgWordHeight =
                            paragraph.words.mapNotNull { it.boundingBlock.height() }.average()
                                .toFloat()

                        Log.d(
                            "BoundingBox",
                            "avgWordHeight: ${paragraph.words.mapNotNull { it.boundingBlock.height() }}"
                        )
                        ParagraphBox(
                            text = paragraphText,
                            boundingBlock = paragraph.boundingBlock,
                            avgWordHeight = avgWordHeight
                        )
                    }
                }
            }
        }.getOrElse { exception ->
            Log.e(
                "ParagraphBoxMapper",
                "Error converting from full text: ${exception.message}",
                exception
            )
            emptyList()
        }
    }

    private fun convertFromTextAnnotations(response: OriginalResponse): List<ParagraphBox> {
        return runCatching {
            response.textAnnotations.drop(1).map { annotation ->
                ParagraphBox(
                    text = annotation.description, boundingBlock = annotation.boundingBlock
                )
            }
        }.getOrElse { exception ->
            Log.e(
                "ParagraphBoxMapper",
                "Error converting from text annotations: ${exception.message}",
                exception
            )
            emptyList()
        }
    }

    private fun BoundingBlock.height(): Float? =
        vertices.takeIf { it.isNotEmpty() }?.let { points ->
            points.maxOfOrNull { it.y.toFloat() }?.let { maxY ->
                points.minOfOrNull { it.y.toFloat() }?.let { minY ->
                    maxY - minY
                }
            }
        }
}