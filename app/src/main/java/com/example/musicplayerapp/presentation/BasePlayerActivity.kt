package com.example.musicplayerapp.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.session.PlaybackState.ACTION_PAUSE
import android.media.session.PlaybackState.ACTION_STOP
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import com.example.musicplayerapp.domain.Utility.orFalse
import com.example.musicplayerapp.domain.model.Song
import com.example.musicplayerapp.presentation.SongPlayerViewModel.Companion.getPlayerViewModelInstance

open class BasePlayerActivity : AppCompatActivity(),OnPlayerServiceCallback {


    private var mService: PlayerService? = null
    private var mBound = false
    private var mMediaItem: MediaItem? = null
    private var mMediaItems: ArrayList<MediaItem>? = null
    private var msg = 0
    val songPlayerViewModel: SongPlayerViewModel = getPlayerViewModelInstance()

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ACTION_PLAY_SONG_IN_LIST -> mService?.play(mMediaItems, mMediaItem)
                ACTION_PAUSE -> mService?.pause()
                ACTION_STOP -> {
                    mService?.stop()
                    songPlayerViewModel.stop()
                }
            }
        }
    }

    fun addToFavoriteSong(song: Song?) = songPlayerViewModel.addFavoriteSong(song)
    fun removeFavoriteSong(id:String?) = songPlayerViewModel.removeFavoriteSong(id)
    fun isFavoriteSong(id:String?) = songPlayerViewModel.isFavoriteSong(id)

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to SongPlayerService, cast the IBinder and get SongPlayerService instance
            val binder = service as PlayerService.LocalBinder
            mService = binder.service
            mBound = true
            mService?.subscribeToSongPlayerUpdates()
            mHandler.sendEmptyMessage(msg)
            mService?.addListener(this@BasePlayerActivity)
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mBound = false
            mService?.removeListener()
            mService = null
        }
    }

    companion object {
        const val PLAY_LIST_KEY = "PLAY_LIST_KEY"
        const val MEDIA_ITEM_KEY = "MEDIA_ITEM_KEY"
        private const val ACTION_PLAY_SONG_IN_LIST = 1
        private const val ACTION_PAUSE = 2
        private const val ACTION_STOP = 3
    }

    fun play(mediaItems: ArrayList<MediaItem>, song: MediaItem?) {
        msg = ACTION_PLAY_SONG_IN_LIST
        mMediaItem = song
        mMediaItems = mediaItems
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }
    private fun bindPlayerService() {
        if (!mBound) bindService(
            Intent(this@BasePlayerActivity, PlayerService::class.java),
            mConnection, Context.BIND_AUTO_CREATE
        )
    }

    fun pause() {
        msg = ACTION_PAUSE
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun stop() {
        msg = ACTION_STOP
        if (mService == null) bindPlayerService()
        else mHandler.sendEmptyMessage(msg)
    }

    fun next() {
        mService?.skipToNext()
    }

    fun previous() {
        mService?.skipToPrevious()
    }

    fun toggle() {
        mService?.toggle()
    }

    fun seekTo(position: Long?) {
        position?.let { nonNullPosition ->
            mService?.seekTo(nonNullPosition)
        }
    }

    fun loadSongsFromExternalStroage(){
        songPlayerViewModel.loadSongsFromExternalStorageStorage(contentResolver)
    }

    fun shuffle() {
        mService?.onShuffle(songPlayerViewModel.isShuffleData.value.orFalse())
        songPlayerViewModel.shuffle()
    }

    fun repeat() {
        mService?.onRepeat(songPlayerViewModel.isRepeatData.value.orFalse())
        songPlayerViewModel.repeat()
    }

    override fun updateSongData(mediaItem: MediaItem) {
        songPlayerViewModel.updateMediaItem(mediaItem)
    }

    override fun updateSongProgress(duration: Long, position: Long) {
        songPlayerViewModel.setChangePosition(position, duration)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        songPlayerViewModel.setPlayingStatus(isPlaying)
    }

    override fun updateUiForPlayingMediaItem(mediaItem: MediaItem?) {
        songPlayerViewModel.updateMediaItem(mediaItem)

    }

    override fun currentMedaiItemIndex(pos: Int) {
        songPlayerViewModel.updateCurrentMediaIndex(pos)
    }

    private fun unbindService() {
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }


    override fun onDestroy() {
        unbindService()
        mService = null
        super.onDestroy()
    }


}