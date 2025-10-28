package com.vako.data.repository

import com.vako.data.db.dao.UserDao
import com.vako.data.db.entity.user.PlaybackProgressEntity
import com.vako.data.db.entity.user.UserEntity
import com.vako.domain.player.model.PlaybackProgress
import com.vako.domain.user.UserRepository
import com.vako.domain.user.model.BookVoiceover
import com.vako.domain.user.model.User
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getCurrentUser(): User = runBlocking {
        var userEntity = userDao.getCurrentUser()
        if (userEntity == null) {
            // create a default local user
            userEntity = UserEntity(id = UUID.randomUUID().toString(), isCurrent = true, displayName = "User")
            userDao.insertUser(userEntity)
        }

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

        User(
            id = userId,
            displayName = userEntity.displayName,
            favoriteBookIds = favoriteIds,
            preferredVoiceovers = preferredMap,
            playbackProgress = playbackMap
        )
    }

    override suspend fun savePlaybackProgress(bookId: String, voiceoverId: String, progress: PlaybackProgress) {
        // ensure user exists
        var userEntity = userDao.getCurrentUser()
        if (userEntity == null) {
            userEntity = UserEntity(id = UUID.randomUUID().toString(), isCurrent = true, displayName = "User")
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
}