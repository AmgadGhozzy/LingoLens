package com.venom.domain.srs

import com.venom.domain.model.KnownState
import com.venom.domain.model.UserWordProgress

/**
 * Spaced Repetition System engine.
 * Calculates stability and schedules reviews based on learning science.
 */
class SrsEngine {
    
    companion object {
        private const val MASTERY_PRODUCTION_THRESHOLD = 3
        private const val MASTERY_RECALL_THRESHOLD = 3
        private const val MASTERY_STABILITY_THRESHOLD = 2.0f
    }
    
    /**
     * Calculate word stability based on static and dynamic factors.
     */
    fun calculateStability(
        rank: Int,
        frequency: Int,
        productionSuccess: Int,
        recallSuccess: Int,
        lapsesCount: Int
    ): Float {
        val base = (1000f - rank.coerceAtMost(1000)) / 1000f * 0.3f + 
                   frequency.coerceIn(1, 6) / 6f * 0.2f
        
        return (base +
            productionSuccess * 2.0f +
            recallSuccess * 1.2f -
            lapsesCount * 1.5f)
            .coerceAtLeast(0f)
    }
    
    /**
     * Calculate next review time in milliseconds.
     */
    fun calculateNextReview(stability: Float, difficultyScore: Int): Long {
        val baseIntervalHours = when {
            stability < 1f -> 4       // 4 hours
            stability < 2f -> 24      // 1 day
            stability < 4f -> 72      // 3 days
            stability < 6f -> 168     // 1 week
            else -> 336               // 2 weeks
        }
        
        val difficultyFactor = 1.0f - (difficultyScore.coerceIn(1, 10) / 20f)
        val intervalMs = (baseIntervalHours * 60 * 60 * 1000 * difficultyFactor).toLong()
        
        return System.currentTimeMillis() + intervalMs
    }
    
    /**
     * Determine new learning state based on progress.
     */
    fun updateKnownState(progress: UserWordProgress): KnownState {
        return when {
            progress.productionSuccess >= MASTERY_PRODUCTION_THRESHOLD &&
            progress.recallSuccess >= MASTERY_RECALL_THRESHOLD &&
            progress.stability >= MASTERY_STABILITY_THRESHOLD -> KnownState.MASTERED
            
            progress.productionSuccess >= 1 || progress.recallSuccess >= 1 -> KnownState.KNOWN
            
            progress.viewCount > 0 -> KnownState.LEARNING
            
            else -> KnownState.SEEN
        }
    }
    
    /**
     * Process a successful recall event.
     */
    fun onRecallSuccess(progress: UserWordProgress, rank: Int, frequency: Int): UserWordProgress {
        val newRecallSuccess = progress.recallSuccess + 1
        val newStability = calculateStability(
            rank, frequency, progress.productionSuccess, newRecallSuccess, progress.lapsesCount
        )
        
        return progress.copy(
            recallSuccess = newRecallSuccess,
            stability = newStability,
            lastReview = System.currentTimeMillis(),
            nextReview = calculateNextReview(newStability, 5), // Default difficulty
            knownState = updateKnownState(progress.copy(recallSuccess = newRecallSuccess, stability = newStability))
        )
    }
    
    /**
     * Process a failed recall event.
     */
    fun onRecallFail(progress: UserWordProgress): UserWordProgress {
        val newLapses = progress.lapsesCount + 1
        val reducedStability = (progress.stability - 1.5f).coerceAtLeast(0f)
        
        return progress.copy(
            recallFail = progress.recallFail + 1,
            lapsesCount = newLapses,
            stability = reducedStability,
            lastReview = System.currentTimeMillis(),
            nextReview = calculateNextReview(reducedStability, 8), // Harder difficulty
            knownState = if (progress.knownState == KnownState.MASTERED) KnownState.KNOWN else progress.knownState
        )
    }
    
    /**
     * Process production success (spelling, writing).
     */
    fun onProductionSuccess(progress: UserWordProgress, rank: Int, frequency: Int): UserWordProgress {
        val newProductionSuccess = progress.productionSuccess + 1
        val newStability = calculateStability(
            rank, frequency, newProductionSuccess, progress.recallSuccess, progress.lapsesCount
        )
        
        return progress.copy(
            productionSuccess = newProductionSuccess,
            practiceCompleted = progress.practiceCompleted + 1,
            stability = newStability,
            lastReview = System.currentTimeMillis(),
            nextReview = calculateNextReview(newStability, 4),
            knownState = updateKnownState(progress.copy(productionSuccess = newProductionSuccess, stability = newStability))
        )
    }
}
