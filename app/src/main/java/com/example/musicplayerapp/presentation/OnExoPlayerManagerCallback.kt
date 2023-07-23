package com.example.musicplayerapp.presentation

import androidx.media3.common.MediaItem

/*
 To make interaction between ExoPlayerManager and SongPlayer Service
 Which return the result from exoPlayer Manager
 @author Amit Kumar
 */
interface OnExoPlayerManagerCallback {
    fun onIsPlayingChanged(isPlaying:Boolean)
    fun onUpdateProgress(duration:Long,position:Long)
    fun updateUiForPlayingMediaItem(mediaItem:MediaItem?)

    fun currentMediaIndex(pos:Int)
}