package com.vako.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vako.data.db.entity.user.FavoriteBookEntity
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.PreferredVoiceoverEntity
import com.vako.data.db.entity.user.UserEntity
import com.vako.data.db.entity.user.detailed.UserWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // User
    @Query("SELECT * FROM `User` WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String): UserWithDetails?

    @Query("UPDATE `User` SET isCurrent = 0")
    suspend fun clearCurrent()

    @Query("UPDATE `User` SET isCurrent = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)

    @Transaction
    suspend fun updateCurrentUser(userId: String) {
        clearCurrent()
        setCurrentUser(userId)
    }

    @Query("SELECT * FROM `User` WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserWithDetails?

    @Transaction
    @Query("SELECT * FROM `User` WHERE isCurrent = 1 LIMIT 1")
    fun observeCurrentUser(): Flow<UserWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteBookEntity)

    @Query("DELETE FROM FavoriteBook WHERE userId = :userId AND bookId = :bookId")
    suspend fun removeFavorite(userId: String, bookId: String)

    // Preferred voiceovers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferredVoiceover(preferred: PreferredVoiceoverEntity)

    @Query("DELETE FROM PreferredVoiceover " +
            "WHERE userId = :userId AND bookId = :bookId AND voiceoverId = :voiceoverId"
    )
    suspend fun removePreferredVoiceover(
        userId: String,
        bookId: String,
        voiceoverId: String
    )

    // Playback progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackProgress(progress: PlaybackProgressEntity)
}