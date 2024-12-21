package com.venom.data.model

import com.google.gson.annotations.SerializedName


data class OcrResponse(
    val google: OcrResult
)

data class OcrResult(
    val text: String,
    @SerializedName("bounding_boxes") val boundingBoxes: List<BoundingBox>,
    val status: String,
    @SerializedName("original_response") val originalResponse: OriginalResponse,
)

data class BoundingBox(
    val text: String, val left: Double, val top: Double, val width: Double, val height: Double
)

data class OriginalResponse(
    val textAnnotations: List<TextAnnotation>, val fullTextAnnotation: FullTextAnnotation
)

data class TextAnnotation(
    val description: String, @SerializedName("boundingPoly") val boundingBlock: BoundingBlock
)

data class ParagraphBox(
    val text: String, val boundingBlock: BoundingBlock, val avgWordHeight: Float = 24f
)


data class BoundingBlock(
    val vertices: List<Vertex>
)


data class Vertex(
    val x: Int, val y: Int
)

data class FullTextAnnotation(
    val text: String, val pages: List<Page>
)


data class Page(
    val blocks: List<Block>
)

data class Block(
    @SerializedName("boundingBox") val boundingBlock: BoundingBlock, val paragraphs: List<Paragraph>
)

data class Paragraph(
    @SerializedName("boundingBox") val boundingBlock: BoundingBlock, val words: List<Word>

)


data class Word(
    @SerializedName("boundingBox") val boundingBlock: BoundingBlock, val symbols: List<Symbol>
)

data class Symbol(
    val text: String
)
