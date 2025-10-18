package com.vako.data.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.vako.data.player.AudioBookPlayer
import com.vako.domain.player.PlayerGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
    ): Player = ExoPlayer.Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()

    @Provides
    @Singleton
    fun providePlayerGateway(impl: AudioBookPlayer): PlayerGateway = impl
}