package com.vako.domain.player.usecases

import com.vako.domain.player.PlayerGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservePlayerStateUseCase @Inject constructor(
    val player: PlayerGateway
) {
    operator fun invoke() = player.observePlayerState()
}