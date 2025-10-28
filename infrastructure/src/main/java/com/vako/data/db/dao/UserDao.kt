package com.vako.data.db.dao

import androidx.room.*
import com.vako.data.db.entity.user.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // User
    @Query("SELECT * FROM `User` WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteBookEntity)

    @Query("DELETE FROM FavoriteBook WHERE userId = :userId AND bookId = :bookId")
    suspend fun removeFavorite(userId: String, bookId: String)

    @Query("SELECT bookId FROM FavoriteBook WHERE userId = :userId")
    suspend fun getFavoriteBookIds(userId: String): List<String>

    @Query("SELECT * FROM FavoriteBook WHERE userId = :userId")
    fun observeFavorites(userId: String): Flow<List<FavoriteBookEntity>>

    // Preferred voiceovers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferredVoiceover(preferred: PreferredVoiceoverEntity)

    @Query("SELECT * FROM PreferredVoiceover WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getPreferredVoiceover(userId: String, bookId: String): PreferredVoiceoverEntity?

    @Query("SELECT * FROM PreferredVoiceover WHERE userId = :userId")
    suspend fun getAllPreferredVoiceovers(userId: String): List<PreferredVoiceoverEntity>

    // Playback progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackProgress(progress: PlaybackProgressEntity)

    @Query("SELECT * FROM PlaybackProgress WHERE userId = :userId AND bookId = :bookId AND voiceoverId = :voiceoverId LIMIT 1")
    suspend fun getPlaybackProgress(userId: String, bookId: String, voiceoverId: String): PlaybackProgressEntity?

    @Query("SELECT * FROM PlaybackProgress WHERE userId = :userId")
    suspend fun getPlaybackProgressForUser(userId: String): List<PlaybackProgressEntity>

    @Query("SELECT * FROM PlaybackProgress WHERE userId = :userId AND bookId = :bookId")
    suspend fun getPlaybackProgressForBook(userId: String, bookId: String): List<PlaybackProgressEntity>

}