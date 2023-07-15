package com.example.musicplayerapp.presentation

import androidx.media3.common.MediaItem


/**
 * To make an interaction between [SongPlayerService] & [BaseSongPlayerActivity]
 *
 * @author ZARA
 * */
interface OnPlayerServiceCallback {
    fun updateSongData(mediaItem: MediaItem)
    fun updateSongProgress(duration: Long, position: Long)
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun updateUiForPlayingMediaItem(mediaItem: MediaItem?)
}