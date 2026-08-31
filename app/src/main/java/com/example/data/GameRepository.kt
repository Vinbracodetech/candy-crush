package com.example.data

import com.example.data.local.LevelProgress
import com.example.data.local.LevelProgressDao
import com.example.data.local.PlayerProfile
import com.example.data.local.PlayerProfileDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(
    private val levelDao: LevelProgressDao,
    private val profileDao: PlayerProfileDao
) {
    val allLevelProgress: Flow<List<LevelProgress>> = levelDao.getAllProgress()
    val playerProfile: Flow<PlayerProfile?> = profileDao.getProfile()

    suspend fun initializeIfEmpty() {
        val currentProfile = profileDao.getProfileSync()
        if (currentProfile == null) {
            profileDao.insertOrUpdate(PlayerProfile())
        }
        val existingLevels = levelDao.getAllProgress().firstOrNull() ?: emptyList()
        if (existingLevels.size < 350) {
            val defaultLevels = (1..350).map {
                val existing = existingLevels.find { l -> l.levelId == it }
                existing ?: LevelProgress(
                    levelId = it,
                    stars = 0,
                    highScore = 0,
                    isUnlocked = it == 1 || existingLevels.any { l -> l.levelId == it - 1 && l.isUnlocked }
                )
            }
            levelDao.insertAll(defaultLevels)
        }
    }

    suspend fun completeLevel(levelId: Int, stars: Int, score: Int) {
        val current = levelDao.getProgressForLevel(levelId)
        val bestStars = maxOf(stars, current?.stars ?: 0)
        val bestScore = maxOf(score, current?.highScore ?: 0)

        levelDao.insertOrUpdate(
            LevelProgress(
                levelId = levelId,
                stars = bestStars,
                highScore = bestScore,
                isUnlocked = true,
                completedAt = System.currentTimeMillis()
            )
        )

        // Unlock next level if exists
        if (levelId < 350) {
            val nextLevel = levelDao.getProgressForLevel(levelId + 1)
            if (nextLevel == null || !nextLevel.isUnlocked) {
                levelDao.insertOrUpdate(
                    LevelProgress(
                        levelId = levelId + 1,
                        stars = nextLevel?.stars ?: 0,
                        highScore = nextLevel?.highScore ?: 0,
                        isUnlocked = true
                    )
                )
            }
        }

        // Reward coins for stars
        val profile = profileDao.getProfileSync() ?: PlayerProfile()
        val earnedCoins = stars * 15 + 25
        profileDao.insertOrUpdate(
            profile.copy(
                coins = profile.coins + earnedCoins,
                totalScore = profile.totalScore + score
            )
        )
    }

    suspend fun updateProfile(transform: (PlayerProfile) -> PlayerProfile) {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        profileDao.insertOrUpdate(transform(current))
    }

    suspend fun deductLife(): Boolean {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        if (current.lives > 0) {
            val newLives = current.lives - 1
            profileDao.insertOrUpdate(
                current.copy(
                    lives = newLives,
                    lastLifeTimestamp = if (current.lives == current.maxLives) System.currentTimeMillis() else current.lastLifeTimestamp
                )
            )
            return true
        }
        return false
    }

    suspend fun restoreLives(amount: Int = 5) {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        profileDao.insertOrUpdate(
            current.copy(
                lives = minOf(current.maxLives, current.lives + amount),
                lastLifeTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun useBooster(type: String): Boolean {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        return when (type) {
            "hammer" -> {
                if (current.hammerBoosters > 0) {
                    profileDao.insertOrUpdate(current.copy(hammerBoosters = current.hammerBoosters - 1))
                    true
                } else false
            }
            "swap" -> {
                if (current.swapBoosters > 0) {
                    profileDao.insertOrUpdate(current.copy(swapBoosters = current.swapBoosters - 1))
                    true
                } else false
            }
            "bomb" -> {
                if (current.colorBombBoosters > 0) {
                    profileDao.insertOrUpdate(current.copy(colorBombBoosters = current.colorBombBoosters - 1))
                    true
                } else false
            }
            else -> false
        }
    }

    suspend fun addBooster(type: String, count: Int = 1) {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        val updated = when (type) {
            "hammer" -> current.copy(hammerBoosters = current.hammerBoosters + count)
            "swap" -> current.copy(swapBoosters = current.swapBoosters + count)
            "bomb" -> current.copy(colorBombBoosters = current.colorBombBoosters + count)
            else -> current
        }
        profileDao.insertOrUpdate(updated)
    }

    suspend fun addCoins(amount: Int) {
        val current = profileDao.getProfileSync() ?: PlayerProfile()
        profileDao.insertOrUpdate(current.copy(coins = current.coins + amount))
    }

    suspend fun checkAndRegenerateLives() {
        val current = profileDao.getProfileSync() ?: return
        if (current.lives < current.maxLives) {
            val now = System.currentTimeMillis()
            val lifeIntervalMs = 15 * 60 * 1000L // 15 minutes per life
            val elapsed = now - current.lastLifeTimestamp
            val livesToRestore = (elapsed / lifeIntervalMs).toInt()
            if (livesToRestore > 0) {
                val newLives = minOf(current.maxLives, current.lives + livesToRestore)
                val remainingTime = elapsed % lifeIntervalMs
                profileDao.insertOrUpdate(
                    current.copy(
                        lives = newLives,
                        lastLifeTimestamp = now - remainingTime
                    )
                )
            }
        }
    }
}
