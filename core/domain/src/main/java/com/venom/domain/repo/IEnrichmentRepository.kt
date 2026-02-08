package com.venom.domain.repo

import com.venom.domain.model.WordMaster

interface IEnrichmentRepository {

    val enrichmentVersion: String

    suspend fun enrichAndGetWords(size: Int = 5): List<WordMaster>
    suspend fun generateWords(prompt: String): Result<List<WordMaster>>
}
