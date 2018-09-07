package me.jy.danggi.common.player

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class PlayerEventListener(val player : MemoPlayer) : Player.DefaultEventListener() {

    override fun onPlayerError(e: ExoPlaybackException?) {
        e?.printStackTrace()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            player.hideController()
        }
    }
}