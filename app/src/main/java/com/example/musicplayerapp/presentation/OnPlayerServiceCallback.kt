package com.example.musicplayerapp.presentation

import androidx.media3.common.MediaItem


/**
 * To make an interaction between [SongPlayerService] & [BaseSongPlayerActivity]
 *
 * @author AMIT
 * */
interface OnPlayerServiceCallback {
    fun updateSongData(mediaItem: MediaItem)
    fun updateSongProgress(duration: Long, position: Long)
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun updateUiForPlayingMediaItem(mediaItem: MediaItem?)

    fun currentMedaiItemIndex(pos:Int)
}