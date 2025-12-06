package com.vako.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// TODO: Redo and add detailed user entity
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val auth: FirebaseAuth
) : UserRepository {
    override suspend fun signInAnonymously() {
        val result = auth.signInAnonymously().await()
        val firebaseUser = result.user ?: throw IllegalStateException("User null")

        val uid = firebaseUser.uid
        createUserIfNotExists(uid, "")
        userDao.updateCurrentUser(firebaseUser.uid)
        Log.e("asd fa user uid",uid)
    }

    override suspend fun signInViaGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw IllegalStateException("User null")
        createUserIfNotExists(firebaseUser.uid, firebaseUser.displayName.toString())
        userDao.updateCurrentUser(firebaseUser.uid)
        Log.e("asd fa user uid google",firebaseUser.uid)
    }

    override suspend fun linkWithCredential(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.currentUser?.linkWithCredential(credential)?.await()
    }

    private suspend fun createUserIfNotExists(id: String, displayName: String): User {
        val existingUser = userDao.getUser(id)
        if (existingUser == null) {
            val createdUser = UserEntity(
                id = id,
                isCurrent = false,
                displayName = displayName
            )
            userDao.insertUser(createdUser)
            return createdUser.toDomain()
        }

        return existingUser.toDomain()
    }

    // TODO: Add checking logout
    override suspend fun getCurrentUser(): User? {
        val userWithDetails = userDao.getCurrentUser()
        return userWithDetails?.toDomain()
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

            if (userEntity.favoriteBooks.map { it.bookId }.contains(bookId)) {
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