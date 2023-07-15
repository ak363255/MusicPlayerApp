package com.example.musicplayerapp.presentation

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import java.util.ArrayList


class PlayerService : Service(),OnExoPlayerManagerCallback {

    private var mNotificationManager: PlayerNotificationManager? = null
    private var exoPlayerManager: ExoPlayerManager? = null
    private val binder = LocalBinder()
    var mCallback: OnPlayerServiceCallback? = null
    var command: String? = null
    var mMediaItems : ArrayList<MediaItem>?= null
    override fun onCreate() {
        super.onCreate()
        exoPlayerManager = ExoPlayerManager(this, this@PlayerService)
        mNotificationManager = PlayerNotificationManager(this)
        mNotificationManager?.createMediaNotification()
    }


    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so clients can call public methods
        val service: PlayerService
            get() = this@PlayerService
    }
    fun subscribeToSongPlayerUpdates() {
        /* Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        * ensure this Service can be promoted to a foreground service.
        * */
        ContextCompat.startForegroundService(
            applicationContext,
            Intent(this, PlayerService::class.java)
        )
    }

    fun addListener(callback: OnPlayerServiceCallback) {
        mCallback = callback
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        val action = intent?.action
        command = intent?.getStringExtra(CMD_NAME)
        if (ACTION_CMD == action && CMD_PAUSE == command) {
            exoPlayerManager?.pause()
        }
        return binder
    }
    fun toggle() {
        exoPlayerManager?.toggle()
    }
    fun seekTo(position: Long) {
        exoPlayerManager?.seekTo(position)
    }

    fun onShuffle(isShuffle: Boolean) {
        exoPlayerManager?.onShuffleModeEnabledChanged(isShuffle)
    }

    fun onRepeat(isRepeat: Boolean) {
        exoPlayerManager?.onRepeatModeChanged(if (isRepeat) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ONE)
    }


    override fun onIsPlayingChanged(isPlaying: Boolean) {
        mNotificationManager?.generateNotification(isPlaying)
        mCallback?.onIsPlayingChanged(isPlaying)
    }

    override fun onUpdateProgress(duration: Long, position: Long) {
        mCallback?.updateSongProgress(duration, position)
    }

    private fun unsubscribeToSongPlayerUpdates() {
        removeListener()
    }

    fun removeListener() {
        mCallback = null
    }


    override fun updateUiForPlayingMediaItem(mediaItem: MediaItem?) {
        mCallback?.updateUiForPlayingMediaItem(mediaItem)
    }

    fun stop() {
        exoPlayerManager?.stop()
        stopForeground(true)
        stopSelf()
        mNotificationManager = null
    }

    fun skipToNext() {
        exoPlayerManager?.skipToPosition(NEXT)
    }

    fun skipToPrevious() {
        exoPlayerManager?.skipToPosition(PREVIOUS)
    }

    fun pause() {
        exoPlayerManager?.pause()
    }

    fun play() {
        exoPlayerManager?.play()
    }

    fun play(mediaItems: ArrayList<MediaItem>?, mediaItem: MediaItem?) {
        mMediaItems = mediaItems
        exoPlayerManager?.let {
            mediaItem?.let { mediaItem -> it.play(mediaItem) }
            mediaItems?.let { mediaItems ->
                it.setPlayList(mediaItems)
            }
        }
    }


    fun getCurrentMediaItem():MediaItem?{
        return exoPlayerManager?.getCurrentMediaItem()
    }

    companion object {
        const val ACTION_CMD = "app.ACTION_CMD"
        const val CMD_NAME = "CMD_NAME"
        const val CMD_PAUSE = "CMD_PAUSE"
        const val NEXT = 1L
        const val PREVIOUS = -1L
    }

}