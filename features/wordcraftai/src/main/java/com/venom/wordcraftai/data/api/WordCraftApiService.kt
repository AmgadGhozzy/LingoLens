package com.venom.wordcraftai.data.api

import com.venom.wordcraftai.data.model.GenerateSentencesRequest
import com.venom.wordcraftai.data.model.GeneratedSentence
import com.venom.wordcraftai.data.model.ImageCaptionResponse
import com.venom.wordcraftai.data.model.ImageTagsResponse
import com.venom.wordcraftai.data.model.SentenceExplanationRequest
import com.venom.wordcraftai.data.model.SentenceExplanationResponse
import com.venom.wordcraftai.data.model.TranslationRequest
import com.venom.wordcraftai.data.model.TranslationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WordCraftApiService {
    @POST("ai/img-to-tags-gpt4o")
    suspend fun getImageTags(@Body request: Map<String, String>): ImageTagsResponse

    @POST("ai/img-to-caption")
    suspend fun getImageCaption(@Body request: Map<String, String>): ImageCaptionResponse

    @POST("ai/translate-single-word")
    suspend fun translateWord(@Body request: TranslationRequest): TranslationResponse

    @POST("ai/generate-sentences-v2")
    suspend fun generateSentences(@Body request: GenerateSentencesRequest): List<GeneratedSentence>

    @POST("ai/sentence-deep-explanation")
    suspend fun getSentenceExplanation(@Body request: SentenceExplanationRequest): SentenceExplanationResponse

    companion object {
        const val BASE_URL = "https://lingolens.com/api/"
    }
}