package com.vako.data.repository

import com.vako.data.db.dao.UserDao
import com.vako.data.db.entity.user.FavoriteBookEntity
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.UserEntity
import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.UserRepository
import com.vako.domain.user.model.BookVoiceover
import com.vako.domain.user.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

// TODO: Redo and add detailed user entity
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getCurrentUser(): User {
        var userEntity = userDao.getCurrentUser()
        if (userEntity == null) {
            // create a default local user
            userEntity = UserEntity(
                id = UUID.randomUUID().toString(),
                isCurrent = true,
                displayName = "User"
            )
            userDao.insertUser(userEntity)
        }

        return fulfilledUser(userEntity)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<User?> {
        return userDao.observeCurrentUser()
            .flatMapLatest { userEntity ->
                if (userEntity != null) {
                    userDao.observeFavorites(userEntity.id)
                        .map { fulfilledUser(userEntity) }
                } else {
                    flowOf(null)
                }
            }
    }

    suspend fun fulfilledUser(userEntity: UserEntity): User {
        val userId = userEntity.id

        // favorites
        val favoriteIds = userDao.getFavoriteBookIds(userId).toSet()

        // preferred voiceovers
        val preferredEntities = userDao.getAllPreferredVoiceovers(userId)
        val preferredMap = mutableMapOf<String, String>()
        preferredEntities.forEach { pref ->
            preferredMap[pref.bookId] = pref.voiceoverId
        }

        // playback progress
        val progressEntities = userDao.getPlaybackProgressForUser(userId)
        val playbackMap = mutableMapOf<BookVoiceover, PlaybackProgress>()
        progressEntities.forEach { p ->
            playbackMap[BookVoiceover(p.bookId, p.voiceoverId)] = PlaybackProgress(
                positionMs = p.positionMs,
                trackIndex = p.trackIndex
            )
        }

        return User(
            id = userId,
            displayName = userEntity.displayName,
            favoriteBookIds = favoriteIds,
            preferredVoiceovers = preferredMap,
            playbackProgress = playbackMap
        )
    }

    override suspend fun savePlaybackProgress(
        bookId: String,
        voiceoverId: String,
        progress: PlaybackProgress
    ) {
        // ensure user exists
        var userEntity = userDao.getCurrentUser()
        if (userEntity == null) {
            userEntity = UserEntity(
                id = UUID.randomUUID().toString(),
                isCurrent = true,
                displayName = "User"
            )
            userDao.insertUser(userEntity)
        }
        val userId = userEntity.id

        val entity = PlaybackProgressEntity(
            userId = userId,
            bookId = bookId,
            voiceoverId = voiceoverId,
            trackIndex = progress.trackIndex,
            positionMs = progress.positionMs
        )

        userDao.savePlaybackProgress(entity)
    }

    override suspend fun toggleIsFavoriteBook(bookId: String): Boolean {
        // ensure user exists
        var userEntity = userDao.getCurrentUser()
        if (userEntity == null) {
            userEntity = UserEntity(
                id = UUID.randomUUID().toString(),
                isCurrent = true,
                displayName = "User"
            )
            userDao.insertUser(userEntity)
        }
        val userId = userEntity.id

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
    }
}