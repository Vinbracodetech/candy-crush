package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val levelId: Int,
    val stars: Int = 0,
    val highScore: Int = 0,
    val isUnlocked: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey val id: Int = 1,
    val lives: Int = 5,
    val maxLives: Int = 5,
    val lastLifeTimestamp: Long = System.currentTimeMillis(),
    val coins: Int = 150,
    val hammerBoosters: Int = 3,
    val swapBoosters: Int = 3,
    val colorBombBoosters: Int = 2,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val lastDailySpinDate: Long = 0L,
    val totalAdsWatched: Int = 0,
    val totalScore: Long = 0L
)

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress ORDER BY levelId ASC")
    fun getAllProgress(): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getProgressForLevel(levelId: Int): LevelProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: LevelProgress)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<LevelProgress>)
}

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getProfile(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfileSync(): PlayerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: PlayerProfile)
}

@Database(
    entities = [LevelProgress::class, PlayerProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun playerProfileDao(): PlayerProfileDao
}
