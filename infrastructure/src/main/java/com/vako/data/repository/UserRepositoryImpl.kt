package com.vako.data.repository

import com.vako.data.db.dao.UserDao
import com.vako.data.db.entity.user.FavoriteBookEntity
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.PreferredVoiceoverEntity
import com.vako.data.db.entity.user.UserEntity
import com.vako.data.mapper.user.toDomain
import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.UserRepository
import com.vako.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Redo and add detailed user entity
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getCurrentUser(): User {
        val userWithDetails = userDao.getCurrentUser()
        if (userWithDetails == null) {
            // create a default local user
            val user = UserEntity(
                id = UUID.randomUUID().toString(),
                isCurrent = true,
                displayName = "User"
            )
            userDao.insertUser(user)
            return user.toDomain()
        }

        return userWithDetails.toDomain()

    }

    override fun observeCurrentUser(): Flow<User?> = flow {
        userDao.observeCurrentUser().collect { userWithDetails ->
            if (userWithDetails != null) {
                emit(userWithDetails.toDomain())
            } else {
                emit(userWithDetails)
            }
        }
    }

    override suspend fun savePlaybackProgress(
        bookId: String,
        voiceoverId: String,
        progress: PlaybackProgress
    ) {
        val userEntity = userDao.getCurrentUser()

        if (userEntity != null) {
            val userId = userEntity.user.id

            val entity = PlaybackProgressEntity(
                userId = userId,
                bookId = bookId,
                voiceoverId = voiceoverId,
                trackIndex = progress.trackIndex,
                positionMs = progress.positionMs
            )

            userDao.savePlaybackProgress(entity)
        }
    }

    override suspend fun savePreferredVoiceover(
        userId: String,
        bookId: String,
        voiceoverId: String
    ) {
        userDao.insertPreferredVoiceover(
            PreferredVoiceoverEntity(
                userId = userId,
                bookId = bookId,
                voiceoverId = voiceoverId,
                selectedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removePreferredVoiceover(
        userId: String,
        bookId: String,
        voiceoverId: String
    ) {
        userDao.removePreferredVoiceover(
            userId = userId,
            bookId = bookId,
            voiceoverId = voiceoverId
        )
    }

    override suspend fun toggleIsFavoriteBook(bookId: String): Boolean {
        val userEntity = userDao.getCurrentUser()
        if (userEntity != null) {
            val userId = userEntity.user.id

            if (bookId in getCurrentUser().favoriteBookIds) {
                userDao.removeFavorite(userId, bookId)
                return false
            } else {
                val entity = FavoriteBookEntity(
                    userId = userId,
                    bookId = bookId,
                )

                userDao.insertFavorite(entity)
                return true
            }
        } else {
            throw IllegalStateException("User don`t exit")
        }
    }
}